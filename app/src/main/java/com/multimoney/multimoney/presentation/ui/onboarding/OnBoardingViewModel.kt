package com.multimoney.multimoney.presentation.ui.onboarding

import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.util.UiEvent
import javax.inject.Inject

class OnBoardingViewModel @Inject constructor() : BaseViewModel() {

    // TODO replace for correct navigate when implemented
    fun navigateToLogin() {
        sendUiEvent(
            UiEvent.Navigate(
                route = Screen.ChartScreen.route
            )
        )
    }

    fun navigateToRegister(){
        sendUiEvent(
            UiEvent.Navigate(
                route = Screen.ChartScreen.route
            )
        )
    }
}