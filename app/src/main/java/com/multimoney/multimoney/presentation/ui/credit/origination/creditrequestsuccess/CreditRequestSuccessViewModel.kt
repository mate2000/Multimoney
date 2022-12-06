package com.multimoney.multimoney.presentation.ui.credit.origination.creditrequestsuccess

import androidx.compose.ui.focus.FocusManager
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreditRequestSuccessViewModel @Inject constructor() : BaseViewModel(true) {

    private fun navigateBackToHome() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.CreditRequestSuccessScreen.route
        )
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnCloseClick -> navigateBackToHome()
            is UIEvent.OnUnderstoodClick -> navigateBackToHome()
        }
    }

    sealed class UIEvent {
        data class OnCloseClick(val focusManager: FocusManager) : UIEvent()
        object OnUnderstoodClick : UIEvent()
    }
}