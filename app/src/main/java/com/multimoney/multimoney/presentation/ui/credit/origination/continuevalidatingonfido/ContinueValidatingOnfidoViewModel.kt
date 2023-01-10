package com.multimoney.multimoney.presentation.ui.credit.origination.continuevalidatingonfido

import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.credit.origination.continuevalidatingonfido.ContinueValidatingOnfidoViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.home.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContinueValidatingOnfidoViewModel @Inject constructor() : BaseViewModel(true) {

    private fun onNavigateToHome() {
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = true, homeState = HomeState.UNEXPANDED)
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
