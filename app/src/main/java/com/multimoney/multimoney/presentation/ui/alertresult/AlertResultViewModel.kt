package com.multimoney.multimoney.presentation.ui.alertresult

import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.alertresult.AlertResultViewModel.UIEvent.OnCloseClick
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlertResultViewModel @Inject constructor() : BaseViewModel() {

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnCloseClick -> popAndNavigateTo(
                route = Screen.HomeScreen.route,
                popTo = Screen.AlertResultScreen.route
            )
        }
    }

    sealed class UIEvent {
        object OnCloseClick : UIEvent()
    }
}