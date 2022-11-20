package com.multimoney.multimoney.presentation.ui.credit.origination.continuevalidatingonfido

import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.credit.origination.continuevalidatingonfido.ContinueValidatingOnfidoViewModel.UIEvent.OnNavigateToHome
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContinueValidatingOnfidoViewModel @Inject constructor() : BaseViewModel(true) {

    private fun onNavigateToHome() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.ContinueValidatingOnfidoScreen.route
        )
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnNavigateToHome -> onNavigateToHome()
        }
    }

    sealed class UIEvent {
        object OnNavigateToHome : UIEvent()
    }
}
