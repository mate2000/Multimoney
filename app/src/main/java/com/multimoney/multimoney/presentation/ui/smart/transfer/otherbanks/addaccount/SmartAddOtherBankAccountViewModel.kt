package com.multimoney.multimoney.presentation.ui.smart.transfer.otherbanks.addaccount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SMART_IDS
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.isEmailValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmartAddOtherBankAccountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true){

    //Stateless
    private var idBrand = savedStateHandle[ID_BRAND] ?: 0
    private var user = savedStateHandle[USER] ?: ""
    private var smartAccount: SmartAccountID? = null

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        smartAccount = savedStateHandle[SMART_IDS]
        getListValues()
    }

    private fun getListValues() {
        viewModelScope.launch {
            getDocumentTypes()
            getBanks()
            getAccountTypes()
        }
    }

    private fun getAccountTypes() = executeUseCase {

    }

    private fun getBanks() = executeUseCase {

    }

    private fun getDocumentTypes() = executeUseCase {

    }

    private fun onAccountTypeSelected(newType: String) {
        //val type = uiState.accountTypeList.find { it?.typeName == newType }
        uiState = uiState.copy(type = newType)
        validateForm()
    }

    private fun onAccountNumberChanged(number: String) {
        if (number.isDigitsOnly()) {
            uiState = uiState.copy(accountNumber = number)
        }
    }

    private fun onDocumentChanged(newDocument: String) {
        uiState = uiState.copy(document = newDocument)
    }

    private fun onNamesChanged(newNames: String) {
        uiState = uiState.copy(names = newNames)
        validateForm()
    }

    private fun onLastNamesChanged(newLastNames: String) {
        uiState = uiState.copy(lastNames = newLastNames)
        validateForm()
    }

    private fun isDocumentValid() {
        uiState = if (isEmailValid(uiState.email)) {
            uiState.copy(personalIdError = Pair(false, R.string.empty))
        } else {
            uiState.copy(personalIdError = Pair(true, R.string.smart_iban_register_email_error))
        }
        validateForm()
    }

    private fun isAccountNumberValid() {
        uiState = uiState.copy(
            isAccountNumberError = uiState.accountNumber.length !in MIN_ACCOUNT_DIGITS..MAX_ACCOUNT_DIGITS
        )
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
                uiState.personalIdError.first -> false
                uiState.email.isEmpty() -> false
                uiState.accountNumber.isEmpty() -> false
                uiState.names.isEmpty() -> false
                uiState.lastNames.isEmpty() -> false
                else -> true
            }
        )
    }

    private fun onContinueClick() {

    }

    // Todo change this navigation to go back to Contacts screen rev-1445
    private fun onNavigateBack() =
        navigateBack(
            popTo = Screen.SmartSelectSendingTypeScreen.route,
            isRestart = false
        )

    data class UIState(
        val openDialog: DialogParameters = DialogParameters(),
        val isLoading: Boolean = false,
        val accountTypeList: List<String?> = listOf(),
        val type: String = "",
        val document: String = "",
        val documentFormat: String = "",
        val documentList: List<String> = listOf(),
        val identificationValueType: String = "",
        val accountNumber: String = "",
        val isAccountNumberError: Boolean = false,
        val bank: String = "",
        val bankList: List<String> = listOf(),
        val email: String = "",
        val personalIdError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val enableButton: Boolean = false,
        val names: String = "",
        val lastNames: String = ""
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnDocumentTypeSelected -> onAccountTypeSelected(uiEvent.type)
            is UIEvent.OnAccountNumberChanged -> onAccountNumberChanged(uiEvent.number)
            is UIEvent.OnAccountTypeSelected -> onAccountTypeSelected(uiEvent.type)
            is UIEvent.OnBankSelected -> onAccountTypeSelected(uiEvent.bank)
            is UIEvent.OnValidateDocument -> isDocumentValid()
            is UIEvent.OnContinueClick -> onContinueClick()
            is UIEvent.OnGetListValues -> getAccountTypes()
            is UIEvent.OnValidateAccountNumber -> isAccountNumberValid()
            is UIEvent.OnNamesChanged -> onNamesChanged(uiEvent.names)
            is UIEvent.OnLastNamesChanged -> onLastNamesChanged(uiEvent.lastNames)
            is UIEvent.OnDocumentChanged -> onDocumentChanged(uiEvent.document)
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnValidateDocument : UIEvent()
        object OnValidateAccountNumber : UIEvent()
        object OnContinueClick : UIEvent()
        object OnGetListValues : UIEvent()
        data class OnDocumentTypeSelected(val type: String) : UIEvent()
        data class OnBankSelected(val bank: String) : UIEvent()
        data class OnAccountTypeSelected(val type: String) : UIEvent()
        data class OnAccountNumberChanged(val number: String) : UIEvent()
        data class OnNamesChanged(val names: String) : UIEvent()
        data class OnLastNamesChanged(val lastNames: String) : UIEvent()
        data class OnDocumentChanged(val document: String) : UIEvent()
    }

    companion object {
        const val MIN_ACCOUNT_DIGITS = 9
        const val MAX_ACCOUNT_DIGITS = 16
    }
}