package com.multimoney.multimoney.presentation.ui.login.signup.completed

import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.login.signup.completed.SignUpCompletedViewModel.UIEvent.OnNavigateToSignIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpCompletedViewModel @Inject constructor() : BaseViewModel(false) {

    private fun onNavigateToSignIn() {
        popAndNavigateTo(
            route = Screen.SignInScreen.route,
            popTo = Screen.SignUpCompleted.route
        )
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnNavigateToSignIn -> onNavigateToSignIn()
        }
    }

    sealed class UIEvent {
        object OnNavigateToSignIn : UIEvent()
    }
}