package com.multimoney.multimoney.presentation.ui.smart.transfer.addaccount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.accountsmart.MutationUpdateFavoriteSmartUseCase
import com.multimoney.domain.interaction.accountsmart.QuerySmartAccountTypeUseCase
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.accountsmart.SmartAccountType
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
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

    //Stateless
    private var idBrand = savedStateHandle[ID_BRAND] ?: 0
    private var user = savedStateHandle[USER] ?: ""
    private var idClient: String? = ""

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idClient = savedStateHandle[ID_CLIENT] ?: ""
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
            validateForm()
        }
    }

    private fun onEmailChanged(newEmail: String) {
        uiState = uiState.copy(email = newEmail)
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
            isAccountNumberError = uiState.accountNumber.length !in MIN_ACCOUNT_DIGITS..MAX_ACCOUNT_DIGITS
        )
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
            enableButton = uiState.type != null && uiState.isAccountNumberError.not()
                    && uiState.emailError.first.not() && uiState.email.isNotEmpty() && uiState.accountNumber.isNotEmpty()
        )
    }


    private fun onContinueClick() {
        executeUseCase {
            mutationUpdateFavoriteSmartUseCase.invoke(
                idBrand = idBrand,
                user = user,
                idFavorite = null,
                idAccountType = uiState.type?.typeId?.toIntOrNull() ?: 0,
                idCustomer = idClient?.toLongOrNull() ?: 0L,
                accountNumber = uiState.accountNumber,
                accountName = "",
                email = uiState.email,
                active = true,
                phoneNumber = "",
                idCurrencyAccount = CurrencyType.Dollar.id
            ).collectLatest { result ->
                result.onLoading { uiState = uiState.copy(isLoading = true) }
                result.onSuccess { account ->
                    val registeredAccount = account?.results?.first()
                    val smartAccount = SmartAccountID(
                        accountNumber = registeredAccount?.accountNumber,
                        currencyID = registeredAccount?.idCurrencyAccount,
                        tokenAccount = null
                    )
                    uiState = uiState.copy(isLoading = false)
                    // Todo: change last parameter to contacts screen
                    navigateTo(
                        "${Screen.OwnTransferAmountScreen.baseRoute}/${
                            encodeData(
                                smartAccount
                            )
                        }/${Screen.SmartAddSACAccountScreen.baseRoute}"
                    )
                }
                result.onFailure { onFailure(it) }
            }
        }
    }

    private fun onNavigateBack() =
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true,
            homeState = HomeState.UNEXPANDED
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
        val enableButton: Boolean = false
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
        data class OnEmailChanged(val email: String) : UIEvent()
    }

    companion object {
        const val MIN_ACCOUNT_DIGITS = 9
        const val MAX_ACCOUNT_DIGITS = 9
    }
}