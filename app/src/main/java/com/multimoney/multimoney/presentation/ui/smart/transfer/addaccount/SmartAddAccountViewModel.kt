package com.multimoney.multimoney.presentation.ui.smart.transfer.addaccount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.accountsmart.QuerySmartAccountTypeUseCase
import com.multimoney.domain.model.accountsmart.SinpeAccount
import com.multimoney.domain.model.accountsmart.SmartAccountType
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel
import com.multimoney.multimoney.presentation.ui.smart.transfer.register.SmartTransferRegisterIbanViewModel
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.isEmailValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SmartAddAccountViewModel @Inject constructor(
    private val querySmartAccountTypeUseCase: QuerySmartAccountTypeUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    //Stateless
    val idBrand = savedStateHandle[ID_BRAND] ?: 0
    val user = savedStateHandle[USER] ?: ""

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

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

    private fun onAccountTypeSelected(newTypeId: String) {
        uiState = uiState.copy(typeId = newTypeId)
    }

    private fun onAccountNumberChanged(number: String) {
        uiState = uiState.copy(accountNumber = number)
    }

    private fun onEmailChanged(newEmail: String) {
        uiState = uiState.copy(email = newEmail)
    }

    private fun isUserEmailValid() {
        if (isEmailValid(uiState.email).not()) {
            uiState =
                uiState.copy(emailError = Pair(true, R.string.smart_iban_register_email_error))
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

    private fun onContinueClick() {

    }

    private fun onNavigateBack() =
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = true, homeState = HomeState.UNEXPANDED)

    data class UIState(
        val openDialog: DialogParameters = DialogParameters(),
        val isLoading: Boolean = false,
        val accountTypeList: List<SmartAccountType?> = listOf(),
        val typeId: String = "",
        val accountNumber: String = "",
        val isAccountNumberError: Boolean = false,
        val email: String = "",
        val emailError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val enableButton: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnAccountTypeSelected -> onAccountTypeSelected(uiEvent.typeId)
            is UIEvent.OnAccountNumberChanged -> onAccountNumberChanged(uiEvent.number)
            is UIEvent.OnEmailChanged -> onEmailChanged(uiEvent.email)
            is UIEvent.OnValidateUserEmail -> isUserEmailValid()
            is UIEvent.OnContinueClick -> onContinueClick()
            is UIEvent.OnGetAccountTypes -> getAccountTypes()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnValidateUserEmail : UIEvent()
        object OnContinueClick : UIEvent()
        object OnGetAccountTypes : UIEvent()
        data class OnAccountTypeSelected(val typeId: String) : UIEvent()
        data class OnAccountNumberChanged(val number: String) : UIEvent()
        data class OnEmailChanged(val email: String) : UIEvent()
    }
}