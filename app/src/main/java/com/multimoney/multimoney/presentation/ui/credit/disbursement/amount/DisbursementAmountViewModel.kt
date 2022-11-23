package com.multimoney.multimoney.presentation.ui.credit.disbursement.amount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.credit.MutationSaveCreditExtensionDetailUseCase
import com.multimoney.domain.interaction.credit.QueryCreditExtensionAmountUseCase
import com.multimoney.domain.interaction.credit.QueryCreditExtensionMessageUseCase
import com.multimoney.domain.model.balance.Summary
import com.multimoney.domain.model.credit.CreditExtensionAmount
import com.multimoney.domain.model.credit.CreditExtensionDetail
import com.multimoney.domain.model.credit.CreditExtensionMessage
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.CREDIT_NUMBER
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.SUMMARY_LIST
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.UIEvent.OnCurrencyIndexChanged
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.UIEvent.OnDisbursementValueChange
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.UIEvent.OnDisbursementValueChangeFinished
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.UIEvent.OnOpenConditionCreditDialog
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.UIEvent.OnSliderValueChange
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.UIEvent.OnSliderValueChangeFinished
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrency
import com.multimoney.multimoney.presentation.util.tickerFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class DisbursementAmountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val queryCreditExtensionAmountUseCase: QueryCreditExtensionAmountUseCase,
    val queryCreditExtensionMessageUseCase: QueryCreditExtensionMessageUseCase,
    val mutationSaveCreditExtensionDetailUseCase: MutationSaveCreditExtensionDetailUseCase
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var minimumDisbursement = 0F
    private var maximumDisbursement = 0F
    private var disbursementProgressFactorErrorMessage = 0
    private var minimumDisbursementErrorMessage = 0
    private var maximumDisbursementErrorMessage = 0
    private var fee: Double = 0.0
    private var sliderFactor = 0.0
    private var remainingTime: Duration = TIMER_DURATION.milliseconds
    private var isTimerRunning: Boolean = false

    private var idBrand: Int? = null
    private var user: String? = ""
    private var idClient: Int? = null
    private var currencyItems: List<Int>? = listOf()
    private var summary: List<Summary>? = listOf()
    private var pkUser: Int? = null
    private var creditNumber: String? = null
    private var creditExtensionAmount: CreditExtensionAmount? = null
    private var creditExtensionMessage: CreditExtensionMessage? = null
    private var creditExtensionDetail: CreditExtensionDetail? = null

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        user = savedStateHandle[USER]
        idClient = savedStateHandle[ID_CLIENT]
        summary = savedStateHandle.get<Array<Summary>>(SUMMARY_LIST)?.toList()
        currencyItems = savedStateHandle.get<Array<Summary>>(SUMMARY_LIST)?.toList()?.map { it.idCurrency ?: 0 }
        pkUser = savedStateHandle.get<String>(PK_USER)?.toInt()
        creditNumber = savedStateHandle[CREDIT_NUMBER]
    }

    private fun onStart() {
        uiState = uiState.copy(
            titleResource = when (idBrand) {
                Brand.Guatemala.id -> R.string.disbursement_amount_gt_title
                else -> R.string.disbursement_amount_title
            },
            isMultipleCurrency = (currencyItems?.lastIndex ?: INITIAL_CURRENCY_INDEX) > INITIAL_CURRENCY_INDEX,
            currencyItems = currencyItems?.map { it.getCurrency().symbol } ?: listOf(),
            currencyIndex = INITIAL_CURRENCY_INDEX
        )
        callCreditExtensionAmount()
    }

    private fun onCloseClick() = navigateBack(popTo = Screen.HomeScreen.route, isRestart = false)

    private fun isFormValid() {
        uiState = uiState.copy(
            isFormValid = when {
                uiState.disbursement.isBlank() -> false
                uiState.disbursementError.first -> false
                else -> true
            }
        )
    }

    private fun isTimerTick() =
        remainingTime.inWholeMilliseconds > 0

    private fun onTimerTick() {
        remainingTime = remainingTime.minus(TIMER_DELAY.milliseconds)
        uiState = uiState.copy(sliderValue = uiState.sliderValue + SLIDER_ANIMATION_VALUE)
    }

    private fun onTimerFinish() {
        isTimerRunning = false
        uiState = uiState.copy(sliderValue = getSliderValue(maximumDisbursement, uiState.progressFactor))
    }

    private fun onExecuteTimer() {
        isTimerRunning = true
        tickerFlow(
            period = TIMER_DELAY.milliseconds,
            duration = TIMER_DURATION.milliseconds
        )
            .takeWhile { isTimerRunning }
            .map {
                LocalDateTime.now()
            }
            .distinctUntilChanged { old, new ->
                old.nano == new.nano
            }
            .onEach {
                if (isTimerTick()) {
                    onTimerTick()
                } else if (isTimerRunning) {
                    onTimerFinish()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun onCurrencyIndexChange(index: Int) {
        uiState = uiState.copy(currencyIndex = index)
        callCreditExtensionAmount()
    }

    private fun callCreditExtensionAmount() = executeUseCase {
        queryCreditExtensionAmountUseCase.invoke(
            idClient = idClient?.toLong() ?: 0,
            currency = currencyItems?.get(uiState.currencyIndex)?.getCurrency()?.disbursementValue.orEmpty(),
            user = user ?: "",
            idBrand = idBrand ?: 0
        ).collectLatest { result ->
            result.onSuccess {
                creditExtensionAmount = it
                minimumDisbursement = it?.amountMin?.toFloat() ?: 0F
                maximumDisbursement = it?.amountMax?.toFloat() ?: 0F
                uiState = uiState.copy(
                    minimumDisbursementLabel = it?.labelAmountMinAvailable ?: "",
                    maximumDisbursementLabel = it?.labelAmountMaxAvailable ?: "",
                    sliderValueRangeInitial = getSliderValue(minimumDisbursement, it?.amountTract?.toDouble() ?: 0.0),
                    sliderValue = if (isTimerRunning.not()) {
                        getSliderValue(maximumDisbursement, it?.amountTract?.toDouble() ?: 0.0)
                    } else {
                        uiState.sliderValue
                    },
                    progressFactor = it?.amountTract?.toDouble() ?: 0.0,
                    disbursement = maximumDisbursement.toString()
                )
                callCreditExtensionMessage()
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    openDialog = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun callCreditExtensionMessage() = executeUseCase {
        queryCreditExtensionMessageUseCase.invoke(
            idClient = idClient?.toLong() ?: 0,
            currency = currencyItems?.get(uiState.currencyIndex)?.getCurrency()?.disbursementValue.orEmpty(),
            user = user ?: "",
            idBrand = idBrand ?: 0,
            amountRequest = uiState.disbursement.toDouble(),
            idLoanClient = creditExtensionAmount?.idLoanClient ?: 0,
            quotaMax = creditExtensionAmount?.quotaMax ?: 0.0,
            idProductBase = creditExtensionAmount?.idProductBase ?: 0,
            cicle = creditExtensionAmount?.cicle ?: 0
        ).collectLatest { result ->
            result.onSuccess {
                creditExtensionMessage = it
                val product = it?.product?.first()
                fee = product?.quotaTotal ?: 0.0
                uiState = uiState.copy(
                    feeLabel = product?.strQuotaTotal.orEmpty(),
                    regularInterestRateLabel = product?.strRateInterestNormal.orEmpty(),
                    termLabel = product?.month.toString(),
                    commissionDisbursementLabel = product?.strComissionDisbursement.orEmpty()
                )
                sliderFactor = getSliderFactor(creditExtensionAmount?.amountTract?.toDouble() ?: 0.0)
                onExecuteTimer()
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    openDialog = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun callMutationSaveCreditExtensionDetail() = executeUseCase {
        val product = creditExtensionMessage?.product?.first()
        mutationSaveCreditExtensionDetailUseCase(
            pkUser = pkUser ?: 0,
            idBrand = idBrand ?: 0,
            user = user.orEmpty(),
            accountNumber = creditNumber.orEmpty(),
            amount = uiState.disbursement.toDouble(),
            month = product?.month?.toInt() ?: 0,
            pkPromotionMonth = creditExtensionMessage?.pkPromotionMonth ?: 0,
            nextPaymentDate = product?.datePayActuality.orEmpty(),
            quota = product?.quota ?: 0.0,
            quotaTotal = product?.quotaTotal ?: 0.0,
            comissionDisbursement = product?.comissionDisbursement?.toDouble() ?: 0.0,
            rateInterestNormalLoan = product?.rateInterestNormal?.toDouble() ?: 0.0,
            rateInterestNormalRegular = product?.rateInterestNormal?.toDouble() ?: 0.0,
            cicle = product?.cicle ?: 0,
            idProduct = creditExtensionAmount?.idProductBase ?: 0,
            descriptionPromotionTerm = product?.descriptionPromotionTerm.orEmpty(),
            pkPromotion = product?.pkPromotion ?: 0
        ).collectLatest { result ->
            result.onSuccess {
                creditExtensionDetail = it
                navigateTo(
                    route = "${Screen.DisbursementAccountScreen.baseRoute}/$idBrand/$user/$idClient/${
                    encodeData(
                        summary
                    )
                    }/${it?.nextPayment}/${it?.quotaTotal}/${it?.selectedAmount}"
                )
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    openDialog = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun onDisbursementValueChangeFinished(value: String) {
        if (isTimerRunning.not()) {
            if (value.isEmpty() || value.toFloat() < minimumDisbursement) {
                uiState = uiState.copy(
                    disbursementError = Pair(true, minimumDisbursementErrorMessage),
                    sliderValue = uiState.sliderValueRangeInitial
                )
            } else if (value.toFloat() > maximumDisbursement) {
                uiState = uiState.copy(
                    disbursementError = Pair(true, maximumDisbursementErrorMessage),
                    sliderValue = SLIDER_TOTAL.toFloat()
                )
            } else if (isDisbursementMultipleOfProgressFactor(value.toFloat()).not()) {
                uiState = uiState.copy(
                    disbursementError = Pair(true, disbursementProgressFactorErrorMessage),
                    sliderValue = getSliderValue(value.toFloat(), uiState.progressFactor)
                )
            } else {
                uiState = uiState.copy(
                    disbursementError = Pair(false, R.string.error_empty),
                    sliderValue = getSliderValue(value.toFloat(), uiState.progressFactor)
                )
                callCreditExtensionMessage()
            }
        }
        isFormValid()
    }

    /**
     * Divide the maximum disbursement between the progress factor to get the amount of times that
     * progress can change. After we divide the total slider value to get the sliderFactor
     * */
    private fun getSliderFactor(progressFactor: Double) = SLIDER_TOTAL / (maximumDisbursement / progressFactor)

    /**
     * Verify if disbursement is multiple of progress factor for example: progressFactor is 10 then
     * we have to get module of disbursement that could be 1000 if module is equal to 0 that means
     * that is multiple of progress factor
     **/
    private fun isDisbursementMultipleOfProgressFactor(value: Float) = (value % uiState.progressFactor).toInt() == 0

    /**
     * Get slider initial value to exclude the disbursement the user could not select
     **/
    private fun getSliderValue(disbursement: Float, progressFactor: Double): Float {
        val sliderFactorTimes = (disbursement / progressFactor.toFloat())
        return sliderFactorTimes * sliderFactor.toFloat()
    }

    private fun onSliderValueChange(value: Float) {
        uiState = if (isTimerRunning.not() && sliderFactor > 0.0) {
            val sliderFactorTimes = (value / sliderFactor).roundToInt()
            val disbursement = if (value == 0f) {
                minimumDisbursement.roundToInt().toString()
            } else if (value >= uiState.sliderValueRangeInitial) {
                (uiState.progressFactor * sliderFactorTimes).roundToInt().toString()
            } else {
                uiState.disbursement
            }
            uiState.copy(sliderValue = value, disbursement = disbursement)
        } else {
            uiState.copy(sliderValue = value)
        }
    }

    private fun onSliderValueChangeFinished() {
        uiState = uiState.copy(disbursementError = Pair(false, R.string.empty))
        if (uiState.disbursementError.first.not() && isTimerRunning.not()) {
            callCreditExtensionMessage()
        }
    }

    private fun onOpenConditionCreditDialog() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = string.disbursement_amount_info_dialog_title,
                descriptionResource = string.disbursement_amount_info_dialog_description,
                isActive = mutableStateOf(true),
                positiveResource = string.accept
            )
        )
    }

    private fun onContinueClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        callMutationSaveCreditExtensionDetail()
    }

    data class UIState(
        // Fields
        val titleResource: Int = R.string.empty,
        val isFormValid: Boolean = false,
        val isMultipleCurrency: Boolean = false,
        val currencyIndex: Int = 0,
        val currencyItems: List<String> = listOf("", ""),
        val feeLabel: String = "",
        val disbursement: String = "",
        val disbursementError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
        val minimumDisbursementLabel: String = "",
        val maximumDisbursementLabel: String = "",
        var progressFactor: Double = 0.0,
        val sliderValue: Float = SLIDER_INITIAL_VALUE,
        val sliderValueRangeInitial: Float = 0F,
        val termLabel: String = "",
        val regularInterestRateLabel: String = "",
        val commissionDisbursementLabel: String = "",
        val isLoading: Boolean = true,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnStart -> onStart()
            is OnCloseClick -> onCloseClick()
            is OnValidateForm -> isFormValid()
            is OnDisbursementValueChange -> uiState = uiState.copy(disbursement = uiEvent.value)
            is OnDisbursementValueChangeFinished -> onDisbursementValueChangeFinished(uiEvent.value)
            is OnCurrencyIndexChanged -> onCurrencyIndexChange(uiEvent.index)
            is OnSliderValueChange -> onSliderValueChange(uiEvent.value)
            is OnSliderValueChangeFinished -> onSliderValueChangeFinished()
            is OnOpenConditionCreditDialog -> onOpenConditionCreditDialog()
            is OnContinueClick -> onContinueClick(uiEvent.focusManager)
        }
    }

    sealed class UIEvent {
        object OnStart : UIEvent()
        object OnCloseClick : UIEvent()
        object OnValidateForm : UIEvent()
        data class OnCurrencyIndexChanged(val index: Int) : UIEvent()
        data class OnDisbursementValueChange(val value: String) : UIEvent()
        data class OnDisbursementValueChangeFinished(val value: String) : UIEvent()
        data class OnSliderValueChange(val value: Float) : UIEvent()
        data class OnContinueClick(val focusManager: FocusManager) : UIEvent()
        object OnSliderValueChangeFinished : UIEvent()
        object OnOpenConditionCreditDialog : UIEvent()
    }

    companion object {
        const val INITIAL_CURRENCY_INDEX = 0
        const val CURRENCY_SEPARATOR = ','
        const val SLIDER_TOTAL = 1
        const val SLIDER_INITIAL_VALUE = 0.0F
        const val SLIDER_ANIMATION_VALUE = 0.05F
        const val TIMER_DURATION = 20L
        const val TIMER_DELAY = 1L
    }
}
