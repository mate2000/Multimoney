package com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidoapproved

import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidoapproved.ApprovedByOnfidoViewModel.UIEvent.OnNavigateToHome
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ApprovedByOnfidoViewModel @Inject constructor() : BaseViewModel(true) {

    private fun onNavigateToHome() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.ContinueValidatingOnfidoScreen.route // define screen!!
        )
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnNavigateToHome -> onNavigateToHome()
            is UIEvent.OnMakeFirstSavingTransfer ->  onMakeFirstSavingTransfer()
        }
    }

    private fun onMakeFirstSavingTransfer() {
        // TODO: navigate to HU REV-1476 Selecciona metodo
    }

    sealed class UIEvent {
        object OnNavigateToHome : UIEvent()
        object OnMakeFirstSavingTransfer : UIEvent()
    }
}