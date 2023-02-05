package com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency

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
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.toCurrencyFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

const val ID_BRAND_ERROR = -1
const val DEFAULT_BASE_AMOUNT_STRING = "0.0"
const val DEFAULT_AMOUNT = "1.0" // change to 0 while backend is ready
const val DEFAULT_AMOUNT_NUMBER = 1.0
const val MINIMUM_AMOUNT_ALLOWED = 5.0

@HiltViewModel
class BuyCurrencyScreenViewModel @Inject constructor(
    private val getExchangeRate: QuerySmartExchangeRateUseCase,
    private val getPriceQuoteAndCommissionsUseCase: GetPriceQuoteAndCommissionsUseCase,
    private val buyCryptoCurrencyUseCase: BuyCryptoCurrencyUseCase,
) : BaseViewModel(false) {

    var uiState by mutableStateOf(UIState())
        private set

    var timerCount by mutableStateOf<Int?>(15)
        private set

    /*var confirmationTimerCount by mutableStateOf(5_000L)
        private set*/

    private val timer = object : CountDownTimer(15000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            timerCount = (millisUntilFinished / 1000).toInt()
        }

        override fun onFinish() {
            timerCount = null
            updateDataWithNewExchangeRate()
            if (idCurrencyAccount == CurrencyType.Colon.id) {
                getExchangeRate()
            }
        }
    }

    /*private val confirmationTimer = object : CountDownTimer(confirmationTimerCount, 1_000) {
        override fun onTick(millisUntilFinished: Long) {
            confirmationTimerCount = (millisUntilFinished / 1000)
        }

        override fun onFinish() {
            updateDataWithNewExchangeRate()
            if (idCurrencyAccount == CurrencyType.Colon.id) {
                getExchangeRate()
            }
            confirmationTimerCount = 20_000L
        }
    }*/

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
                timer.cancel()
                onFailure()
            }
        }
    }

    private fun convertColonesToDollars(smartAccountAvailableBalance: Double): Unit =
        executeUseCase {
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
                    onFailure()
                }
            }
        }

    private fun getExchangeRate(): Unit = executeUseCase {
        getExchangeRate.invoke(
            user = user,
            identification = identification,
            idBrand = idBrand,
            abbreviation = CurrencyType.Colon.disbursementValue,
            idOriginCurrency = CurrencyType.Dollar.id.toString(),
            idDestinationCurrency = CurrencyType.Colon.id.toString(),
            amount = 0.0
        ).collectLatest { result ->
            result.onLoading { uiState = uiState.copy(isLoading = true) }
            result.onSuccess { exchangeRate ->
                uiState = uiState.copy(
                    isLoading = false,
                    exchangeRate = exchangeRate?.exchangeRate ?: 1.0
                )
            }
            result.onFailure {
                timer.cancel()
                onFailure()
            }
        }
    }

    private fun validateAmountInput(amount: String) {
        val quoteAmount = if (uiState.isTransformationCurrency.value.not()) {
            amount.ifEmpty { DEFAULT_BASE_AMOUNT_STRING }.toDouble()
        } else {
            amount.ifEmpty { DEFAULT_AMOUNT }.toDouble()
                .times(uiState.pricesQuoteAndCommissions?.price ?: DEFAULT_AMOUNT_NUMBER)
        }
        val amountPlusFee = amount.ifEmpty { DEFAULT_BASE_AMOUNT_STRING }.toDouble()
            .plus(uiState.pricesQuoteAndCommissions?.totalFee ?: 0.0)

        when {
            quoteAmount < MINIMUM_AMOUNT_ALLOWED -> isError(
                errorMessage = R.string.crypto_purchase_flow_error_minimum_amount,
                isError = true
            )
            quoteAmount >= smartAccountAvailableBalance -> isError(
                errorMessage = R.string.crypto_purchase_flow_error_available_amount,
                arg = smartAccountAvailableBalance.toCurrencyFormat(),
                isError = true
            )
            amountPlusFee >= smartAccountAvailableBalance -> isError(
                errorMessage = R.string.crypto_purchase_flow_error_available_amount_commission,
                isError = true
            )
            else -> isError()
        }
    }

    private fun isError(
        @StringRes errorMessage: Int = R.string.empty,
        arg: Any = Any(),
        isError: Boolean = false
    ) {
        uiState = uiState.copy(
            error = errorMessage,
            errorMessageArg = arg,
            isError = isError
        )
    }

    private fun purchaseCryptoCurrency() {
        executeUseCase {
            buyCryptoCurrencyUseCase.invoke(
                pkUser = pkUser,
                idBrand = idBrand,
                user = user,
                identification = identification,
                market = market,
                accountToken = accountToken,
                commissionAmount = uiState.pricesQuoteAndCommissions?.fee?.toDouble() ?: 0.0,
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

    private fun onFailure() {
        uiState = uiState.copy(
            isLoading = false,
            openDialog = DialogParameters(
                descriptionResource = R.string.error_occurred_title,
                negativeResource = R.string.error_button_try_later,
                positiveResource = R.string.error_button_retry,
                isActive = mutableStateOf(true),
                negativeAction = {
                      uiState.failureAction()
                },
                positiveAction = {
                    timerCount = null
                    updateDataWithNewExchangeRate()
                    if (idCurrencyAccount == CurrencyType.Colon.id) {
                        getExchangeRate()
                    }
                }
            )
        )
    }

    data class UIState(
        val exchangeRate: Double = 1.0,
        val asset: String = "",
        val availableSmartAmount: Double = 0.0,
        val isConfirmationBottomSheetOpen: MutableState<Boolean> = mutableStateOf(false),
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val failureAction: () -> Unit = {},
        val isPurchaseInProcess: Boolean = false, // to handle loading screen after purchase
        val isPurchaseFailed: Boolean = false, // to handle error screen after purchase
        val isPurchaseSuccess: Boolean = false, // to handle success screen after purchase
        val isError: Boolean = false,
        @StringRes val error: Int = R.string.empty,
        val errorMessageArg: Any = Any(),
        val quoteAmount: MutableState<String> = mutableStateOf(""),
        val baseAmount: MutableState<String> = mutableStateOf(""),
        val isTransformationCurrency: MutableState<Boolean> = mutableStateOf(false),
        val textIsNotEmpty: Boolean = quoteAmount.value.isNotEmpty() || baseAmount.value.isNotEmpty(),
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
            UIEvent.OnGetExchangeRate -> if (uiState.exchangeRate == 1.0) {
                getExchangeRate()
            }
            is UIEvent.ValidateAmountInput -> validateAmountInput(event.amount)
            is UIEvent.OnSetFailureAction -> uiState = uiState.copy(
                failureAction = event.failureAction
            )
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
        data class ValidateAmountInput(val amount: String) : UIEvent()
        data class OnSetFailureAction(val failureAction: () -> Unit) : UIEvent()
        object OnGetQuoteAndCommissions : UIEvent()
        object OnPurchaseCryptoCurrency : UIEvent()
        object OnGetExchangeRate : UIEvent()
    }
}