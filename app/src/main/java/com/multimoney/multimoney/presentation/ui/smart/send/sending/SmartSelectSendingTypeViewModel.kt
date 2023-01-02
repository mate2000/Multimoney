package com.multimoney.multimoney.presentation.ui.smart.send.sending

import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SMART_IDS
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.ui.smart.send.sending.SmartSelectSendingTypeViewModel.UIEvent.OnIBANAccountSelected
import com.multimoney.multimoney.presentation.ui.smart.send.sending.SmartSelectSendingTypeViewModel.UIEvent.OnMyContactsSelected
import com.multimoney.multimoney.presentation.ui.smart.send.sending.SmartSelectSendingTypeViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.send.sending.SmartSelectSendingTypeViewModel.UIEvent.OnSmartAccountSelected
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SmartSelectSendingTypeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // Stateless
    var smartAccount: SmartAccountID? = null
    var pkUser: String = ""
    var identification: String = ""
    var idClient: Int = 0
    var idBrand: Int = 0

    init {
        smartAccount = savedStateHandle[SMART_IDS]
        pkUser = savedStateHandle[PK_USER] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnMyContactsSelected -> onNavigateToMyContacts()
            is OnSmartAccountSelected -> onNavigateToSmartAccount()
            is OnIBANAccountSelected -> onNavigateToIBANAccount()
        }
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

    private fun onNavigateToIBANAccount() {
        // TODO navigate to HU REV-1423
    }

    private fun onNavigateToSmartAccount() {
        // TODO navigate to HU REV-1431
    }

    private fun onNavigateToMyContacts() {
        // TODO navigate to HU REV-1445
    }

    private fun onNavigateBack() {
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = false
        )
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnMyContactsSelected : UIEvent()
        object OnSmartAccountSelected : UIEvent()
        object OnIBANAccountSelected : UIEvent()
    }
}
