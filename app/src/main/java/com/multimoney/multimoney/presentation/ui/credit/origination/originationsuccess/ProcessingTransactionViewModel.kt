package com.multimoney.multimoney.presentation.ui.credit.origination.originationsuccess

import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.credit.origination.originationsuccess.ProcessingTransactionViewModel.UIEvent.OnNavigateToHome
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProcessingTransactionViewModel @Inject constructor() : BaseViewModel(true) {
    private fun onNavigateToHome() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.ProcessingTransactionScreen.route
        )
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnNavigateToHome -> onNavigateToHome()
        }
    }

    sealed class UIEvent {
        object OnNavigateToHome : UIEvent()
    }
}
