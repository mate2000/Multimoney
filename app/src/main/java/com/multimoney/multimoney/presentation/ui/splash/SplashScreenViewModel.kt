package com.multimoney.multimoney.presentation.ui.splash

import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.util.UiEvent
import javax.inject.Inject

class SplashScreenViewModel @Inject constructor() : BaseViewModel() {

    // TODO replace for correct navigate when implemented
    fun navigateToChart() {
        sendUiEvent(
            UiEvent.Navigate(
                route = Screen.ChartScreen.route
            )
        )
    }
}