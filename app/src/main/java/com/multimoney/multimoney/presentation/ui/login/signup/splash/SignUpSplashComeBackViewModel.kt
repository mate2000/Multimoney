package com.multimoney.multimoney.presentation.ui.login.signup.splash

import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpSplashComeBackViewModel @Inject constructor() : BaseViewModel() {

    fun onUIEvent(event: UIEvent) {
        when (event) {

        }
    }

    sealed class UIEvent {

    }
}