package com.multimoney.multimoney.presentation.ui.credit.payment.amount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.balance.Summary
import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.CLIENT_BANK_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.SUMMARY_LIST
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnMaximumPaymentButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnMinimumPaymentButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnNavigateBackHome
import javax.inject.Inject

class PaymentAmountViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String? = null
    private var idBrand: Int? = null
    private var idClient: Int? = null
    private var idLoanClient: Int? = null
    private var summaryList: List<Summary>? = null
    private var clientBankAccount: ClientBankAccount? = null
    private var minimumPayment: Int = 0
    private var maximumPayment: Int = 0

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idClient = savedStateHandle[ID_CLIENT] ?: 0
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: 0
        summaryList = savedStateHandle.get<Array<Summary>>(SUMMARY_LIST)?.toList()
        clientBankAccount = savedStateHandle[CLIENT_BANK_ACCOUNT]
        onInitializeInteractionValues()
    }

    private fun onInitializeInteractionValues() {
        var minimumPaymentLabel = ""
        var maximumPaymentLabel = ""
        var isAmountVisible = false
        if ((summaryList?.count() ?: 0) > 1) {
            summaryList?.forEachIndexed { index, summary ->
                if (index < (summaryList?.lastIndex ?: 0)) {
                    minimumPaymentLabel = minimumPaymentLabel.plus(summary.minPaymentLabel).plus(PAYMENT_PLUS)
                    maximumPaymentLabel = maximumPaymentLabel.plus(summary.currentBalanceLabel).plus(PAYMENT_PLUS)
                } else {
                    minimumPaymentLabel = minimumPaymentLabel.plus(summary.minPaymentLabel)
                    maximumPaymentLabel = maximumPaymentLabel.plus(summary.currentBalanceLabel)
                }
            }
        } else if (summaryList?.isNotEmpty() == true && summaryList?.firstOrNull() != null) {
            val summary = summaryList?.first()
            minimumPaymentLabel = summary?.minPaymentLabel.orEmpty()
            maximumPaymentLabel = summary?.currentBalanceLabel.orEmpty()
            minimumPayment = summary?.minPayment?.toInt() ?: 0
            maximumPayment = summary?.currentBalance?.toInt() ?: 0
            isAmountVisible = true
        }
        uiState = uiState.copy(
            minimumPaymentLabel = minimumPaymentLabel,
            maximumPaymentLabel = maximumPaymentLabel,
            isAmountVisible = isAmountVisible,
            currency = minimumPaymentLabel.first().toString()
        )
        onAmountValueChange(minimumPayment.toString())
    }

    private fun onPaymentButtonClick(isMinimumSelected: Boolean) {
        uiState = uiState.copy(
            currentAmountValueString = if (uiState.isAmountVisible) {
                if (isMinimumSelected) {
                    minimumPayment.toString()
                } else {
                    maximumPayment.toString()
                }
            } else {
                uiState.currentAmountValueString
            },
            isMinimumSelected = isMinimumSelected,
            isMaximumSelected = !isMinimumSelected,
            enableButton = true,
            currentAmountError = Pair(false, R.string.empty)
        )
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

    private fun onNavigateBack() = navigateBack(popTo = Screen.PaymentAccountScreen.route, isRestart = false)

    private fun onNavigateBackHome() = navigateBack(popTo = Screen.HomeScreen.route, isRestart = false)

    fun getFormattedCurrency() =
        if (uiState.currentAmountValueString.isNotEmpty() && uiState.currentAmountValueString.toInt() > maximumPayment) {
            uiState.maximumPaymentLabel
        } else {
            PAYMENT_MUST_HIGHER_THAN_VALUE
        }

    data class UIState(
        // Interactions
        val minimumPaymentLabel: String = "",
        val maximumPaymentLabel: String = "",
        val isMinimumSelected: Boolean = false,
        val isMaximumSelected: Boolean = false,
        val currency: String = "$",
        val currentAmountValueString: String = "0",
        val currentAmountError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
        val enableButton: Boolean = false,
        val isAmountVisible: Boolean = true
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnNavigateBackHome -> onNavigateBackHome()
            is OnAmountValueChange -> onAmountValueChange(uiEvent.value)
            is OnMinimumPaymentButtonClick -> onPaymentButtonClick(true)
            is OnMaximumPaymentButtonClick -> onPaymentButtonClick(false)
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnNavigateBackHome : UIEvent()
        object OnMinimumPaymentButtonClick : UIEvent()
        object OnMaximumPaymentButtonClick : UIEvent()
        data class OnAmountValueChange(val value: String) : UIEvent()
    }

    companion object {
        const val PAYMENT_MUST_HIGHER_THAN_VALUE = 0
        const val PAYMENT_PLUS = " + "
    }
}
