package com.multimoney.multimoney.presentation.ui.smart.payment.accounts

import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountViewModel.UIEvent.OnColonSelected
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountViewModel.UIEvent.OnDollarSelected
import javax.inject.Inject

class SmartPaymentAccountViewModel @Inject constructor() :
    BaseViewModel(true) {

    private fun onNavigateBack() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.SmartPaymentAccountScreen.route
        )
    }

    //TODO Implement Colon navigation
    private fun navigateToColonPaymentScreen() {

    }

    //TODO Implement Dollar navigation
    private fun navigateToDollarPaymentScreen() {

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