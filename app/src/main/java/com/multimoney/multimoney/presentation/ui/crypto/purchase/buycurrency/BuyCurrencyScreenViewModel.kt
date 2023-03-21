package com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency

import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.PurchaseStatus
import com.multimoney.domain.interaction.accountsmart.QuerySmartExchangeRateUseCase
import com.multimoney.domain.interaction.crypto.BuyCryptoCurrencyUseCase
import com.multimoney.domain.interaction.crypto.GetPriceQuoteAndCommissionsUseCase
import com.multimoney.domain.model.crypto.BuyCryptoRequest
import com.multimoney.domain.model.crypto.PricesQuoteAndCommissions
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.crypto.CryptoProcessErrorCodes
import com.multimoney.multimoney.presentation.util.calculateAmountPlusFee
import com.multimoney.multimoney.presentation.util.calculateQuote
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.format
import com.multimoney.multimoney.presentation.util.toCurrencyFormat
import com.multimoney.multimoney.presentation.util.toJson
import com.multimoney.multimoney.util.CryptoTimerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class BuyCurrencyScreenViewModel @Inject constructor(
    private val getExchangeRate: QuerySmartExchangeRateUseCase,
    private val getPriceQuoteAndCommissionsUseCase: GetPriceQuoteAndCommissionsUseCase,
    private val buyCryptoCurrencyUseCase: BuyCryptoCurrencyUseCase,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(false) {

    var uiState by mutableStateOf(UIState())
        private set

    //share properties
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
    var accountNumber = ""
    var openMaintenanceAction = {}

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
        idCurrencyAccount: Int,
        ibanAccountNumber: String,
        openMaintenanceAction: () -> Unit
    ) {
        this.openMaintenanceAction = openMaintenanceAction
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
        this.accountNumber = ibanAccountNumber
        if (idCurrencyAccount == CurrencyType.Colon.id) {
            convertColonesToDollars(smartAccountAvailableBalance)
        } else {
            this.smartAccountAvailableBalance = smartAccountAvailableBalance
        }
    }

    private val timer = CryptoTimerHelper(
        coroutineScope = viewModelScope,
        time = DEFAULT_TIMER_COUNT,
        onTick = { seconds ->
            val remainingTime = seconds.seconds
            uiState = uiState.copy(
                remainingTime = remainingTime,
                remainingTimeText = remainingTime.format()
            )
        },
        onFinished = {
            updateUiWithNewPricesAndCommissions()
            if (idCurrencyAccount == CurrencyType.Colon.id) {
                getExchangeRate()
            }
        }
    )

    private val confirmationTimer = CryptoTimerHelper(
        coroutineScope = viewModelScope,
        time = CONFIRMATION_BOTTOM_SHEET_INITIAL_TIMER_COUNT,
        isBottomSheetOpen = true,
        onTick = { seconds ->
            val remainingTime = seconds.seconds
            uiState = uiState.copy(
                remainingTime = remainingTime,
                remainingTimeText = remainingTime.format()
            )
        },
        onFinished = {
            updateUiWithNewPricesAndCommissions()
            if (idCurrencyAccount == CurrencyType.Colon.id) {
                getExchangeRate()
            }
        }
    )

    private fun updateUiWithNewPricesAndCommissions(): Unit = executeUseCase {
        getPriceQuoteAndCommissionsUseCase.invoke(
            asset = asset,
            crypto_network = cryptoNetWork,
            idBrand = idBrand,
            user = user,
            market = market,
            identification = identification,
            side = side,
            base_amount = uiState.baseAmount.value.ifEmpty {
                DEFAULT_BASE_AMOUNT_STRING
            }.toDouble(),
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
                if (!uiState.isConfirmationBottomSheetOpen) {
                    timer.startTimer()
                } else {
                    confirmationTimer.startTimer()
                }
            }
            result.onFailure {
                timer.stopTimer()
                confirmationTimer.stopTimer()
                if (it.errorCode == CryptoProcessErrorCodes.Maintenance.status) {
                    openMaintenanceAction()
                    return@onFailure
                }
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
                    if (smartAccountAvailableBalance == 0.0) {
                        onFailure()
                        return@onSuccess
                    }
                    this.smartAccountAvailableBalance = exchangeRate?.convertedAmount ?: 0.0
                }
                result.onFailure {
                    timer.stopTimer()
                    confirmationTimer.stopTimer()
                    this.smartAccountAvailableBalance = 0.0
                    if (it.errorCode == CryptoProcessErrorCodes.Maintenance.status) {
                        openMaintenanceAction()
                        return@onFailure
                    }
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
                    isLoading = false, exchangeRate = exchangeRate?.exchangeRate ?: 1.0
                )
            }
            result.onFailure {
                timer.stopTimer()
                confirmationTimer.stopTimer()
                if (it.errorCode == CryptoProcessErrorCodes.Maintenance.status) {
                    openMaintenanceAction()
                    return@onFailure
                }
                onFailure()
            }
        }
    }

    private fun validateAmountInput(amount: String) {
        val quoteAmount = calculateQuote(
            isTransformationCurrency = uiState.isTransformationCurrency.value,
            amount = amount,
            price = uiState.pricesQuoteAndCommissions?.price ?: DEFAULT_AMOUNT_NUMBER
        )
        val amountPlusFee = calculateAmountPlusFee(
            amount = amount,
            fee = uiState.pricesQuoteAndCommissions?.totalFee
        )
        uiState = uiState.copy(amountInUSD = quoteAmount, amountPlusFee = amountPlusFee)


        when {
            amount.isEmpty() -> isError(isError = true, focusError = false)
            quoteAmount < MINIMUM_AMOUNT_ALLOWED -> isError(
                errorMessage = R.string.crypto_purchase_flow_error_minimum_amount,
                isError = true,
                focusError = true
            )
            quoteAmount >= smartAccountAvailableBalance -> isError(
                errorMessage = R.string.crypto_purchase_flow_error_available_amount,
                arg = smartAccountAvailableBalance.toCurrencyFormat(),
                isError = true,
                focusError = true
            )
            amountPlusFee >= smartAccountAvailableBalance -> isError(
                errorMessage = R.string.crypto_purchase_flow_error_available_amount_commission,
                isError = true,
                focusError = true
            )
            else -> isError()
        }
    }

    private fun isError(
        @StringRes errorMessage: Int = R.string.empty,
        arg: Any = Any(),
        isError: Boolean = false,
        focusError: Boolean = false
    ) {
        uiState = uiState.copy(
            error = errorMessage,
            errorMessageArg = arg,
            isError = isError,
            focusError = focusError
        )
    }

    private fun setBuyCryptoRequest() {
        uiState = uiState.copy(
            buyCryptoRequest = BuyCryptoRequest(
                pkUser = pkUser,
                idBrand = idBrand,
                user = user,
                identification = identification,
                market = market,
                accountToken = accountToken,
                commissionAmount = uiState.pricesQuoteAndCommissions?.internal_fee ?: 0.0,
                taxAmount = uiState.pricesQuoteAndCommissions?.taxAmount ?: 0.0,
                exchangeRate = if (idCurrencyAccount == CurrencyType.Dollar.id) 1.0 else uiState.exchangeRate,
                quoteId = uiState.pricesQuoteAndCommissions?.quote_id ?: "",
                quoteAmount = calculateQuote(
                    quoteAmount = uiState.quoteAmount.value,
                    baseAmount = uiState.baseAmount.value,
                    price = uiState.pricesQuoteAndCommissions?.price ?: DEFAULT_AMOUNT_NUMBER,
                ),
                fee = uiState.pricesQuoteAndCommissions?.fee?.toDouble() ?: 0.0,
                internalFee = uiState.pricesQuoteAndCommissions?.internal_fee ?: 0.0,
                totalFee = uiState.pricesQuoteAndCommissions?.totalFee ?: 0.0
            )
        )
    }

    private fun registerAdjustPurchaseCrypto() = viewModelScope.launch {
        if (dataStorePreferences.isAdjustCryptoSuccessPurchaseFirstTime().firstOrNull() == false) {
            dataStorePreferences.setAdjustCryptoSuccessPurchaseFirstTime(true)
            setBuyCryptoRequest()
            registerAdjustEvent(
                adjustEventType = AdjustEventType.PURCHASE_CRYPTO_FIRST_TIME_SUCCESS_PURCHASE,
                listParameters = listOf(
                    Pair("market", market),
                    Pair("user", user),
                    Pair("accountToken", accountToken.toString()),
                    Pair("commissionAmount",
                        uiState.pricesQuoteAndCommissions?.internal_fee.toString()
                    ),
                    Pair("taxAmount",
                        uiState.pricesQuoteAndCommissions?.taxAmount.toString()
                    ),
                    Pair("exchangeRate", if (idCurrencyAccount == CurrencyType.Dollar.id)
                        1.0.toString() else uiState.exchangeRate.toString()
                    ),
                    Pair("quoteId", uiState.pricesQuoteAndCommissions?.quote_id ?: ""),
                    Pair("quoteAmount", calculateQuote(
                        quoteAmount = uiState.quoteAmount.value,
                        baseAmount = uiState.baseAmount.value,
                        price = uiState.pricesQuoteAndCommissions?.price ?: DEFAULT_AMOUNT_NUMBER,
                    ).toString()),
                    Pair("fee", uiState.pricesQuoteAndCommissions?.fee ?: "0.0"),
                    Pair("internalFee", uiState.pricesQuoteAndCommissions?.internal_fee.toString()),
                    Pair("totalFee", uiState.pricesQuoteAndCommissions?.totalFee.toString())
                ),
                data = uiState.buyCryptoRequest?.toJson() ?: ""
            )
        }
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
                commissionAmount = uiState.pricesQuoteAndCommissions?.internal_fee ?: 0.0,
                taxAmount = uiState.pricesQuoteAndCommissions?.taxAmount ?: 0.0,
                exchangeRate = if (idCurrencyAccount == CurrencyType.Dollar.id) 1.0 else uiState.exchangeRate,
                quoteId = uiState.pricesQuoteAndCommissions?.quote_id ?: "",
                quoteAmount = calculateQuote(
                    quoteAmount = uiState.quoteAmount.value,
                    baseAmount = uiState.baseAmount.value,
                    price = uiState.pricesQuoteAndCommissions?.price ?: DEFAULT_AMOUNT_NUMBER,
                ),
                fee = uiState.pricesQuoteAndCommissions?.fee?.toDouble() ?: 0.0,
                internalFee = uiState.pricesQuoteAndCommissions?.internal_fee ?: 0.0,
                totalFee = uiState.pricesQuoteAndCommissions?.totalFee ?: 0.0
            ).collectLatest { result ->
                result.onLoading {
                    uiState = uiState.copy(
                        isLoading = true,
                        purchaseStatus = PurchaseStatus.LOADING
                    )
                }
                result.onSuccess {
                    when (it.buyHQR.status) {
                        CryptoProcessErrorCodes.WeeklyLimitExceeded.status -> {
                            uiState = uiState.copy(
                                error = R.string.crypto_purchase_flow_error_weekly_amount_exceeded,
                                focusError = true,
                                isLoading = false,
                                purchaseStatus = PurchaseStatus.IDLE
                            )
                            return@onSuccess
                        }
                        CryptoProcessErrorCodes.InsufficientFundsBuy.status -> {
                            uiState = uiState.copy(
                                error = R.string.crypto_purchase_flow_error_no_funds,
                                focusError = true,
                                isLoading = false,
                                purchaseStatus = PurchaseStatus.IDLE
                            )
                            return@onSuccess
                        }
                        CryptoProcessErrorCodes.ExpiredPriceBuy.status -> {
                            uiState = uiState.copy(
                                error = R.string.crypto_purchase_flow_error_price_expired,
                                focusError = true,
                                isLoading = false,
                                purchaseStatus = PurchaseStatus.IDLE
                            )
                            return@onSuccess
                        }
                    }
                    uiState = uiState.copy(
                        isLoading = false,
                        referenceNumber = it.buyHQR.result?.sysdeTransactionNumber,
                        purchaseStatus = PurchaseStatus.SUCCESS
                    )
                }
                result.onFailure {
                    if (it.errorCode == CryptoProcessErrorCodes.Maintenance.status) {
                        openMaintenanceAction()
                        return@onFailure
                    }
                    uiState = uiState.copy(
                        isLoading = false,
                        purchaseStatus = PurchaseStatus.FAILED
                    )
                }
            }
        }
    }

    private fun onFailure() {
        uiState = uiState.copy(
            isLoading = false,
            openDialog = DialogParameters(descriptionResource = R.string.error_occurred_title,
                negativeResource = R.string.error_button_try_later,
                positiveResource = R.string.error_button_retry,
                isActive = mutableStateOf(true),
                negativeAction = {
                    uiState.failureAction()
                },
                positiveAction = {
                    updateUiWithNewPricesAndCommissions()
                    if (idCurrencyAccount == CurrencyType.Colon.id) {
                        convertColonesToDollars(smartAccountAvailableBalance)
                        getExchangeRate()
                    }
                })
        )
    }

    private fun clearInputData() {
        timer.stopTimer()
        confirmationTimer.stopTimer()
        uiState = uiState.copy(
            baseAmount = mutableStateOf(""),
            quoteAmount = mutableStateOf(""),
            remainingTime = Duration.ZERO
        )
    }

    data class UIState(
        // ** mutable data
        val exchangeRate: Double = 1.0,
        val asset: String = "",
        val availableSmartAmount: Double = 0.0,
        val quoteAmount: MutableState<String> = mutableStateOf(""),
        val baseAmount: MutableState<String> = mutableStateOf(""),
        val pricesQuoteAndCommissions: PricesQuoteAndCommissions? = null,
        val buyCryptoRequest: BuyCryptoRequest? = null,
        // ** interactions
        val isConfirmationBottomSheetOpen: Boolean = false,
        val isLoading: Boolean = false,
        //* timer
        val remainingTime: Duration = Duration.ZERO,
        val remainingTimeText: String = remainingTime.format(),
        // timer *
        val openDialog: DialogParameters = DialogParameters(),
        val failureAction: () -> Unit = {},
        //** validations
        val focusError: Boolean = false,
        val isError: Boolean = false,
        @StringRes val error: Int = R.string.empty,
        val errorMessageArg: Any = Any(),
        val isTransformationCurrency: MutableState<Boolean> = mutableStateOf(false),
        //voucher information
        val referenceNumber: String? = null,
        val amountInUSD: Double? = null,
        val amountPlusFee: Double? = null,
        val purchaseStatus: PurchaseStatus = PurchaseStatus.IDLE
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnGetQuoteAndCommissions -> updateUiWithNewPricesAndCommissions()
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
                idCurrencyAccount = event.idCurrencyAccount,
                ibanAccountNumber = event.ibanAccountNumber,
                openMaintenanceAction = event.openMaintenanceAction
            )
            UIEvent.OnGetExchangeRate -> if (uiState.exchangeRate == 1.0) {
                getExchangeRate()
            }
            is UIEvent.ValidateAmountInput -> validateAmountInput(event.amount)
            is UIEvent.OnSetFailureAction -> uiState = uiState.copy(
                failureAction = event.failureAction
            )
            UIEvent.OnOpenPurchaseConfirmationBottomSheet -> {
                timer.stopTimer()
                uiState = uiState.copy(
                    isConfirmationBottomSheetOpen = true
                )
                confirmationTimer.startTimer()
            }
            UIEvent.OnClosePurchaseConfirmationBottomSheet -> {
                confirmationTimer.stopTimer()
                uiState = uiState.copy(
                    isConfirmationBottomSheetOpen = false
                )
                timer.startTimer()
            }
            UIEvent.OnClearInputData -> clearInputData()
            UIEvent.OnRegisterAdjustPurchase -> registerAdjustPurchaseCrypto()
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
            val idCurrencyAccount: Int,
            val ibanAccountNumber: String,
            val openMaintenanceAction: () -> Unit
        ) : UIEvent()

        data class ValidateAmountInput(val amount: String) : UIEvent()
        data class OnSetFailureAction(val failureAction: () -> Unit) : UIEvent()
        object OnGetQuoteAndCommissions : UIEvent()
        object OnPurchaseCryptoCurrency : UIEvent()
        object OnGetExchangeRate : UIEvent()
        object OnOpenPurchaseConfirmationBottomSheet : UIEvent()
        object OnClosePurchaseConfirmationBottomSheet : UIEvent()
        object OnClearInputData : UIEvent()
        object OnRegisterAdjustPurchase : UIEvent()
    }

    companion object {
        const val ID_BRAND_ERROR = -1
        const val DEFAULT_BASE_AMOUNT_STRING = "0.0"
        const val DEFAULT_AMOUNT = "1.0" // change to 0 while backend is ready
        const val DEFAULT_AMOUNT_NUMBER = 1.0
        const val MINIMUM_AMOUNT_ALLOWED = 5.0
        const val DEFAULT_TIMER_COUNT = 15
        const val CONFIRMATION_BOTTOM_SHEET_INITIAL_TIMER_COUNT = 5
    }
}