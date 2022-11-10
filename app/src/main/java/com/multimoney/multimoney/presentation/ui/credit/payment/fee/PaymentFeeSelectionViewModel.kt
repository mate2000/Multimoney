package com.multimoney.multimoney.presentation.ui.credit.payment.fee

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.balance.Summary
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.NAME_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_DATE
import com.multimoney.multimoney.presentation.navigation.navgraph.SUMMARY_LIST
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.credit.payment.fee.PaymentFeeSelectionViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.fee.PaymentFeeSelectionViewModel.UIEvent.OnNavigateToPaymentAccount
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentFeeSelectionViewModel @Inject constructor(savedStateHandle: SavedStateHandle) :
    BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String? = ""
    private var idBrand: Int? = null
    private var idClient: Int? = null
    private var idLoanClient: Int? = null
    private var identification: String? = null
    private var userName: String? = null
    private var paymentDate: String? = null

    init {
        user = savedStateHandle[USER]
        idBrand = savedStateHandle[ID_BRAND]
        idClient = savedStateHandle[ID_CLIENT]
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT]
        identification = savedStateHandle[IDENTIFICATION]
        userName = savedStateHandle[NAME_CLIENT]
        paymentDate = savedStateHandle[PAYMENT_DATE]
        uiState = uiState.copy(summaryList = savedStateHandle.get<Array<Summary>>(SUMMARY_LIST)?.toList())
    }

    private fun onNavigateToPaymentAccount(currencyType: CurrencyType?) {
        val summaryList = if (currencyType?.id != CurrencyType.All.id) {
            listOf(uiState.summaryList?.firstOrNull { it?.idCurrency == currencyType?.id })
        } else {
            uiState.summaryList
        }
        navigateTo(
            route = "${Screen.PaymentAccountScreen.baseRoute}/$user/$idBrand/$idClient/$idLoanClient/${
            encodeData(
                summaryList
            )
            }/$identification/$userName/$paymentDate/${Screen.PaymentFeeScreen.baseRoute}"
        )
    }

    private fun onNavigateBack() = navigateBack(Screen.HomeScreen.route, false)

    fun getAllQuotas(plusString: String): String {
        val quotas = StringBuilder()
        uiState.summaryList?.forEachIndexed { index, summary ->
            quotas.append(summary?.monthlyQuotaLabel)
            if (uiState.summaryList?.lastIndex != index) {
                quotas.append(plusString)
            }
        }
        return quotas.toString()
    }

    data class UIState(
        var summaryList: List<Summary?>? = listOf()
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnNavigateToPaymentAccount -> onNavigateToPaymentAccount(event.currencyType)
            is OnNavigateBack -> onNavigateBack()
        }
    }

    sealed class UIEvent {
        data class OnNavigateToPaymentAccount(val currencyType: CurrencyType?) : UIEvent()
        object OnNavigateBack : UIEvent()
    }
}
