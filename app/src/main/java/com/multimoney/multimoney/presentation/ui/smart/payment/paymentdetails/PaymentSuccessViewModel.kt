package com.multimoney.multimoney.presentation.ui.smart.payment.paymentdetails

import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.navgraph.CARD_SELECTED
import com.multimoney.multimoney.presentation.navigation.navgraph.CURRENCY_SYMBOL
import com.multimoney.multimoney.presentation.navigation.navgraph.EXCHANGE_AMOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.EXCHANGE_RATE_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CURRENCY
import com.multimoney.multimoney.presentation.navigation.navgraph.IS_MULTI_CURRENCY
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_AMOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.REFERENCE_NUMBER
import com.multimoney.multimoney.presentation.ui.smart.payment.paymentdetails.PaymentSuccessViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.smart.payment.paymentdetails.PaymentSuccessViewModel.UIEvent.OnSharedVoucherImage
import com.multimoney.multimoney.presentation.util.ShareHelper
import com.multimoney.multimoney.presentation.util.getCurrentDate
import com.multimoney.multimoney.presentation.util.getCurrentTime
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class PaymentSuccessViewModel @Inject constructor(
    private val shareHelper: ShareHelper,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var smartAccountCurrency: String = savedStateHandle[CURRENCY_SYMBOL] ?: ""
    var amountSaved: String = savedStateHandle[PAYMENT_AMOUNT] ?: ""
    var isMultiCurrency: Boolean = savedStateHandle[IS_MULTI_CURRENCY] ?: false
    var exchangeCurrency: String = if (smartAccountCurrency == "$") "C" else "$"
    var exchangeAmount: String = savedStateHandle[EXCHANGE_AMOUNT] ?: ""
    var exchangeRate: String = savedStateHandle[EXCHANGE_RATE_LABEL] ?: ""
    var cardNumberMasked: String = savedStateHandle[CARD_SELECTED] ?: ""
    var referenceNumber: String = savedStateHandle[REFERENCE_NUMBER] ?: ""
    var currentDate: String = getCurrentDate(Calendar.getInstance().time)
    var currentTime: String = getCurrentTime(Calendar.getInstance().time)

    private fun onShareVoucherImage(
        view: View,
        capturingBounds: Rect
    ) {
        shareHelper.sharedScreenShot(view, capturingBounds)
    }

    data class UIState(
        val isAlertResultVisible: Boolean = false,
        val isLoading: Boolean = false
    )

    private fun onNavigateToHome() { /* todo */ }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnCloseClick -> onNavigateToHome()
            is OnSharedVoucherImage -> onShareVoucherImage(event.view, event.capturingBounds)
        }
    }

    sealed class UIEvent {
        object OnCloseClick : UIEvent()
        data class OnSharedVoucherImage(
            val view: View,
            val capturingBounds: Rect
        ) : UIEvent()
    }
}
