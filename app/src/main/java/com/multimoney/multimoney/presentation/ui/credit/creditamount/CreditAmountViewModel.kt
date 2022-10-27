package com.multimoney.multimoney.presentation.ui.credit.creditamount

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.catalog.SmartStep
import com.multimoney.domain.interaction.credit.MutationSaveCreditApplicationUseCase
import com.multimoney.domain.interaction.credit.QueryCreditOfferUseCase
import com.multimoney.domain.interaction.credit.QueryPaymentAmountUseCase
import com.multimoney.domain.interaction.credit.QueryScreenConfigUseCase
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.credit.Product
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.BaseEvent.OnOpenConditionOfCreditDialog
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnCallMutationSaveCreditApplicationUseCase
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnCallQueryCreditOfferUseCase
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnCurrencyIndexChanged
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnDisbursementValueChange
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnDisbursementValueChangeFinished
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnInitializeErrorMessages
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnInitializeText
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnOpenConditionCreditDialog
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnOpenTermAndCondition
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnSliderValueChange
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnSliderValueChangeFinished
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnTermAndConditionCheckedChange
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.util.DialogParameters
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
class CreditAmountViewModel @Inject constructor(
    private val queryCreditOfferUseCase: QueryCreditOfferUseCase,
    private val queryPaymentAmountUseCase: QueryPaymentAmountUseCase,
    private val mutationSaveCreditApplicationUseCase: MutationSaveCreditApplicationUseCase,
    private val queryScreenConfigUseCase: QueryScreenConfigUseCase
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var products: List<Product?>? = listOf()
    private var currencyItems: List<String>? = listOf()
    private var minimumDisbursement = 0F
    private var maximumDisbursement = 0F
    private var disbursementProgressFactorErrorMessage = 0
    private var minimumDisbursementErrorMessage = 0
    private var maximumDisbursementErrorMessage = 0
    private var conditionModalDescription: String = ""
    private var fee: Double = 0.0
    private var sliderFactor = 0.0
    private var remainingTime: Duration = TIMER_DURATION.milliseconds
    private var isTimerRunning: Boolean = false
    private var idUserRequest = 0

    private fun isFormValid() = emitBaseEvent(
        OnFormValidateCompleted(
            when {
                uiState.disbursement.isBlank() -> false
                uiState.disbursementError.first -> false
                uiState.isTermAndConditionChecked.not() -> false
                else -> true
            }
        )
    )

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

    private fun callQueryCreditOfferUseCase(
        pkUser: Int,
        idBrand: Int,
        onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryCreditOfferUseCase.invoke(pkUser = pkUser, idBrand = idBrand).collectLatest { result ->
            result.onSuccess { creditOffer ->
                idUserRequest = creditOffer?.idUserRequest ?: 0
                products = creditOffer?.products
                currencyItems = products?.map { it?.currency ?: "" }
                onExecuteTimer()
                setCreditOffer(INITIAL_CURRENCY_INDEX)
                isLoading = false
            }.onFailure {
                isLoading = false
                onFailureWithDialog(
                    false,
                    DialogParameters(
                        description = it.getError().toString(),
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                isLoading = true
            }
        }
    }

    private fun callQueryPaymentAmountUseCase(
        amount: Int,
        user: String,
        idBrand: Int,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryPaymentAmountUseCase.invoke(
            amount = amount,
            months = uiState.termLabel,
            idProduct = products?.get(uiState.currencyIndex)?.id ?: "",
            currencySymbol = uiState.currencyItems[uiState.currencyIndex],
            user = user,
            idBrand = idBrand
        ).collectLatest { result ->
            result.onSuccess {
                fee = it?.paymentAmount ?: fee
                uiState = uiState.copy(
                    feeLabel = it?.paymentAmountLabel ?: uiState.feeLabel
                )
                onLoadingValueChange(false)
            }.onFailure {
                onFailureWithDialog(
                    false,
                    DialogParameters(
                        description = it.getError().toString(),
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                onLoadingValueChange(true)
            }
        }
    }

    private fun callMutationSaveCreditApplicationUseCase(
        pkUser: String,
        descPromotion: String,
        idPromotion: Int,
        user: String,
        idBrand: Int,
        onSuccess: (screenConfig: List<CreditCatalog?>?) -> Unit,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit,
        onCurrencySymbolValueChange: (currencySymbol: String) -> Unit
    ) = executeUseCase {
        onCurrencySymbolValueChange(uiState.currencyItems[uiState.currencyIndex])
        mutationSaveCreditApplicationUseCase.invoke(
            idUserRequest = idUserRequest,
            pkUser = pkUser.toInt(),
            descPromotion = descPromotion,
            interestRate = uiState.regularInterestRateLabel,
            symbolCurrency = uiState.currencyItems[uiState.currencyIndex],
            descCurrency = products?.get(uiState.currencyIndex)?.currencyName ?: "",
            idProduct = products?.get(uiState.currencyIndex)?.id?.toInt() ?: 0,
            idPromotion = idPromotion,
            months = uiState.termLabel,
            commissionPercentage = uiState.commissionDisbursementLabel,
            paymentDate = products?.get(uiState.currencyIndex)?.paymentDate ?: "",
            paymentAmount = fee.toInt().toString(),
            user = user,
            idBrand = idBrand,
            selectedAmount = uiState.disbursement.toDouble(),
            minimumAmount = minimumDisbursement.toDouble(),
            creditLimit = maximumDisbursement.toDouble(),
            tractAmount = uiState.progressFactor,
            currentStep = SmartStep.Two.name
        ).collectLatest { result ->
            result.onSuccess {
                onCallQueryScreenConfig(
                    pkUser = pkUser,
                    user = user,
                    idBrand = idBrand,
                    idUserRequest = idUserRequest.toString(),
                    onSuccess = onSuccess,
                    onLoadingValueChange = onLoadingValueChange,
                    onFailureWithDialog = onFailureWithDialog,
                )
            }.onMessage {
                onFailureWithDialog(
                    false,
                    DialogParameters(
                        description = it?.messageError?.message ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }.onFailure {
                onFailureWithDialog(
                    false,
                    DialogParameters(
                        description = it.getError().toString(),
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                onLoadingValueChange(true)
            }
        }
    }

    private fun onOpenConditionCreditDialog() {
        emitBaseEvent(
            OnOpenConditionOfCreditDialog(
                DialogParameters(
                    titleResource = R.string.credit_amount_condition_of_credit_info,
                    description = conditionModalDescription,
                    isActive = mutableStateOf(true),
                    positiveResource = R.string.accept
                )
            )
        )
    }

    private fun onOpenTermAndCondition() {
        uiState = uiState.copy(isTermAndConditionDialogActive = mutableStateOf(true))
    }

    private fun setCreditOffer(productIndex: Int) {
        // Set initial conditions
        if (products.isNullOrEmpty().not()) {
            products?.get(productIndex)?.apply {
                this@CreditAmountViewModel.minimumDisbursement = minimumDisbursement.toFloat()
                this@CreditAmountViewModel.maximumDisbursement = maximumDisbursement.toFloat()
                this@CreditAmountViewModel.fee = fee
                sliderFactor = getSliderFactor(progressFactor)
                uiState = uiState.copy(
                    isMultipleCurrency = (products?.lastIndex ?: INITIAL_CURRENCY_INDEX) > INITIAL_CURRENCY_INDEX,
                    currencyIndex = productIndex,
                    sliderValueRangeInitial = getSliderValue(minimumDisbursement.toFloat(), progressFactor),
                    sliderValue = if (isTimerRunning.not()) {
                        getSliderValue(maximumDisbursement.toFloat(), progressFactor)
                    } else {
                        uiState.sliderValue
                    },
                    progressFactor = progressFactor,
                    currencyItems = currencyItems ?: listOf(),
                    feeLabel = feeLabel,
                    disbursement = maximumDisbursement.toDouble().toInt().toString(),
                    minimumDisbursementLabel = minimumDisbursementLabel,
                    maximumDisbursementLabel = maximumDisbursementLabel,
                    regularInterestRateLabel = regularInterestRateLabel,
                    termLabel = termLabel,
                    commissionDisbursementLabel = commissionDisbursementLabel,
                    disbursementError = Pair(false, R.string.empty)
                )
                isFormValid()
            }
        }
    }

    /**
     * Divide the maximum disbursement between the progress factor to get the amount of times that
     * progress can change. After we divide the total slider value to get the sliderFactor
     * */
    private fun getSliderFactor(progressFactor: Double) =
        SLIDER_TOTAL / (maximumDisbursement / progressFactor)

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

    private fun onDisbursementValueChangeFinished(
        value: String,
        user: String,
        idBrand: Int,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
    ) {
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
                callQueryPaymentAmountUseCase(
                    value.toDouble().toInt(),
                    user,
                    idBrand,
                    onLoadingValueChange,
                    onFailureWithDialog
                )
            }
        }
        isFormValid()
    }

    private fun onInitializeErrorMessages(
        disbursementProgressFactorErrorMessage: Int,
        minimumDisbursementErrorMessage: Int,
        maximumDisbursementErrorMessage: Int
    ) {
        this.disbursementProgressFactorErrorMessage = disbursementProgressFactorErrorMessage
        this.minimumDisbursementErrorMessage = minimumDisbursementErrorMessage
        this.maximumDisbursementErrorMessage = maximumDisbursementErrorMessage
    }

    private fun onSliderValueChange(value: Float) {
        uiState = if (isTimerRunning.not()) {
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

    private fun onSliderValueChangeFinished(
        user: String,
        idBrand: Int,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
    ) {
        uiState = uiState.copy(disbursementError = Pair(false, R.string.empty))
        if (uiState.disbursementError.first.not() && isTimerRunning.not()) {
            callQueryPaymentAmountUseCase(
                uiState.disbursement.toDouble().toInt(),
                user,
                idBrand,
                onLoadingValueChange,
                onFailureWithDialog
            )
        }
    }

    private fun onTermAndConditionCheckedChange(isChecked: Boolean) {
        uiState = uiState.copy(isTermAndConditionChecked = isChecked)
        isFormValid()
    }

    private fun onCallQueryScreenConfig(
        pkUser: String,
        user: String,
        idBrand: Int,
        idUserRequest: String,
        onSuccess: (screenConfig: List<CreditCatalog?>?) -> Unit,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryScreenConfigUseCase.invoke(
            pkUser, user, idBrand, idUserRequest
        ).collectLatest { result ->
            result.onSuccess {
                onLoadingValueChange(false)
                onSuccess(it)
            }.onLoading {
                onLoadingValueChange(true)
            }.onFailure {
                onFailureWithDialog(
                    false,
                    DialogParameters(description = it.getError() ?: "")
                )
            }
        }
    }

    data class UIState(
        // Fields
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
        val isTermAndConditionChecked: Boolean = false,
        val isTermAndConditionDialogActive: MutableState<Boolean> = mutableStateOf(false),
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnValidateForm -> isFormValid()
            is OnInitializeText -> conditionModalDescription = uiEvent.dialogDescription
            is OnCallQueryCreditOfferUseCase -> callQueryCreditOfferUseCase(
                uiEvent.pkUser,
                uiEvent.idBrand,
                uiEvent.onFailureWithDialog
            )
            is OnCallMutationSaveCreditApplicationUseCase -> callMutationSaveCreditApplicationUseCase(
                uiEvent.pkUser,
                uiEvent.descPromotion,
                uiEvent.idPromotion,
                uiEvent.user,
                uiEvent.idBrand,
                uiEvent.onSuccess,
                uiEvent.onLoadingValueChange,
                uiEvent.onFailureWithDialog,
                uiEvent.onCurrencySymbolValueChange
            )
            is OnTermAndConditionCheckedChange -> onTermAndConditionCheckedChange(uiEvent.isChecked)
            is OnOpenConditionCreditDialog -> onOpenConditionCreditDialog()
            is OnOpenTermAndCondition -> onOpenTermAndCondition()
            is OnCurrencyIndexChanged -> setCreditOffer(uiEvent.index)
            is OnDisbursementValueChange -> uiState = uiState.copy(disbursement = uiEvent.value)
            is OnDisbursementValueChangeFinished -> onDisbursementValueChangeFinished(
                uiEvent.value,
                uiEvent.user,
                uiEvent.idBrand,
                uiEvent.onLoadingValueChange,
                uiEvent.onFailureWithDialog
            )
            is OnInitializeErrorMessages -> onInitializeErrorMessages(
                disbursementProgressFactorErrorMessage = uiEvent.disbursementProgressFactorErrorMessage,
                minimumDisbursementErrorMessage = uiEvent.minimumDisbursementErrorMessage,
                maximumDisbursementErrorMessage = uiEvent.maximumDisbursementErrorMessage
            )
            is OnSliderValueChange -> onSliderValueChange(uiEvent.value)
            is OnSliderValueChangeFinished -> onSliderValueChangeFinished(
                uiEvent.user,
                uiEvent.idBrand,
                uiEvent.onLoadingValueChange,
                uiEvent.onFailureWithDialog
            )
        }
    }

    sealed class UIEvent {
        data class OnInitializeText(val dialogDescription: String) : UIEvent()
        data class OnCallQueryCreditOfferUseCase(
            val pkUser: Int,
            val idBrand: Int,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
        ) : UIEvent()

        data class OnCallMutationSaveCreditApplicationUseCase(
            val pkUser: String,
            val descPromotion: String,
            val idPromotion: Int,
            val user: String,
            val idBrand: Int,
            val onSuccess: (screenConfig: List<CreditCatalog?>?) -> Unit,
            val onLoadingValueChange: (status: Boolean) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit,
            val onCurrencySymbolValueChange: (currencySymbol: String) -> Unit
        ) : UIEvent()

        data class OnTermAndConditionCheckedChange(val isChecked: Boolean) : UIEvent()
        data class OnCurrencyIndexChanged(val index: Int) : UIEvent()
        data class OnDisbursementValueChange(val value: String) : UIEvent()
        data class OnDisbursementValueChangeFinished(
            val value: String,
            val user: String,
            val idBrand: Int,
            val onLoadingValueChange: (status: Boolean) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
        ) : UIEvent()

        data class OnInitializeErrorMessages(
            val disbursementProgressFactorErrorMessage: Int,
            val minimumDisbursementErrorMessage: Int,
            val maximumDisbursementErrorMessage: Int
        ) : UIEvent()

        data class OnSliderValueChange(val value: Float) : UIEvent()
        data class OnSliderValueChangeFinished(
            val user: String,
            val idBrand: Int,
            val onLoadingValueChange: (status: Boolean) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
        ) : UIEvent()

        object OnValidateForm : UIEvent()
        object OnOpenConditionCreditDialog : UIEvent()
        object OnOpenTermAndCondition : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
        data class OnOpenConditionOfCreditDialog(val dialogParameters: DialogParameters) : BaseEvent()
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