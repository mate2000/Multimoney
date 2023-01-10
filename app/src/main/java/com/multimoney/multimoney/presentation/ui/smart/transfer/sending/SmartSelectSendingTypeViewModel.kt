package com.multimoney.multimoney.presentation.ui.smart.transfer.sending

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SMART_IDS
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.smart.transfer.sending.SmartSelectSendingTypeViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SmartSelectSendingTypeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var smartAccount: SmartAccountID? = null
    var user: String = ""
    var identification: String = ""
    var idClient: Int = 0
    var idBrand: Int = 0
    var previousScreen: String = ""

    init {
        smartAccount = savedStateHandle[SMART_IDS]
        user = savedStateHandle[USER] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idClient = savedStateHandle[ID_CLIENT] ?: 0
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
    }

    fun getTitleSmartAccountResource(): Int? {
        val result = when (smartAccount?.currencyID?.getCurrencyFromId()?.value) {
            CurrencyType.Dollar.value -> {
                R.string.payment_select_sending_type_smart_account_dollars
            }
            CurrencyType.Colon.value -> {
                R.string.payment_select_sending_type_smart_account_colones
            }
            else -> {
                0
            }
        }
        return result
    }

    fun getIconSmartAccountResource(): Int? {
        val result = when (smartAccount?.currencyID?.getCurrencyFromId()?.value) {
            CurrencyType.Dollar.value -> {
                R.drawable.ic_sending_dollar
            }
            CurrencyType.Colon.value -> {
                R.drawable.ic_payment_colon
            }
            else -> {
                0
            }
        }
        return result
    }

    private fun onNavigateToHome() =
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = true)

    private fun onNavigateToIBANAccount() {
        navigateTo(
            "${Screen.SmartTransferIbanAccountScreen.baseRoute}/${encodeData(smartAccount)}/$user/$idBrand/$identification/${Screen.SmartSelectSendingTypeScreen.baseRoute}/$idClient"
        )
    }

    private fun onNavigateToSmartAccount() {
        // TODO navigate to HU REV-1431
        emitBaseEvent(BaseEvent.OnShowTbdToastEvent)
    }

    private fun onPermissionPermanentlyDenied() {
        uiState = uiState.copy(
            errorMessageRes = R.string.smart_sac_transfer_contact_permission_denied_message,
            errorButtonTextRes = R.string.got_it,
            isButtonLaunchAction = false
        )
        showRationale(true)
    }

    fun setIfIsLastPermissionRetry(isLastRetry: Boolean) {
        uiState = uiState.copy(isLastPermissionRetry = isLastRetry)
    }

    private fun onPermissionResult(isPermissionGranted: Boolean) {
        if (isPermissionGranted) {
            showRationale(false)
            onNavigateToMyContacts()
        } else if (uiState.isLastPermissionRetry) {
            onPermissionPermanentlyDenied()
        } else {
            showRationale(true)
        }
    }

    private fun showRationale(show: Boolean) {
        uiState = uiState.copy(showErrorScreen = show)
    }

    private fun onNavigateToMyContacts() {
        // TODO navigate to HU REV-1445
        showRationale(false)
        emitBaseEvent(BaseEvent.OnShowTbdToastEvent)
    }

    private fun onNavigateToMyFavorites() {
        // TODO navigate to [tbd]
        emitBaseEvent(BaseEvent.OnShowTbdToastEvent)
    }

    private fun onNavigateToOtherBankAccounts() {
        // TODO navigate to HU REV-1458
        emitBaseEvent(BaseEvent.OnShowTbdToastEvent)
    }

    private fun onNavigateToTransfer365Mobile() {
        // TODO navigate to HU REV-1458
        emitBaseEvent(BaseEvent.OnShowTbdToastEvent)
    }

    private fun onNavigateBack() {
        when (previousScreen) {
            Screen.SmartSelectAccountScreen.baseRoute -> {
                navigateBack(popTo = Screen.SmartSelectAccountScreen.route, isRestart = false)
            }
            else -> navigateBack(popTo = Screen.HomeScreen.route, isRestart = false)
        }
    }

    data class UIState(
        var showErrorScreen: Boolean = false,
        val errorMessageRes: Int = R.string.smart_sac_transfer_contact_rationale_message,
        val errorButtonTextRes: Int = R.string.smart_sac_transfer_contact_rationale_button,
        val isButtonLaunchAction: Boolean = true,
        val isLastPermissionRetry: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnCloseClick -> onNavigateToHome()
            is OnNavigateBack -> onNavigateBack()
            is UIEvent.OnNavigateToMyContacts -> onNavigateToMyContacts()
            is UIEvent.OnSmartAccountSelected -> onNavigateToSmartAccount()
            is UIEvent.OnIBANAccountSelected -> onNavigateToIBANAccount()
            is UIEvent.OnMyFavoritesSelected -> onNavigateToMyFavorites()
            is UIEvent.OnContactPermissionPermanentlyDenied -> onPermissionPermanentlyDenied()
            is UIEvent.OnOtherBankAccountsSelected -> onNavigateToOtherBankAccounts()
            is UIEvent.OnTransfer365MobileSelected -> onNavigateToTransfer365Mobile()
            is UIEvent.OnPermissionResult -> onPermissionResult(uiEvent.isPermissionGranted)
            is UIEvent.OnShowRationale -> showRationale(uiEvent.show)
        }
    }

    sealed class UIEvent {
        object OnCloseClick : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnNavigateToMyContacts : UIEvent()
        object OnSmartAccountSelected : UIEvent()
        object OnIBANAccountSelected : UIEvent()
        object OnMyFavoritesSelected : UIEvent()
        object OnContactPermissionPermanentlyDenied : UIEvent()
        object OnOtherBankAccountsSelected : UIEvent()
        object OnTransfer365MobileSelected : UIEvent()
        data class OnPermissionResult(val isPermissionGranted: Boolean) : UIEvent()
        data class OnShowRationale(val show: Boolean) : UIEvent()
    }

    sealed class BaseEvent {
        object OnShowTbdToastEvent : BaseEvent()
    }
}
