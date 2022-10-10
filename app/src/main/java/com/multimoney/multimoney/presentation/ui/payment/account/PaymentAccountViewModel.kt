package com.multimoney.multimoney.presentation.ui.payment.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountViewModel.UIEvent.OnGetTextResources
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountViewModel.UIEvent.OnSetIdCurrency
import com.multimoney.multimoney.presentation.util.catalog.Currency
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentAccountViewModel @Inject constructor() : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var idCurrency: Int? = null

    private fun setCurrency(idCurrency: Int) {
        this.idCurrency = idCurrency
    }

    private fun getTextResources() {
        uiState = uiState.copy(
            titleResource = Currency.Search.getAccountIconByCurrency(idCurrency).accountTitle
        )
    }

    data class UIState(
        // Interactions
        val titleResource: Int = R.string.empty
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> popAndNavigateTo(
                route = Screen.PaymentFeeScreen.route,
                popTo = Screen.PaymentAccountScreen.route
            )
            is OnSetIdCurrency -> setCurrency(uiEvent.idCurrency)
            is OnGetTextResources -> getTextResources()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        class OnSetIdCurrency(val idCurrency: Int) : UIEvent()
        object OnGetTextResources : UIEvent()
    }
}
