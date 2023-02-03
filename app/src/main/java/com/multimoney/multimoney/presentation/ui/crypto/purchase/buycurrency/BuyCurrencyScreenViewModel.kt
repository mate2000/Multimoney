package com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency

import android.content.Context
import android.os.CountDownTimer
import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.accountsmart.QuerySmartExchangeRateUseCase
import com.multimoney.domain.interaction.crypto.BuyCryptoCurrencyUseCase
import com.multimoney.domain.interaction.crypto.GetPriceQuoteAndCommissionsUseCase
import com.multimoney.domain.model.crypto.PricesQuoteAndCommissions
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

const val ID_BRAND_ERROR = -1
const val DEFAULT_BASE_AMOUNT_STRING = "0.0"
const val DEFAULT_AMOUNT = "1.0" // change to 0 while backend is ready

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
            if (idCurrencyAccount == CurrencyType.Colon.id) {
                getExchangeRate()
            }
        }
    }

    var pkUser = 0
    var asset = ""
    private var cryptoNetWork = ""
    var idBrand = ID_BRAND_ERROR
    var user = ""
    var market = ""
    var identification = ""
    var accountToken = 0L
    private var side = ""
    var assetImageUrl = ""
    var idCurrencyAccount = CurrencyType.Colon.id
    var smartAccountAvailableBalance = 0.0

    private fun onSetUserData(
        pkUser: Int,
        asset: String?,
        cryptoNetwork: String?,
        idBrand: Int,
        user: String,
        market: String?,
        identification: String,
        accountToken: Long,
        side: String,
        assetImageUrl: String?,
        smartAccountAvailableBalance: Double,
        idCurrencyAccount: Int
    ) {
        this.pkUser = pkUser
        this.idCurrencyAccount = idCurrencyAccount
        this.isTimerRunning = true
        this.asset = asset ?: ""
        this.cryptoNetWork = cryptoNetwork ?: ""
        this.idBrand = idBrand
        this.user = user
        this.market = market ?: ""
        this.identification = identification
        this.accountToken = accountToken
        this.side = side
        this.assetImageUrl = assetImageUrl ?: ""
        if (idCurrencyAccount == CurrencyType.Colon.id) {
            convertColonesToDollars(smartAccountAvailableBalance)
        } else {
            this.smartAccountAvailableBalance = smartAccountAvailableBalance
        }
        if (isTimerRunning.not()) {
            updateDataWithNewExchangeRate()
            getExchangeRate()
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
            base_amount = uiState.baseAmount.value.ifEmpty { DEFAULT_BASE_AMOUNT_STRING }
                .toDouble(),
            quote_amount = uiState.quoteAmount.value.ifEmpty {
                if (uiState.baseAmount.value.isNotEmpty()) DEFAULT_BASE_AMOUNT_STRING else DEFAULT_AMOUNT
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

    private fun convertColonesToDollars(smartAccountAvailableBalance: Double): Unit = executeUseCase {
        getExchangeRate.invoke(
            user = user,
            identification = identification,
            idBrand = idBrand,
            abbreviation = CurrencyType.Dollar.disbursementValue,
            idOriginCurrency = CurrencyType.Colon.id.toString(),
            idDestinationCurrency = CurrencyType.Dollar.id.toString(),
            amount = smartAccountAvailableBalance
        ).collectLatest { result ->
            result.onLoading {
                this.smartAccountAvailableBalance = 0.0
            }
            result.onSuccess { exchangeRate ->
                this.smartAccountAvailableBalance = exchangeRate?.convertedAmount ?: 0.0
            }
            result.onFailure {
                this.smartAccountAvailableBalance = 0.0
            }
        }
    }

    private fun getExchangeRate(): Unit = executeUseCase {
        getExchangeRate.invoke(
            user = user,
            identification = identification,
            idBrand = idBrand,
            abbreviation = CurrencyType.Colon.disbursementValue,
            idOriginCurrency = CurrencyType.Colon.id.toString(),
            idDestinationCurrency = CurrencyType.Dollar.id.toString(),
            amount = uiState.quoteAmount.value.ifEmpty {
                uiState.pricesQuoteAndCommissions?.quote_amount.toString()
            }.toDouble()
        ).collectLatest { result ->
            result.onLoading { uiState = uiState.copy(isLoading = true) }
            result.onSuccess { exchangeRate ->
                uiState = uiState.copy(
                    isLoading = false,
                    exchangeRate = exchangeRate?.exchangeRate ?: 1.0
                )
            }
            result.onFailure {
                uiState = uiState.copy(isLoading = false)
                timer.cancel()
            }
        }
    }

    /*fun validateFormWhileDollars(): MutableState<Boolean> {
        val quoteAmount = uiState.quoteAmount.value.ifEmpty {
            uiState.pricesQuoteAndCommissions?.quote_amount.toString()
        }.toDoubleOrNull() ?: 0.0
        val available = smartAccountAvailableBalance
        val totalFee = uiState.pricesQuoteAndCommissions?.totalFee ?: 0.0
        return when {
            quoteAmount < 5.0 -> isError(
                errorMessage = R.string.crypto_purchase_flow_error_minimum_amount,
                isError = true
            )
            quoteAmount >= available -> isError(
                errorMessage = R.string.crypto_purchase_flow_error_available_amount,
                arg = available.toCurrencyFormat(),
                isError = true
            )
            quoteAmount >= (available + totalFee) -> isError(
                errorMessage = R.string.crypto_purchase_flow_error_available_amount_commission,
                isError = true
            )
            else -> isError()
        }
    }*/

    /*private fun isError(
        @StringRes errorMessage: Int = R.string.empty,
        arg: Any = Any(),
        isError: Boolean = false
    ): MutableState<Boolean> {
        if (!isError) return mutableStateOf(false)
        uiState = uiState.copy(
            error = ErrorTextHelper.ErrorTextResource(errorMessage, arg)
        )
        return mutableStateOf(true)
    }*/

    private fun purchaseCryptoCurrency() {
        executeUseCase {
            buyCryptoCurrencyUseCase.invoke(
                pkUser = pkUser,
                idBrand = idBrand,
                user = user,
                identification = identification,
                market = market,
                accountToken = accountToken,
                commissionAmount = 0.0,
                taxAmount = uiState.pricesQuoteAndCommissions?.taxAmount ?: 0.0,
                exchangeRate = if (idCurrencyAccount == CurrencyType.Dollar.id) 1.0 else uiState.exchangeRate,
                quoteId = uiState.pricesQuoteAndCommissions?.quote_id ?: "",
                quoteAmount = uiState.quoteAmount.value.toDouble(),
                fee = uiState.pricesQuoteAndCommissions?.fee?.toDouble() ?: 0.0,
                internalFee = uiState.pricesQuoteAndCommissions?.internal_fee ?: 0.0,
                totalFee = uiState.pricesQuoteAndCommissions?.totalFee ?: 0.0
            ).collectLatest { result ->
                result.onLoading {
                    uiState = uiState.copy(
                        isLoading = true,
                        isPurchaseInProcess = true
                    )
                }
                result.onSuccess {
                    uiState = uiState.copy(
                        isLoading = false,
                        isPurchaseInProcess = false,
                        isPurchaseSuccess = true
                    )
                }
                result.onFailure {
                    uiState = uiState.copy(
                        isLoading = false,
                        isPurchaseInProcess = false,
                        isPurchaseFailed = true
                    )
                }
            }
        }
    }

    data class UIState(
        val exchangeRate: Double = 1.0,
        val asset: String = "",
        val availableSmartAmount: Double = 0.0,
        val isConfirmationBottomSheetOpen: MutableState<Boolean> = mutableStateOf(false),
        val isLoading: Boolean = false,
        val isPurchaseInProcess: Boolean = false, // to handle loading screen after purchase
        val isPurchaseFailed : Boolean = false, // to handle error screen after purchase
        val isPurchaseSuccess : Boolean = false, // to handle success screen after purchase
        val error: ErrorTextHelper = ErrorTextHelper.ErrorText(""),
        val quoteAmount: MutableState<String> = mutableStateOf(""),
        val baseAmount: MutableState<String> = mutableStateOf(""),
        val pricesQuoteAndCommissions: PricesQuoteAndCommissions? = null
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnGetQuoteAndCommissions -> updateDataWithNewExchangeRate()
            UIEvent.OnPurchaseCryptoCurrency -> purchaseCryptoCurrency()
            is UIEvent.OnSetUserData -> onSetUserData(
                pkUser = event.pkUser,
                asset = event.asset,
                cryptoNetwork = event.cryptoNetwork,
                idBrand = event.idBrand,
                user = event.user,
                market = event.market,
                identification = event.identification,
                accountToken = event.accountToken,
                side = event.side,
                assetImageUrl = event.assetImageUrl,
                smartAccountAvailableBalance = event.smartAccountAvailableBalance,
                idCurrencyAccount = event.idCurrencyAccount
            )
            is UIEvent.OnSetQuoteAmount -> {}
        }
    }

    sealed class UIEvent {
        data class OnSetUserData(
            val pkUser: Int,
            val asset: String,
            val cryptoNetwork: String?,
            val idBrand: Int,
            val user: String,
            val market: String?,
            val identification: String,
            val accountToken: Long,
            val side: String,
            val assetImageUrl: String?,
            val smartAccountAvailableBalance: Double,
            val idCurrencyAccount: Int
        ) : UIEvent()

        object OnGetQuoteAndCommissions : UIEvent()
        data class OnSetQuoteAmount(val quoteAmount: Double) : UIEvent()
        object OnPurchaseCryptoCurrency : UIEvent()
    }
}

sealed interface ErrorTextHelper {
    data class ErrorText(val text: String) : ErrorTextHelper
    class ErrorTextResource(
        @StringRes val stringRes: Int,
        vararg val args: Any
    ) : ErrorTextHelper

    fun getErrorText(context: Context): String {
        return when (this) {
            is ErrorText -> text
            is ErrorTextResource -> context.getString(stringRes, *args)
        }
    }
}