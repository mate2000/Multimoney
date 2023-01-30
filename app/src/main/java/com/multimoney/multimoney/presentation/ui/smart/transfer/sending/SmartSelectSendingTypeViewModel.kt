package com.multimoney.multimoney.presentation.ui.smart.transfer.sending

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.model.accountsmart.RelatedContact
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SECOND_SMART_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.SMART_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.smart.transfer.sending.SmartSelectSendingTypeViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.SmartTransferTypes
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmartSelectSendingTypeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var selectedSmartAccount: SmartAccountID? = null
    var secondSmartAccount: SmartAccountID? = null
    var user: String = ""
    var identification: String = ""
    var idClient: Int = 0
    var idBrand: Int = 0
    var previousScreen: String = ""

    init {
        selectedSmartAccount = savedStateHandle[SMART_ACCOUNT]
        secondSmartAccount = savedStateHandle[SECOND_SMART_ACCOUNT]
        user = savedStateHandle[USER] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idClient = savedStateHandle[ID_CLIENT] ?: 0
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
    }

    fun getTitleAndIconSmartAccountResources(): Pair<Int, Int?> {
        val result = when (selectedSmartAccount?.currencyID?.getCurrencyFromId()?.value) {
            CurrencyType.Dollar.value -> {
                Pair(
                    R.string.payment_select_sending_type_smart_account_colones,
                    R.drawable.ic_payment_colon
                )
            }
            CurrencyType.Colon.value -> {
                Pair(
                    R.string.payment_select_sending_type_smart_account_dollars,
                    R.drawable.ic_sending_dollar
                )
            }
            else -> {
                Pair(R.string.empty, 0)
            }
        }
        return result
    }

    private fun onNavigateToHome() =
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = true)

    private fun onNavigateToIBANAccount() {
        navigateTo(
            "${Screen.SmartTransferIbanAccountScreen.baseRoute}/${encodeData(selectedSmartAccount)}/$user/$idBrand/$identification/${Screen.SmartSelectSendingTypeScreen.baseRoute}/$idClient"
        )
    }

    private fun onNavigateToSmartAccount() {
        navigateTo(
            "${Screen.OwnTransferAmountScreen.baseRoute}/" +
                    "${encodeData(selectedSmartAccount)}/${encodeData(secondSmartAccount)}/" +
                    "${SmartTransferTypes.SmartToSmart.id}"
        )
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

    fun getPermissionState() {
        viewModelScope.launch {
            uiState = uiState.copy(
                isContactPermissionAlreadyRequested = dataStorePreferences.isContactPermissionRequested()
                    .first()
            )
        }
    }

    private fun onPermissionResult(isPermissionGranted: Boolean, numbersList: List<String>?) {
        viewModelScope.launch {
            dataStorePreferences.isContactPermissionRequested(true)
            uiState = uiState.copy(
                isContactPermissionAlreadyRequested = dataStorePreferences.isContactPermissionRequested()
                    .first()
            )
            if (isPermissionGranted) {
                showRationale(false)
                onNavigateToMyContacts(numbersList ?: listOf())
            } else if (uiState.isLastPermissionRetry) {
                onPermissionPermanentlyDenied()
            } else {
                showRationale(true)
            }
        }
    }

    private fun showRationale(show: Boolean) {
        uiState = uiState.copy(showErrorScreen = show)
    }

    private fun onNavigateToMyContacts(numbers: List<String>) {
        showRationale(false)
        val contacts = encodeData(
            numbers.map {
                RelatedContact(it)
            }
        )
        navigateTo(
            "${Screen.MyContactsTransferScreen.baseRoute}/$user/$idBrand/$contacts/${
            encodeData(selectedSmartAccount)
            }"
        )
    }

    private fun onNavigateToMyFavorites() {
        // TODO navigate to [tbd]
        emitBaseEvent(BaseEvent.OnShowTbdToastEvent)
    }

    private fun onNavigateToOtherBankAccounts() {
        navigateTo(
            "${Screen.SmartOtherBanksAccountScreen.baseRoute}/$idBrand/$user/${
                encodeData(
                    selectedSmartAccount
                )
            }/${SmartTransferTypes.SmartToOtherBank.id}"
        )
    }

    private fun onNavigateToTransfer365Mobile() {
        navigateTo(
            "${Screen.SmartOtherBanksAccountScreen.baseRoute}/$idBrand/$user/${
                encodeData(
                    selectedSmartAccount
                )
            }/${SmartTransferTypes.SmartToMobile.id}"
        )
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
        val isLastPermissionRetry: Boolean = false,
        val isContactPermissionAlreadyRequested: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnCloseClick -> onNavigateToHome()
            is OnNavigateBack -> onNavigateBack()
            is UIEvent.OnNavigateToMyContacts -> onNavigateToMyContacts(uiEvent.numbersList)
            is UIEvent.OnSmartAccountSelected -> onNavigateToSmartAccount()
            is UIEvent.OnIBANAccountSelected -> onNavigateToIBANAccount()
            is UIEvent.OnMyFavoritesSelected -> onNavigateToMyFavorites()
            is UIEvent.OnContactPermissionPermanentlyDenied -> onPermissionPermanentlyDenied()
            is UIEvent.OnOtherBankAccountsSelected -> onNavigateToOtherBankAccounts()
            is UIEvent.OnTransfer365MobileSelected -> onNavigateToTransfer365Mobile()
            is UIEvent.OnPermissionResult -> onPermissionResult(
                uiEvent.isPermissionGranted,
                uiEvent.numbersList
            )
            is UIEvent.OnShowRationale -> showRationale(uiEvent.show)
        }
    }

    sealed class UIEvent {
        object OnCloseClick : UIEvent()
        object OnNavigateBack : UIEvent()
        data class OnNavigateToMyContacts(val numbersList: List<String>) : UIEvent()
        object OnSmartAccountSelected : UIEvent()
        object OnIBANAccountSelected : UIEvent()
        object OnMyFavoritesSelected : UIEvent()
        object OnContactPermissionPermanentlyDenied : UIEvent()
        object OnOtherBankAccountsSelected : UIEvent()
        object OnTransfer365MobileSelected : UIEvent()
        data class OnPermissionResult(
            val isPermissionGranted: Boolean,
            val numbersList: List<String>?
        ) : UIEvent()

        data class OnShowRationale(val show: Boolean) : UIEvent()
    }

    sealed class BaseEvent {
        object OnShowTbdToastEvent : BaseEvent()
    }
}
