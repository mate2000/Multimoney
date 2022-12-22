package com.multimoney.multimoney.presentation.ui.smart.payment.options

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SMART_IDS_LIST
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.smart.payment.options.SmartPaymentOptionsViewModel.UIEvent.OnColonSelected
import com.multimoney.multimoney.presentation.ui.smart.payment.options.SmartPaymentOptionsViewModel.UIEvent.OnDollarSelected
import com.multimoney.multimoney.presentation.ui.smart.payment.options.SmartPaymentOptionsViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SmartPaymentOptionsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: Int = 0
    private var idClient: String = ""
    private var idLoanClient: String = ""
    private var identification: String? = ""
    private var smartAccountIDs: List<SmartAccountID>? = listOf()

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idClient = savedStateHandle[ID_CLIENT] ?: ""
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        smartAccountIDs = savedStateHandle.get<Array<SmartAccountID>>(SMART_IDS_LIST)?.toList()
    }

    private fun onNavigateBack() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.SmartPaymentOptionsScreenCR.route
        )
    }

    private fun navigateToPaymentAccountScreen(currencyType: CurrencyType) {
        val account = when (currencyType) {
            CurrencyType.Colon -> smartAccountIDs?.find { it.currencyID == CurrencyType.Colon.id }
            else -> smartAccountIDs?.find { it.currencyID == CurrencyType.Dollar.id }
        }
        navigateTo(
            route = "${Screen.SmartPaymentAccountScreenCR.baseRoute}/$user/$idBrand/$identification/${Screen.SmartPaymentOptionsScreenCR.baseRoute}/$idClient/$idLoanClient/${encodeData(account)}"
        )
    }

    data class UIState(
        val openDialog: DialogParameters = DialogParameters(),
        var isLoading: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnColonSelected -> navigateToPaymentAccountScreen(CurrencyType.Colon)
            is OnDollarSelected -> navigateToPaymentAccountScreen(CurrencyType.Dollar)
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnColonSelected : UIEvent()
        object OnDollarSelected : UIEvent()
    }
}
