package com.multimoney.multimoney.presentation.ui.smart.transfer.iban.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.accountsmart.MutationAddACHAccountUseCase
import com.multimoney.domain.interaction.security.QueryValidateBankAccountUseCase
import com.multimoney.domain.model.accountsmart.IbanAccountID
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.security.ValidateAccount
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SMART_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.credit.addibanaccount.AddIbanAccountViewModel
import com.multimoney.multimoney.presentation.util.capitalized
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SmartTransferTypes
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.isEmailValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SmartTransferRegisterIbanViewModel @Inject constructor(
    private val queryValidateBankAccount: QueryValidateBankAccountUseCase,
    private val mutationAddACHAccount: MutationAddACHAccountUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set
    var closeKeyboard by mutableStateOf(false)

    // Interactions
    private var validateAccount: ValidateAccount? = null

    // Stateless
    private var user: String?
    private var idBrand: Int?
    private var identification: String?
    private var previousScreen: String?
    private var idClient: Int?
    private var smartAccount: SmartAccountID? = null

    init {
        user = savedStateHandle[USER]
        idBrand = savedStateHandle[ID_BRAND]
        identification = savedStateHandle[IDENTIFICATION]
        previousScreen = savedStateHandle[PREVIOUS_SCREEN]
        idClient = savedStateHandle[ID_CLIENT]
        smartAccount = savedStateHandle[SMART_ACCOUNT]
    }

    private fun onAccountValueValueChange(bankAccount: String) {
        val bankAccountFormatted = bankAccount.replace(Brand.CostaRica.iban, "")
        if (bankAccountFormatted.isDigitsOnly() && bankAccountFormatted.length <= IBAN_MAX_LENGTH) {
            uiState = uiState.copy(
                ibanAccountNumber = bankAccountFormatted,
                accountValidationError = null,
                accountError = Pair(false, R.string.empty)
            )
            if (bankAccountFormatted.length == AddIbanAccountViewModel.IBAN_MAX_LENGTH) {
                validateIbanAccount()
            } else {
                isFormValid()
            }
        }
    }

    private fun onAccountValueCompleted() {
        val bankAccount = uiState.ibanAccountNumber.replace(Brand.CostaRica.iban, "")
        uiState = uiState.copy(
            accountError = if (bankAccount.length < AddIbanAccountViewModel.IBAN_MAX_LENGTH) {
                Pair(true, R.string.smart_iban_register_account_length_error)
            } else {
                Pair(false, R.string.empty)
            }
        )
    }

    private fun validateIbanAccount() = executeUseCase {
        uiState = uiState.copy(
            accountInformation = Pair(true, R.string.iban_account_loading),
            validationFinish = false
        )
        // To validate non personal accounts we have to pass the identification as empty
        queryValidateBankAccount(
            account = "${Brand.CostaRica.iban}${uiState.ibanAccountNumber.replace(Brand.CostaRica.iban, "")}",
            identification = "",
            queryType = null,
            user = user.orEmpty(),
            idBrand = idBrand ?: 0
        ).collectLatest {
            it.onSuccess { account ->
                account?.let { response ->
                    when (response.responseCode) {
                        AddIbanAccountViewModel.IS_VALID -> {
                            validateAccount = response
                            uiState = uiState.copy(
                                accountInformation = Pair(false, R.string.empty),
                                validationFinish = true,
                                documentNumber = response.identification.orEmpty(),
                                proprietary = response.name,
                                accountValidationError = Pair(
                                    false,
                                    ""
                                )
                            )
                        }
                        AddIbanAccountViewModel.HAS_ERRORS -> {
                            uiState =
                                uiState.copy(
                                    accountError = Pair(false, R.string.empty),
                                    validationFinish = true,
                                    accountInformation = Pair(false, R.string.empty),
                                    accountValidationError = Pair(
                                        true,
                                        response.responseMessage.capitalized()
                                    )
                                )
                        }
                    }
                }
                isFormValid()
            }.onFailure { error ->
                uiState = uiState.copy(
                    accountInformation = Pair(false, R.string.empty),
                    openDialog = DialogParameters(
                        description = error.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
                onFailure(error)
                isFormValid()
            }
        }
    }

    private fun onAddFavoriteValueChange(isChecked: Boolean) {
        uiState = uiState.copy(
            addFavorite = isChecked,
            favoriteName = if (isChecked) validateAccount?.name ?: "" else ""
        )
    }

    private fun onFavoriteNameValueChange(favoriteName: String) {
        uiState = uiState.copy(favoriteName = favoriteName)
    }

    private fun onEmailNameValueChange(email: String) {
        uiState = uiState.copy(email = email)
        clearUserEmailError()
        isFormValid()
    }

    private fun isUserEmailValid() {
        if (isEmailValid(uiState.email).not()) {
            uiState =
                uiState.copy(userEmailError = Pair(true, R.string.smart_iban_register_email_error))
        }
        isFormValid()
    }

    private fun clearUserEmailError() {
        uiState = uiState.copy(userEmailError = Pair(false, R.string.error_empty))
    }

    private fun isFormValid() {
        uiState = uiState.copy(
            isFormValid = when {
                uiState.ibanAccountNumber.isBlank() -> false
                uiState.accountError.first -> false
                uiState.accountValidationError?.first == true -> false
                isEmailValid(uiState.email).not() -> false
                else -> true
            }
        )
    }
    private fun onContinueButtonClick() = executeUseCase {
        mutationAddACHAccount(
            idBrand = idBrand ?: Brand.CostaRica.id,
            user = user ?: "",
            accountNumber = Brand.CostaRica.iban.plus(uiState.ibanAccountNumber),
            titularName = if (uiState.addFavorite) {
                uiState.favoriteName.ifBlank { validateAccount?.name ?: "" }
            } else {
                validateAccount?.name ?: ""
            },
            isFavorite = uiState.addFavorite,
            typeAccountId = 0,
            identificationNumber = identification ?: "",
            destinationBankId = validateAccount?.bankId ?: 0,
            identificationTypeAccount = validateAccount?.identification?.toIntOrNull() ?: 0,
            destinationCurrencyId = validateAccount?.currency?.toIntOrNull() ?: 0,
            document = uiState.documentNumber,
            description = uiState.favoriteName.ifBlank { validateAccount?.name ?: "" }
        ).collectLatest {
            it.onSuccess {
                uiState = uiState.copy(isLoading = false)
                onNavigateToSendMoney()
            }.onFailure { error ->
                uiState = uiState.copy(isLoading = false)
                onFailure(error)
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun onFailure(error: HttpError) {
        uiState = uiState.copy(
            isLoading = false,
            openDialog = DialogParameters(
                description = error.getError() ?: "",
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onNavigateBack() {
        navigateBack(popTo = Screen.SmartTransferIbanAccountScreen.route, isRestart = false)
    }

    private fun onNavigateToSendMoney() {
        val ibanAccount = encodeData(
            IbanAccountID(
                bank = validateAccount?.bankName,
                clientIdentification = identification,
                sinpeAccount = Brand.CostaRica.iban.plus(uiState.ibanAccountNumber),
                currencyId = validateAccount?.currency?.getCurrencyFromId()?.id ?: 0,
                nameAccount = uiState.favoriteName.ifBlank { validateAccount?.name ?: "" }
            )
        )
        navigateTo(
            "${Screen.SmartTransferAmountScreen.baseRoute}/" +
                "${encodeData(smartAccount)}/$ibanAccount/" +
                "${SmartTransferTypes.SmartToIban.id}/${Screen.SmartTransferRegisterIbanScreen.baseRoute}"
        )
    }

    data class UIState(
        val openDialog: DialogParameters = DialogParameters(),
        var isLoading: Boolean = false,
        val ibanAccountNumber: String = "",
        val accountError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val validationFinish: Boolean = false,
        val proprietary: String = "",
        val accountInformation: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val accountValidationError: Pair<Boolean, String>? = Pair(false, ""),
        val documentNumber: String = "",
        val email: String = "",
        val userEmailError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val favoriteName: String = "",
        val addFavorite: Boolean = false,
        val isFormValid: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnAccountValueChange -> onAccountValueValueChange(uiEvent.accountNumber)
            is UIEvent.OnAddFavoriteValueChange -> onAddFavoriteValueChange(uiEvent.isChecked)
            is UIEvent.OnFavoriteNameValueChange -> onFavoriteNameValueChange(uiEvent.favoriteName)
            is UIEvent.OnEmailNameValueChange -> onEmailNameValueChange(uiEvent.email)
            is UIEvent.OnValidateUserEmail -> isUserEmailValid()
            is UIEvent.OnContinueButtonClick -> onContinueButtonClick()
            is UIEvent.OnAccountValueCompleted -> onAccountValueCompleted()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnValidateUserEmail : UIEvent()
        object OnContinueButtonClick : UIEvent()
        object OnAccountValueCompleted : UIEvent()
        data class OnAccountValueChange(val accountNumber: String) : UIEvent()
        data class OnAddFavoriteValueChange(val isChecked: Boolean) : UIEvent()
        data class OnFavoriteNameValueChange(val favoriteName: String) : UIEvent()
        data class OnEmailNameValueChange(val email: String) : UIEvent()
    }

    companion object {
        const val IBAN_MAX_LENGTH = 20
        const val DEBOUNCE_VALIDATION_TIME = 5000L
    }
}
