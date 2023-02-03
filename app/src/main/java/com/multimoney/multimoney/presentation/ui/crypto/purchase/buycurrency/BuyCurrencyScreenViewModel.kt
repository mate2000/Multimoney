package com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.domain.interaction.accountsmart.QuerySmartExchangeRateUseCase
import com.multimoney.domain.interaction.crypto.BuyCryptoCurrencyUseCase
import com.multimoney.domain.interaction.crypto.GetPriceQuoteAndCommissionsUseCase
import com.multimoney.domain.model.crypto.PricesQuoteAndCommissions
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyCurrencyScreenViewModel @Inject constructor(
    private val getExchangeRate: QuerySmartExchangeRateUseCase,
    private val getPriceQuoteAndCommissionsUseCase: GetPriceQuoteAndCommissionsUseCase,
    private val buyCryptoCurrencyUseCase: BuyCryptoCurrencyUseCase
): BaseViewModel(false) {

    var uiState by mutableStateOf(UIState())
        private set

    var timerCount by mutableStateOf(15)
        private set

    var isTimerRunning by mutableStateOf(false)

    //data from sharedViewModel
    var asset = ""
    var cryptoNetWork = ""
    var idBrand = -1
    var user = ""
    var market = ""
    var identification = ""
    var baseAmount = 0.0
    var side = ""
    var assetImageUrl = ""
    var smartAccountAvailableBalance = 0.0

    private fun timer() {
        viewModelScope.launch {
            while (isTimerRunning) {
                delay(1000)
                timerCount--
                if (timerCount == 0) {
                    updateDataWithNewExchangeRate()
                    timerCount = 15
                }
            }
        }
    }

    private fun onSetUserData(
        asset: String?,
        cryptoNetwork: String?,
        idBrand: Int,
        user: String,
        market: String?,
        identification: String,
        baseAmount: Double,
        side: String,
        assetImageUrl: String?,
        smartAccountAvailableBalance: Double
    ) {
        this.isTimerRunning = true
        this.asset = asset ?: ""
        this.cryptoNetWork = cryptoNetwork ?: ""
        this.idBrand = idBrand
        this.user = user
        this.market = market ?: ""
        this.identification = identification
        this.baseAmount = baseAmount
        this.side = side
        this.assetImageUrl = assetImageUrl ?: ""
        this.smartAccountAvailableBalance = smartAccountAvailableBalance
        timer() // to run timer at the beginning of the screen
    }

    private fun updateDataWithNewExchangeRate() = executeUseCase {
        getPriceQuoteAndCommissionsUseCase.invoke(
                asset = asset,
                crypto_network = cryptoNetWork,
                idBrand = idBrand,
                user = user,
                market = market,
                identification = identification,
                base_amount = baseAmount,
                side = side,
                quote_amount = uiState.quoteAmount
        ).collectLatest { result ->
            result.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
            result.onSuccess { pricesQuotesAndCommission ->
                uiState = uiState.copy(
                    isLoading = false,
                    pricesQuoteAndCommissions = pricesQuotesAndCommission.pricesQuote
                )
            }
            result.onFailure {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    private fun purchaseCryptoCurrency(
        pkUser: Int,
        identification: String,
        market: String,
        commissionAmount: Double,
        taxAmount: Double,
        accountToken: Double,
        exchangeRate: Double,
        idBrand: Int,
        user: String,
        quoteId: String,
        quoteAmount: Double,
        fee: Double,
        internalFee: Double,
        totalFee: Double
    ) {
        executeUseCase {
            buyCryptoCurrencyUseCase.invoke(
                pkUser = pkUser,
                identification = identification,
                market = market,
                commissionAmount = commissionAmount,
                taxAmount = taxAmount,
                accountToken = accountToken,
                exchangeRate = exchangeRate,
                idBrand = idBrand,
                user = user,
                quoteId = quoteId,
                quoteAmount = quoteAmount,
                fee = fee,
                internalFee = internalFee,
                totalFee = totalFee
            ).collectLatest { result ->
                result.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
                result.onSuccess {
                    uiState = uiState.copy(isLoading = false)
                }
                result.onFailure {
                    uiState = uiState.copy(isLoading = false)
                }
            }
        }
    }

    data class UIState(
        val exchangeRate: String = "",
        val asset: String = "",
        val availableSmartAmount: Double = 0.0,
        val isLoading: Boolean = false,
        val quoteAmount: Double = 1.0,
        val pricesQuoteAndCommissions: PricesQuoteAndCommissions? = null
    )

    fun onUIEvent(event: UIEvent) {
        when(event) {
            is UIEvent.OnGetExchangeRate -> updateDataWithNewExchangeRate()
            is UIEvent.OnPurchaseCryptoCurrency -> purchaseCryptoCurrency(
                pkUser = event.pkUser,
                identification = event.identification,
                market = event.market,
                commissionAmount = event.commissionAmount,
                taxAmount = event.taxAmount,
                accountToken = event.accountToken,
                exchangeRate = event.exchangeRate,
                idBrand = event.idBrand,
                user = event.user,
                quoteId = event.quoteId,
                quoteAmount = event.quoteAmount,
                fee = event.fee,
                internalFee = event.internalFee,
                totalFee = event.totalFee
            )
            is UIEvent.OnSetUserData -> onSetUserData(
                asset = event.asset,
                cryptoNetwork = event.cryptoNetwork,
                idBrand = event.idBrand,
                user = event.user,
                market = event.market,
                identification = event.identification,
                baseAmount = event.baseAmount,
                side = event.side,
                assetImageUrl = event.assetImageUrl,
                smartAccountAvailableBalance = event.smartAccountAvailableBalance
            )
        }
    }

    sealed class UIEvent {
        data class OnSetUserData(
            val asset: String,
            val cryptoNetwork: String?,
            val idBrand: Int,
            val user: String,
            val market: String?,
            val identification: String,
            val baseAmount: Double,
            val side: String,
            val assetImageUrl: String?,
            val smartAccountAvailableBalance: Double
        ) : UIEvent()
        object OnGetExchangeRate : UIEvent()
        data class OnPurchaseCryptoCurrency(
            val pkUser: Int,
            val identification: String,
            val market: String,
            val commissionAmount: Double,
            val taxAmount: Double,
            val accountToken: Double,
            val exchangeRate: Double,
            val idBrand: Int,
            val user: String,
            val quoteId: String,
            val quoteAmount: Double,
            val fee: Double,
            val internalFee: Double,
            val totalFee: Double
        ) : UIEvent()
    }
}