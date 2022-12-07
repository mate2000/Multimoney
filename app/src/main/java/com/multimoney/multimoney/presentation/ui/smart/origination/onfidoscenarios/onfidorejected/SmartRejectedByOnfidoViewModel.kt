package com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidorejected

import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidorejected.SmartRejectedByOnfidoViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidorejected.SmartRejectedByOnfidoViewModel.UIEvent.OnValidateIdentity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SmartRejectedByOnfidoViewModel @Inject constructor() : BaseViewModel(true) {

    private fun onNavigateToHome() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.ContinueValidatingOnfidoScreen.route // define screen!!
        )
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnNavigateToHome -> onNavigateToHome()
            is OnValidateIdentity ->  onValidateIdentity()
        }
    }

    private fun onValidateIdentity() {
        // TODO: send to validation process
    }

    sealed class UIEvent {
        object OnNavigateToHome : UIEvent()
        object OnValidateIdentity : UIEvent()
    }
}