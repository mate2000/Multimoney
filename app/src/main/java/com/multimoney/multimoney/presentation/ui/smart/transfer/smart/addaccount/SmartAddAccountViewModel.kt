package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.addaccount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.accountsmart.MutationUpdateFavoriteSmartUseCase
import com.multimoney.domain.interaction.accountsmart.QuerySmartAccountTypeUseCase
import com.multimoney.domain.model.accountsmart.PhoneSmart
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.accountsmart.SmartAccountType
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SMART_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.TRANSFER_TYPE
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.addaccount.SmartAdd365AccountViewModel
import com.multimoney.multimoney.presentation.util.MAX_SMART_ACCOUNT_DIGITS
import com.multimoney.multimoney.presentation.util.MIN_SMART_ACCOUNT_DIGITS
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SmartTransferTypes
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.isEmailValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SmartAddAccountViewModel @Inject constructor(
    private val querySmartAccountTypeUseCase: QuerySmartAccountTypeUseCase,
    private val mutationUpdateFavoriteSmartUseCase: MutationUpdateFavoriteSmartUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // Stateless
    private var idBrand = savedStateHandle[ID_BRAND] ?: 0
    private var user = savedStateHandle[USER] ?: ""
    private var smartAccount: SmartAccountID? = null
    private var transferType: Int = 0

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        smartAccount = savedStateHandle[SMART_ACCOUNT]
        transferType = savedStateHandle[TRANSFER_TYPE] ?: 0
    }

    private fun getAccountTypes() = executeUseCase {
        querySmartAccountTypeUseCase.invoke(
            idBrand = idBrand,
            user = user
        ).collectLatest { result ->
            result.onLoading { uiState = uiState.copy(isLoading = true) }
            result.onSuccess { types ->
                uiState = uiState.copy(
                    isLoading = false,
                    accountTypeList = types?.typeList ?: listOf()
                )
            }
            result.onFailure {
                onFailure(it)
            }
        }
    }

    private fun onAccountTypeSelected(newType: String) {
        val type = uiState.accountTypeList.find { it?.typeName == newType }
        uiState = uiState.copy(type = type)
        validateForm()
    }

    private fun onAccountNumberChanged(number: String) {
        if (number.isDigitsOnly()) {
            uiState = uiState.copy(accountNumber = number)
        }
    }

    private fun onEmailChanged(newEmail: String) {
        uiState = uiState.copy(email = newEmail)
    }

    private fun onNamesChanged(newNames: String) {
        uiState = uiState.copy(names = newNames)
        validateForm()
    }

    private fun onLastNamesChanged(newLastNames: String) {
        uiState = uiState.copy(lastNames = newLastNames)
        validateForm()
    }

    private fun isUserEmailValid() {
        uiState = if (isEmailValid(uiState.email)) {
            uiState.copy(emailError = Pair(false, R.string.empty))
        } else {
            uiState.copy(emailError = Pair(true, R.string.smart_iban_register_email_error))
        }
        validateForm()
    }

    private fun isAccountNumberValid() {
        uiState = uiState.copy(
            isAccountNumberError = uiState.accountNumber.length !in MIN_SMART_ACCOUNT_DIGITS..MAX_SMART_ACCOUNT_DIGITS
        )
        validateForm()
    }

    private fun onAddFavoriteValueChange(isChecked: Boolean) {
        uiState = uiState.copy(isFavorite = isChecked)
    }

    private fun onNicknameChanged(nickname: String) {
        uiState = uiState.copy(nickname = nickname)
        validateForm()
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

    private fun validateForm() {
        uiState = uiState.copy(
            enableButton = when {
                uiState.type == null -> false
                uiState.isAccountNumberError -> false
                uiState.emailError.first -> false
                uiState.email.isEmpty() -> false
                uiState.accountNumber.isEmpty() -> false
                uiState.names.isEmpty() -> false
                uiState.lastNames.isEmpty() -> false
                else -> true
            }
        )
    }

    private fun onContinueClick() {
        val fullName = "${uiState.names} ${uiState.lastNames}"
        executeUseCase {
            mutationUpdateFavoriteSmartUseCase.invoke(
                idBrand = idBrand,
                user = user,
                idFavorite = null,
                idAccountType = uiState.type?.typeId?.toIntOrNull() ?: 0,
                idCustomer = smartAccount?.customerId ?: 0L,
                accountNumber = uiState.accountNumber,
                accountName = if (uiState.isFavorite) {
                    uiState.nickname.ifEmpty { fullName }
                } else {
                    fullName
                },
                email = uiState.email,
                active = true,
                isFavorite = uiState.isFavorite,
                phoneNumber = null,
                idCurrencyAccount = null,
                identification = ""
            ).collectLatest { result ->
                result.onLoading { uiState = uiState.copy(isLoading = true) }
                result.onSuccess { account ->
                    val registeredAccount = account?.results?.firstOrNull()
                    val currency = if (registeredAccount?.currencyAccount != null) {
                        registeredAccount.currencyAccount.toString()
                    } else {
                        CurrencyType.Dollar.id.toString()
                    }
                    val contactAccount = PhoneSmart(
                        number = registeredAccount?.phoneNumber,
                        titular = registeredAccount?.accountName ?: "${uiState.names} ${uiState.lastNames}",
                        bankName = "",
                        identification = registeredAccount?.identification,
                        accountNumber = registeredAccount?.accountNumber ?: uiState.accountNumber,
                        email = registeredAccount?.email ?: uiState.email,
                        idCurrency = currency,
                        currency = currency.getCurrencyFromId().currency,
                        ibanNumber = ""
                    )
                    uiState = uiState.copy(isLoading = false)
                    navigateTo(
                        "${Screen.MyContactsTransferAmountScreen.baseRoute}/" +
                                "${encodeData(smartAccount)}/${encodeData(contactAccount)}/" +
                                "${SmartTransferTypes.SmartToContact.id}/$idBrand/" +
                                Screen.SmartAddSACAccountScreen.baseRoute
                    )
                }
                result.onFailure { onFailure(it) }
            }
        }
    }

    private fun onNavigateBack() =
        navigateBack(
            popTo = Screen.MyContactsTransferScreen.route,
            isRestart = false
        )

    data class UIState(
        val openDialog: DialogParameters = DialogParameters(),
        val isLoading: Boolean = false,
        val accountTypeList: List<SmartAccountType?> = listOf(),
        val type: SmartAccountType? = null,
        val accountNumber: String = "",
        val isAccountNumberError: Boolean = false,
        val email: String = "",
        val emailError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val enableButton: Boolean = false,
        val names: String = "",
        val lastNames: String = "",
        val isFavorite: Boolean = false,
        val nickname: String = ""
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnAccountTypeSelected -> onAccountTypeSelected(uiEvent.type)
            is UIEvent.OnAccountNumberChanged -> onAccountNumberChanged(uiEvent.number)
            is UIEvent.OnEmailChanged -> onEmailChanged(uiEvent.email)
            is UIEvent.OnValidateUserEmail -> isUserEmailValid()
            is UIEvent.OnContinueClick -> onContinueClick()
            is UIEvent.OnGetAccountTypes -> getAccountTypes()
            is UIEvent.OnValidateAccountNumber -> isAccountNumberValid()
            is UIEvent.OnNamesChanged -> onNamesChanged(uiEvent.names)
            is UIEvent.OnLastNamesChanged -> onLastNamesChanged(uiEvent.lastNames)
            is UIEvent.OnAddFavoriteValueChange -> onAddFavoriteValueChange(uiEvent.isChecked)
            is UIEvent.OnNicknameChanged -> onNicknameChanged(uiEvent.nickname)
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnValidateUserEmail : UIEvent()
        object OnValidateAccountNumber : UIEvent()
        object OnContinueClick : UIEvent()
        object OnGetAccountTypes : UIEvent()
        data class OnAccountTypeSelected(val type: String) : UIEvent()
        data class OnAccountNumberChanged(val number: String) : UIEvent()
        data class OnNamesChanged(val names: String) : UIEvent()
        data class OnLastNamesChanged(val lastNames: String) : UIEvent()
        data class OnEmailChanged(val email: String) : UIEvent()
        data class OnAddFavoriteValueChange(val isChecked: Boolean) : UIEvent()
        data class OnNicknameChanged(val nickname: String) : UIEvent()
    }
}
