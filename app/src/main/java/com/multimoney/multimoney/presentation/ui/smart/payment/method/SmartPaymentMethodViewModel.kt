package com.multimoney.multimoney.presentation.ui.smart.payment.method

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.ACCOUNT_TOKEN
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CURRENCY
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.navgraph.USER_SMART_ACCOUNT
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIState
import com.multimoney.multimoney.presentation.ui.smart.payment.method.SmartPaymentMethodViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.method.SmartPaymentMethodViewModel.UIEvent.OnTransferSelected
import com.multimoney.multimoney.presentation.ui.smart.payment.method.SmartPaymentMethodViewModel.UIEvent.OnVisaSelected
import javax.inject.Inject
import okhttp3.internal.toLongOrDefault

class SmartPaymentMethodViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // stateLess
    var userSmartAccount: String = ""
    var username: String = savedStateHandle[USER] ?: ""
    var idBrand: String = savedStateHandle[ID_BRAND] ?: ""
    var identification: String = savedStateHandle[IDENTIFICATION] ?: ""
    var accountToken: String = savedStateHandle[ACCOUNT_TOKEN] ?: ""
    var currencyId: String = savedStateHandle[ID_CURRENCY] ?: ""

    init {
        userSmartAccount = savedStateHandle[USER_SMART_ACCOUNT] ?: ""
    }

    private fun onNavigateBack() {
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = false
        )
    }

    private fun navigateToTransferScreen() {
        navigateTo("${Screen.SavingMethodTransferScreen.baseRoute}/$userSmartAccount")
    }

    private fun navigateToVisaScreen() {
        navigateTo(
            "${Screen.PaymentSmartCardsScreen.baseRoute}/$username/$idBrand/$identification/${accountToken.toLongOrNull()}/$currencyId"
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
