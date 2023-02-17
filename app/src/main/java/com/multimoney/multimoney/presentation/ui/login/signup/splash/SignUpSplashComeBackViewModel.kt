package com.multimoney.multimoney.presentation.ui.login.signup.splash

import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.FORCE_CHANGE_DEVICE
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_UP_STEP
import com.multimoney.multimoney.presentation.ui.login.signup.splash.SignUpSplashComeBackViewModel.UIEvent.OnOpenStep
import com.multimoney.multimoney.presentation.util.getNavParam
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpSplashComeBackViewModel @Inject constructor(savedStateHandle: SavedStateHandle) :
    BaseViewModel(false) {

    // Stateless
    var step: Int? = 0
    var idBrand: Int? = 0

    init {
        step = savedStateHandle.get<String>(SIGN_UP_STEP)?.toInt()
        idBrand = savedStateHandle[ID_BRAND]
    }

    private fun openStep() {
        popAndNavigateTo(
            route = "${Screen.SignUpScreen.baseRoute}/".plus(step).plus(
                getNavParam(ID_BRAND, idBrand),
            ), popTo = Screen.SignUpSplashComeBackScreen.route
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
