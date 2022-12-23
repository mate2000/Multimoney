package com.multimoney.multimoney.presentation.ui.smart.payment.sending

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.ACCOUNT_TOKEN
import com.multimoney.multimoney.presentation.navigation.navgraph.CURRENCY_CODE
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.USER_SMART_ACCOUNT
import com.multimoney.multimoney.presentation.ui.smart.payment.sending.SmartSelectSendingTypeViewModel.UIEvent.OnNavigateBack
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SmartSelectSendingTypeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var pkUser: String = ""
    var identification: String = ""
    var codeCurrency: String? = ""
    var idClient: Int = 0
    var idBrand: Int = 0
    var tokenNumber: String? = ""
    var accountNumber: String? = ""

    init {
        codeCurrency = savedStateHandle[CURRENCY_CODE] ?: ""
        pkUser = savedStateHandle[PK_USER] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        tokenNumber = savedStateHandle[ACCOUNT_TOKEN]
        codeCurrency = savedStateHandle[CURRENCY_CODE]
        accountNumber = savedStateHandle[USER_SMART_ACCOUNT]
    }


    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is UIEvent.OnMyContactsSelected -> onNavigateToMyContacts()
            is UIEvent.OnSmartAccountSelected -> onNavigateToSmartAccount()
            is UIEvent.OnIBANAccountSelected -> onNavigateToIBANAccount()
        }
    }

    fun getTitleSmartAccountResource() : Int? {
        val result = if (codeCurrency?.uppercase() == DOLARES ) {
            R.string.payment_select_sending_type_smart_account_dollars
       } else if ( codeCurrency?.uppercase() == COLONES ) {
           R.string.payment_select_sending_type_smart_account_colones
       } else {
           0
       }
        return result
    }

    fun getIconSmartAccountResource() : Int? {
        val result = if (codeCurrency?.uppercase() == DOLARES ) {
            R.drawable.ic_sending_dollar
        } else if ( codeCurrency?.uppercase() == COLONES ) {
            R.drawable.ic_payment_colon
        } else {
            0
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
    data class UIState(
        val idBrand: String = ""
    )

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnMyContactsSelected : UIEvent()
        object OnSmartAccountSelected : UIEvent()
        object OnIBANAccountSelected : UIEvent()
    }

    companion object {
        const val COLONES = "COLONES"
        const val DOLARES = "DÓLARES"
    }
}