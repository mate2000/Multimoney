package com.multimoney.multimoney.presentation.ui.smart.payment.accounts

import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountViewModel.UIEvent.OnColonSelected
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountViewModel.UIEvent.OnDollarSelected
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountViewModel.UIEvent.OnNavigateBack
import javax.inject.Inject

class SmartPaymentAccountViewModel @Inject constructor(savedStateHandle: SavedStateHandle) :
    BaseViewModel(true) {

    // Stateless
    private var user: String = ""
    private var idBrand: String = ""
    private var idClient: String = ""
    private var idLoanClient: String = ""
    private var identification: String? = null

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: ""
        idClient = savedStateHandle[ID_CLIENT] ?: ""
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
    }

    private fun onNavigateBack() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.SmartPaymentAccountScreen.route
        )
    }

    //TODO Implement Colon navigation
    private fun navigateToColonPaymentScreen() {
        navigateToAddIbanAccount()
    }

    private fun navigateToDollarPaymentScreen() {
        navigateToAddIbanAccount()
    }

    private fun navigateToAddIbanAccount() {
        navigateTo(
            route = "${Screen.AddIbanAccountScreen.baseRoute}/$user/$idBrand/$identification/${Screen.PaymentAccountScreen.baseRoute}/$idClient/$idLoanClient"
        )
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnColonSelected -> navigateToColonPaymentScreen()
            is OnDollarSelected -> navigateToDollarPaymentScreen()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnColonSelected : UIEvent()
        object OnDollarSelected : UIEvent()
    }
}