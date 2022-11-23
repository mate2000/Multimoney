package com.multimoney.multimoney.presentation.ui.credit.disbursement.addnewaccountsuccess

import androidx.compose.ui.focus.FocusManager
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddNewAccountSuccessViewModel @Inject constructor() : BaseViewModel(true){


    private fun navigateBackToHome() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.AddNewAccountSuccessScreen.route
        )
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnCloseClick -> navigateBackToHome()
            is UIEvent.OnBackClick -> navigateBackToHome()
            is UIEvent.OnNavigateBackToHome -> navigateBackToHome()
        }
    }

    sealed class UIEvent {
        data class OnCloseClick(val focusManager: FocusManager) : UIEvent()
        data class OnBackClick(val focusManager: FocusManager) : UIEvent()
        object OnNavigateBackToHome : UIEvent()
    }

}