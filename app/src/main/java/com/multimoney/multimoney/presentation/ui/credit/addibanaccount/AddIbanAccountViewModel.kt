package com.multimoney.multimoney.presentation.ui.credit.addibanaccount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.credit.MutationSaveClientBankAccountUseCase
import com.multimoney.domain.interaction.security.QueryValidateBankAccountUseCase
import com.multimoney.domain.model.security.ValidateAccount
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.credit.addibanaccount.AddIbanAccountViewModel.UIEvent.OnAccountValueChange
import com.multimoney.multimoney.presentation.ui.credit.addibanaccount.AddIbanAccountViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.credit.addibanaccount.AddIbanAccountViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.credit.addibanaccount.AddIbanAccountViewModel.UIEvent.OnEditAccount
import com.multimoney.multimoney.presentation.ui.credit.addibanaccount.AddIbanAccountViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.util.capitalized
import com.multimoney.multimoney.presentation.util.catalog.BankAccountType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class AddIbanAccountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val queryValidateBankAccountUseCase: QueryValidateBankAccountUseCase,
    private val mutationSaveClientBankAccountUseCase: MutationSaveClientBankAccountUseCase
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    // ViewModel variables
    private var user: String?
    private var idBrand: Int?
    private var identification: String?
    private var previousScreen: String?
    private var idClient: Int?
    private var idLoanClient: Int?
    var validateAccount: ValidateAccount? = null

    init {
        user = savedStateHandle[USER]
        idBrand = savedStateHandle[ID_BRAND]
        identification = savedStateHandle[IDENTIFICATION]
        previousScreen = savedStateHandle[PREVIOUS_SCREEN]
        idClient = savedStateHandle[ID_CLIENT]
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT]
    }

    private fun onStart() {
        uiState = uiState.copy(
            title = when (previousScreen) {
                Screen.DisbursementAccountScreen.baseRoute -> R.string.add_iban_account_disbursement_tile
                Screen.SmartPaymentAccountScreenCR.baseRoute -> R.string.payment_account_iban_title
                else -> R.string.add_iban_account_payment_tile
            }
        )
    }

    private fun onAccountValueValueChange(
        bankAccount: String
    ) {
        if (bankAccount.isDigitsOnly() && bankAccount.length <= IBAN_MAX_LENGTH) {
            uiState = uiState.copy(
                isFormValid = false,
                accountNumber = bankAccount,
                accountError = if (bankAccount.length < IBAN_MAX_LENGTH) {
                    Pair(true, R.string.iban_account_error)
                } else {
                    Pair(false, R.string.empty)
                },
                validationError = null
            )
            if (bankAccount.length == IBAN_MAX_LENGTH) {
                validateIbanAccount()
            }
        }
    }

    private fun getQueryType() = when (previousScreen) {
        Screen.SmartPaymentAccountScreenCR.baseRoute -> null
        else -> BankAccountType.Credit.value
    }

    private fun validateIbanAccount() = executeUseCase {
        uiState = uiState.copy(accountInformation = Pair(true, R.string.iban_account_loading))
        queryValidateBankAccountUseCase(
            account = "${Brand.CostaRica.iban}${uiState.accountNumber}",
            identification = identification.orEmpty(),
            queryType = getQueryType(),
            user = user.orEmpty(),
            idBrand = idBrand ?: 0
        ).collectLatest {
            it.onSuccess { account ->
                account?.let { response ->
                    when (response.responseCode) {
                        IS_VALID -> {
                            validateAccount = response
                            uiState = uiState.copy(
                                ibanSuccess = true,
                                isFormValid = true,
                                accountInformation = Pair(false, R.string.empty)
                            )
                        }
                        HAS_ERRORS -> {
                            uiState =
                                uiState.copy(
                                    accountError = Pair(false, R.string.empty),
                                    accountInformation = Pair(false, R.string.empty),
                                    validationError = Pair(true, response.responseMessage.capitalized())
                                )
                        }
                    }
                }
            }
            it.onFailure { error ->
                uiState = uiState.copy(
                    isLoading = false,
                    dialogParameters = DialogParameters(
                        description = error.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }
        }
    }

    private fun resetAccountNumber() {
        uiState = uiState.copy(
            accountNumber = "",
            accountError = Pair(false, R.string.empty),
            ibanSuccess = false,
            isFormValid = false
        )
    }

    private fun onContinueClick() = executeUseCase {
        mutationSaveClientBankAccountUseCase(
            idClient = idClient?.toLong() ?: 0,
            idBank = validateAccount?.bankId ?: 0,
            accountNumber = "${Brand.CostaRica.iban}${uiState.accountNumber}",
            idCurrency = validateAccount?.currency?.getCurrencyFromId()?.id ?: 0,
            idAccountType = null,
            idLoanClient = idLoanClient?.toLong() ?: 0,
            user = user.orEmpty(),
            idBrand = idBrand ?: 0
        ).collectLatest { result ->
            result.onSuccess {
                if (previousScreen == Screen.DisbursementAccountScreen.baseRoute) {
                    navigateBack(Screen.DisbursementAccountScreen.route, true)
                } else {
                    navigateBack(Screen.PaymentAccountScreen.route, true)
                }
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    dialogParameters = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun onBackClick() = if (uiState.ibanSuccess) {
        onEditAccount()
    } else if (previousScreen == Screen.DisbursementAccountScreen.baseRoute) {
        navigateBack(Screen.DisbursementAccountScreen.route, false)
    } else {
        navigateBack(Screen.PaymentAccountScreen.route, false)
    }

    private fun onEditAccount() {
        uiState = uiState.copy(
            dialogParameters = DialogParameters(
                titleResource = R.string.empty,
                descriptionResource = R.string.iban_dialog_message,
                negativeResource = R.string.cancel,
                positiveAction = { resetAccountNumber() },
                isActive = mutableStateOf(true)
            )
        )
    }

    data class UIState(
        val title: Int = R.string.empty,
        val accountNumber: String = "",
        val accountError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val accountInformation: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val isFormValid: Boolean = false,
        val validationError: Pair<Boolean, String>? = Pair(false, ""),
        val ibanSuccess: Boolean = false,
        val isLoading: Boolean = false,
        val dialogParameters: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnStart -> onStart()
            is OnBackClick -> onBackClick()
            is OnContinueClick -> onContinueClick()
            is OnAccountValueChange -> onAccountValueValueChange(uiEvent.account)
            is OnEditAccount -> onEditAccount()
        }
    }

    sealed class UIEvent {
        object OnStart : UIEvent()
        object OnBackClick : UIEvent()
        object OnContinueClick : UIEvent()
        object OnEditAccount : UIEvent()
        data class OnAccountValueChange(val account: String) : UIEvent()
    }

    companion object {
        const val IS_VALID = 0
        const val HAS_ERRORS = 1
        const val IBAN_MAX_LENGTH = 20
    }
}
