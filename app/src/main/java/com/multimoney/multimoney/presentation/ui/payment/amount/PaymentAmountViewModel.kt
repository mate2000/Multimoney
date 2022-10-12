package com.multimoney.multimoney.presentation.ui.payment.amount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.payment.amount.PaymentAmountViewModel.UIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.payment.amount.PaymentAmountViewModel.UIEvent.OnMaximumPaymentButtonClick
import com.multimoney.multimoney.presentation.ui.payment.amount.PaymentAmountViewModel.UIEvent.OnMinimumPaymentButtonClick
import com.multimoney.multimoney.presentation.ui.payment.amount.PaymentAmountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.payment.amount.PaymentAmountViewModel.UIEvent.OnSetParameters
import javax.inject.Inject

class PaymentAmountViewModel @Inject constructor() : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var minimumPayment: Int = 0
    private var maximumPayment: Int = 0

    private fun onSetParameters(minimumPayment: Int, maximumPayment: Int) {
        this.minimumPayment = minimumPayment
        this.maximumPayment = maximumPayment

        onAmountValueChange(minimumPayment.toString())
    }

    private fun onAmountValueChange(value: String) {
        uiState = uiState.copy(
            currentAmountValueString = value,
            isMinimumSelected = value.isNotEmpty() && value.toInt() == minimumPayment,
            isMaximumSelected = value.isNotEmpty() && value.toInt() == maximumPayment,
            enableButton = (
                value.isNotEmpty() && value.toInt() <= maximumPayment &&
                    value.isNotEmpty() && value.toInt() > PAYMENT_MUST_HIGHER_THAN_VALUE
                ),
            currentAmountError = if (value.isNotEmpty() && value.toInt() > maximumPayment) {
                Pair(true, R.string.payment_amount_amount_max_error)
            } else if (value.isEmpty() || value.toInt() <= PAYMENT_MUST_HIGHER_THAN_VALUE) {
                Pair(true, R.string.payment_amount_amount_min_error)
            } else {
                Pair(false, R.string.empty)
            }
        )
    }

    fun getFormattedCurrency() =
        if (uiState.currentAmountValueString.isNotEmpty() && uiState.currentAmountValueString.toInt() > maximumPayment) {
            uiState.maximumPaymentLabel
        } else {
            PAYMENT_MUST_HIGHER_THAN_VALUE
        }

    data class UIState(
        // Interactions
        val minimumPaymentLabel: String = "$5000",
        val maximumPaymentLabel: String = "$35000",
        val isMinimumSelected: Boolean = false,
        val isMaximumSelected: Boolean = false,
        val currency: String = "$",
        val currentAmountValueString: String = "0",
        val currentAmountError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
        val enableButton: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> popAndNavigateTo(
                route = Screen.PaymentAccountScreen.route,
                popTo = Screen.PaymentAmountScreen.route
            )
            is OnAmountValueChange -> onAmountValueChange(uiEvent.value)
            is OnMinimumPaymentButtonClick -> onAmountValueChange(minimumPayment.toString())
            is OnMaximumPaymentButtonClick -> onAmountValueChange(maximumPayment.toString())
            is OnSetParameters -> onSetParameters(uiEvent.minimumPayment, uiEvent.maximumPayment)
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnMinimumPaymentButtonClick : UIEvent()
        object OnMaximumPaymentButtonClick : UIEvent()
        class OnSetParameters(val minimumPayment: Int, val maximumPayment: Int) : UIEvent()
        data class OnAmountValueChange(val value: String) : UIEvent()
    }

    companion object {
        const val PAYMENT_MUST_HIGHER_THAN_VALUE = 0
    }
}
