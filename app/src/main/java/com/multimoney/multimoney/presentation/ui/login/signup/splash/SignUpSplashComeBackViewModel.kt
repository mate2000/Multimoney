package com.multimoney.multimoney.presentation.ui.login.signup.splash

import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.login.signup.splash.SignUpSplashComeBackViewModel.UIEvent.OnOpenStep
import com.multimoney.multimoney.presentation.ui.login.signup.splash.SignUpSplashComeBackViewModel.UIEvent.OnStepValueChange
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpSplashComeBackViewModel @Inject constructor() : BaseViewModel() {

    // Stateless
    var step: Int? = 0

    private fun openStep() {
        popAndNavigateTo(
            route = "${Screen.SignUpScreen.baseRoute}/".plus(step),
            popTo = Screen.SignUpBiometricsScreen.route
        )
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnStepValueChange -> step = event.step.toInt()
            is OnOpenStep -> openStep()
        }
    }

    sealed class UIEvent {
        data class OnStepValueChange(val step: String) : UIEvent()
        object OnOpenStep : UIEvent()
    }
}