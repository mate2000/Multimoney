package com.multimoney.multimoney.presentation.ui.payment.fee

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.model.balance.Summary
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.payment.fee.PaymentFeeSelectionViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.payment.fee.PaymentFeeSelectionViewModel.UIEvent.OnNavigateToPaymentAccount
import com.multimoney.multimoney.presentation.ui.payment.fee.PaymentFeeSelectionViewModel.UIEvent.OnSaveArguments
import com.multimoney.multimoney.presentation.util.catalog.Currency
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentFeeSelectionViewModel @Inject constructor() : BaseViewModel(true) {

    // Stateless
    var user: String? = ""
    var idBrand: Int? = null
    var idClient: Int? = null
    var idLoanClient: Int? = null

    // UIState
    var uiState by mutableStateOf(UIState())
        private set


    private fun onSaveArguments(
        user: String?, idBrand: Int?, idClient: Int?, idLoanClient: Int?, summaryList: List<Summary>
    ) {
        this.user = user
        this.idBrand = idBrand
        this.idClient = idClient
        this.idLoanClient = idLoanClient
        uiState = uiState.copy(summaryList = summaryList)
    }

    private fun onNavigateToPaymentAccount(currency: Currency) {
        navigateTo(route = "${Screen.PaymentAccountScreen.baseRoute}/${user}/${idBrand}/${idClient}/${idLoanClient}/${currency.value}/${currency.id}")
    }

    private fun onNavigateBack() {
        popAndNavigateTo(Screen.HomeScreen.route, Screen.PaymentFeeScreen.route)
    }

    fun getAllQuotas(): String {
        val quotas = StringBuilder()
        uiState.summaryList.forEachIndexed { index, summary ->
            quotas.append(summary.monthlyQuotaLabel)
            if (uiState.summaryList.lastIndex != index) {
                quotas.append(" + ")
            }
        }
        return quotas.toString()
    }

    data class UIState(
        var summaryList: List<Summary> = listOf()
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnSaveArguments -> onSaveArguments(
                event.user, event.idBrand, event.idClient, event.idLoanClient, event.summaryList
            )
            is OnNavigateToPaymentAccount -> onNavigateToPaymentAccount(event.currency)
            is OnNavigateBack -> onNavigateBack()
        }
    }

    sealed class UIEvent {
        data class OnSaveArguments(
            val user: String?, val idBrand: Int?, val idClient: Int?, val idLoanClient: Int?, val summaryList: List<Summary>
        ) : UIEvent()

        data class OnNavigateToPaymentAccount(val currency: Currency) : UIEvent()
        object OnNavigateBack : UIEvent()
    }
}