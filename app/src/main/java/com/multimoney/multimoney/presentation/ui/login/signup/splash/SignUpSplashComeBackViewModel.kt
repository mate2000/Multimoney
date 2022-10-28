package com.multimoney.multimoney.presentation.ui.login.signup.splash

import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_UP_STEP
import com.multimoney.multimoney.presentation.ui.login.signup.splash.SignUpSplashComeBackViewModel.UIEvent.OnOpenStep
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpSplashComeBackViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : BaseViewModel(false) {

    // Stateless
    var step: Int? = 0

    init {
        step = savedStateHandle.get<String>(SIGN_UP_STEP)?.toInt()
    }

    private fun openStep() {
        popAndNavigateTo(
            route = "${Screen.SignUpScreen.baseRoute}/".plus(step),
            popTo = Screen.SignUpSplashComeBackScreen.route
        )
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnOpenStep -> openStep()
        }
    }

    sealed class UIEvent {
        object OnOpenStep : UIEvent()
    }
}
