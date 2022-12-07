package com.multimoney.multimoney.presentation.ui.smart.payment.method

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIState
import com.multimoney.multimoney.presentation.ui.smart.payment.method.SmartPaymentMethodViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.method.SmartPaymentMethodViewModel.UIEvent.OnTransferSelected
import com.multimoney.multimoney.presentation.ui.smart.payment.method.SmartPaymentMethodViewModel.UIEvent.OnVisaSelected
import javax.inject.Inject

class SmartPaymentMethodViewModel @Inject constructor() : BaseViewModel(true) {
    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    private fun onNavigateBack() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.SmartPaymentScreen.route
        )
    }

    //TODO Implement transfer navigation
    private fun navigateToTransferScreen() {

    }

    //TODO Implement visa navigation
    private fun navigateToVisaScreen() {

    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnTransferSelected -> navigateToTransferScreen()
            is OnVisaSelected -> navigateToVisaScreen()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnTransferSelected : UIEvent()
        object OnVisaSelected : UIEvent()
    }
}