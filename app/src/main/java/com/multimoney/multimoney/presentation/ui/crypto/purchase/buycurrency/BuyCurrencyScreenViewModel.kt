package com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.multimoney.domain.interaction.accountsmart.QuerySmartExchangeRateUseCase
import com.multimoney.domain.interaction.crypto.BuyCryptoCurrencyUseCase
import com.multimoney.domain.interaction.crypto.GetPriceQuoteAndCommissionsUseCase
import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BuyCurrencyScreenViewModel @Inject constructor(
    private val getExchangeRate: QuerySmartExchangeRateUseCase,
    private val getPriceQuoteAndCommissionsUseCase: GetPriceQuoteAndCommissionsUseCase,
    private val buyCryptoCurrencyUseCase: BuyCryptoCurrencyUseCase
): BaseViewModel(false) {

    var uiState by mutableStateOf(UIState())
        private set

    private fun updateDataWithNewExchangeRate() {

    }

    private fun updatePurchaseReferenceData() {

    }

    private fun purchaseCryptoCurrency() {

    }

    data class UIState(
        val exchangeRate: String = ""
    )

    fun onUIEvent(event: UIEvent) {
        when(event) {
            is UIEvent.OnGetExchangeRate -> updateDataWithNewExchangeRate()
            is UIEvent.OnPurchaseCryptoCurrency -> purchaseCryptoCurrency()
        }
    }

    sealed class UIEvent {
        object OnGetExchangeRate : UIEvent()
        object OnPurchaseCryptoCurrency : UIEvent()
    }
}