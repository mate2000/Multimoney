package com.multimoney.multimoney.presentation.ui.smart.payment.methodsv

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.SMART_IDS
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIState
import com.multimoney.multimoney.presentation.ui.smart.payment.methodsv.SmartPaymentMethodViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.methodsv.SmartPaymentMethodViewModel.UIEvent.OnTransferSelected
import com.multimoney.multimoney.presentation.ui.smart.payment.methodsv.SmartPaymentMethodViewModel.UIEvent.OnVisaSelected
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SmartPaymentMethodViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // stateLess
    private var smartAccount: SmartAccountID? = null

    init {
        smartAccount = savedStateHandle[SMART_IDS]
    }

    private fun onNavigateBack() {
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = false
        )
    }

    private fun navigateToTransferScreen() {
        navigateTo("${Screen.SavingMethodTransferScreen.baseRoute}/${smartAccount?.accountNumber ?: ""}")
    }

    private fun navigateToVisaScreen() {
        navigateTo(
            "${Screen.SmartPaymentCardsScreenSV.baseRoute}/${encodeData(smartAccount)}"
        )
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnTransferSelected -> navigateToTransferScreen()
            is OnVisaSelected -> navigateToVisaScreen()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnTransferSelected : UIEvent()
        object OnVisaSelected : UIEvent()
    }
}
