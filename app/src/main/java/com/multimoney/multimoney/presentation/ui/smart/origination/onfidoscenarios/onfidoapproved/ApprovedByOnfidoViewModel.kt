package com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidoapproved

import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.USER_SMART_ACCOUNT
import com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidoapproved.ApprovedByOnfidoViewModel.UIEvent.OnNavigateToHome
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ApprovedByOnfidoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // stateLess
    var userSmartAccount: String = ""

    init {
        userSmartAccount = savedStateHandle[USER_SMART_ACCOUNT] ?: ""
    }

    private fun onNavigateToHome() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.ApprovedByOnfidoScreen.route
        )
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnNavigateToHome -> onNavigateToHome()
            is UIEvent.OnMakeFirstSavingTransfer -> onMakeFirstSavingTransfer()
        }
    }

    private fun onMakeFirstSavingTransfer() {
        // TODO: navigate to HU REV-1476 Selecciona metodo
        navigateTo("${Screen.SmartPaymentScreen.baseRoute}/$userSmartAccount")
    }

    sealed class UIEvent {
        object OnNavigateToHome : UIEvent()
        object OnMakeFirstSavingTransfer : UIEvent()
    }
}