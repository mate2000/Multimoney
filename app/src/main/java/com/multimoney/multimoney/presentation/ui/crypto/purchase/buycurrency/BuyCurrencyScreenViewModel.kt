package com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency

import android.os.CountDownTimer
import androidx.compose.runtime.MutableState
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyCurrencyScreenViewModel @Inject constructor(
    private val getExchangeRate: QuerySmartExchangeRateUseCase,
    private val getPriceQuoteAndCommissionsUseCase: GetPriceQuoteAndCommissionsUseCase,
    private val buyCryptoCurrencyUseCase: BuyCryptoCurrencyUseCase
) : BaseViewModel(false) {

    var uiState by mutableStateOf(UIState())
        private set

    var timerCount by mutableStateOf<Int?>(null)
        private set

    var isTimerRunning by mutableStateOf(timerCount != null || timerCount != 0)

    private val timer = object : CountDownTimer(15000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            timerCount = (millisUntilFinished / 1000).toInt()
        }

        override fun onFinish() {
            updateDataWithNewExchangeRate()
        }
    }

    //data from sharedViewModel
    var asset = ""
    var cryptoNetWork = ""
    var idBrand = -1
    var user = ""
    var market = ""
    var identification = ""
    var side = ""
    var assetImageUrl = ""
    var smartAccountAvailableBalance = 0.0

    // working on timer
    /*private fun timer() {
        viewModelScope.launch {
            while (isTimerRunning) {
                delay(1000)
                timerCount--
                if (timerCount == 0) {
                    updateDataWithNewExchangeRate()
                    isTimerRunning = false
                    timerCount = 15
                }
            }
        }
    }*/

    private fun onSetUserData(
        asset: String,
        cryptoNetwork: String,
        idBrand: Int,
        user: String,
        market: String,
        identification: String,
        side: String,
        assetImageUrl: String,
        smartAccountAvailableBalance: Double
    ) {
        this.isTimerRunning = true
        this.asset = asset
        this.cryptoNetWork = cryptoNetwork
        this.idBrand = idBrand
        this.user = user
        this.market = market
        this.identification = identification
        this.side = side
        this.assetImageUrl = assetImageUrl
        this.smartAccountAvailableBalance = smartAccountAvailableBalance
        if (isTimerRunning.not()) {
            updateDataWithNewExchangeRate()
        }
    }

    private fun updateDataWithNewExchangeRate(): Unit = executeUseCase {
        getPriceQuoteAndCommissionsUseCase.invoke(
            asset = asset,
            crypto_network = cryptoNetWork,
            idBrand = idBrand,
            user = user,
            market = market,
            identification = identification,
            side = side,
            base_amount = uiState.baseAmount.value.ifEmpty { "0.0" }.toDouble(),
            quote_amount = uiState.quoteAmount.value.ifEmpty {
                if (uiState.baseAmount.value.isNotEmpty()) "0.0" else "1.0"
            }.toDouble()
        ).collectLatest { result ->
            result.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
            result.onSuccess { pricesQuotesAndCommission ->
                uiState = uiState.copy(
                    isLoading = false,
                    pricesQuoteAndCommissions = pricesQuotesAndCommission.pricesQuote
                )
                timer.start()
            }
            result.onFailure {
                uiState = uiState.copy(isLoading = false)
                timer.cancel()
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
        val quoteAmount: MutableState<String> = mutableStateOf(""),
        val baseAmount: MutableState<String> = mutableStateOf(""),
        val pricesQuoteAndCommissions: PricesQuoteAndCommissions? = null
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
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
                side = event.side,
                assetImageUrl = event.assetImageUrl,
                smartAccountAvailableBalance = event.smartAccountAvailableBalance
            )
            is UIEvent.OnSetQuoteAmount -> {}
        }
    }

    sealed class UIEvent {
        data class OnSetUserData(
            val asset: String,
            val cryptoNetwork: String,
            val idBrand: Int,
            val user: String,
            val market: String,
            val identification: String,
            val side: String,
            val assetImageUrl: String,
            val smartAccountAvailableBalance: Double
        ) : UIEvent()

        object OnGetExchangeRate : UIEvent()
        data class OnSetQuoteAmount(val quoteAmount: Double) : UIEvent()
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