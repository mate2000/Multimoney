package com.multimoney.multimoney.presentation.ui.payment.amount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.payment.amount.PaymentAmountViewModel.UIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.payment.amount.PaymentAmountViewModel.UIEvent.OnAmountValueChangeFinished
import com.multimoney.multimoney.presentation.ui.payment.amount.PaymentAmountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.util.stringToIntegerFormat
import javax.inject.Inject

class PaymentAmountViewModel @Inject constructor() : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    val minimumPayment: Int = 5000
    val maximumPayment: Int = 35000

    private fun onAmountValueChange(value: String) {
        uiState = uiState.copy(currentAmountValueString = value)
    }

    private fun onAmountValueChangeFinished(value: String) {
        uiState = uiState.copy(
            enableButton = (
                value.toInt() <= maximumPayment &&
                    value.toInt() >= minimumPayment
                ),
            currentAmountError = if (value.toInt() > maximumPayment) {
                Pair(true, R.string.payment_amount_amount_max_error)
            } else if (value.toInt() < minimumPayment) {
                Pair(true, R.string.payment_amount_amount_min_error)
            } else {
                Pair(false, R.string.error_empty)
            }
        )
    }

    fun setCurrentValueToMin() {
        onAmountValueChange(minimumPayment.toString())
    }

    fun setCurrentValueToMax() {
        onAmountValueChange(maximumPayment.toString())
    }

    fun getFormattedCurrency() = "${uiState.currency} ${uiState.currentAmountValueString.stringToIntegerFormat()}"

    data class UIState(
        // Interactions
        val minimumPaymentLabel: String = "$5000",
        val maximumPaymentLabel: String = "$5000",
        val currentAmountValue: Int = 0,
        val currency: String = "",
        val currentAmountValueString: String = currentAmountValue.toString(),
        val currentAmountError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
        val enableButton: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> popAndNavigateTo(
                route = Screen.HomeScreen.route,
                popTo = Screen.VisaIssuanceScreen.route
            )
            is OnAmountValueChangeFinished -> onAmountValueChangeFinished(uiEvent.value)
            is OnAmountValueChange -> onAmountValueChange(uiEvent.value)
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        class OnAmountValueChangeFinished(val value: String) : UIEvent()
        data class OnAmountValueChange(val value: String) : UIEvent()
    }
}
