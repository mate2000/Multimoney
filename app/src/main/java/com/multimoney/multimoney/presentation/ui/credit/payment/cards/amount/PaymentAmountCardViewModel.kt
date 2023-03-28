package com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.virtualcard.MutationActivatedCardAutomaticDebitUseCase
import com.multimoney.domain.interaction.virtualcard.MutationPayCreditVDUseCase
import com.multimoney.domain.model.metrics.BaseEventDataDto
import com.multimoney.domain.model.security.InfoUser
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.domain.model.virtualcard.PayCreditVisaDirect
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.CARD_SELECTED
import com.multimoney.multimoney.presentation.navigation.navgraph.CREDIT_NUMBER
import com.multimoney.multimoney.presentation.navigation.navgraph.CURRENT_AMOUNT_VALUE
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CURRENCY
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.INFO_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.IS_AUTOMATIC_PAYMENT_CHECKED
import com.multimoney.multimoney.presentation.navigation.navgraph.MAXIMUM_PAYMENT
import com.multimoney.multimoney.presentation.navigation.navgraph.MAXIMUM_PAYMENT_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.MINIMUM_PAYMENT
import com.multimoney.multimoney.presentation.navigation.navgraph.MINIMUM_PAYMENT_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_DATE
import com.multimoney.multimoney.presentation.navigation.navgraph.REFERENCE_NUMBER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.credit.origination.amount.CreditAmountViewModel
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnAlertResultButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnAlertResultRightButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnAutomaticProgrammedPaymentCheckedChanged
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnHidePaymentBottomSheet
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnMinimumPaymentButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnPayClick
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getNavParam
import com.multimoney.multimoney.presentation.util.isValidAmount
import com.multimoney.multimoney.presentation.util.toJson
import com.multimoney.multimoney.presentation.util.transformation.CurrencyDoubleTransformation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class PaymentAmountCardViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val mutationPayCreditVDUseCase: MutationPayCreditVDUseCase,
    private val mutationActivatedCardAutomaticDebitUseCase: MutationActivatedCardAutomaticDebitUseCase,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var infoUser: InfoUser? = null
    private var identification: String? = null
    private var creditNumber: String? = null
    private var idClient: Int? = null
    private var idLoanClient: Int? = null
    private var minimumPayment: Float = 0.00F
    private var minimumPaymentLabel: String = ""
    private var maximumPayment: Float = 0.00F
    private var maximumPaymentLabel: String = ""
    private var alertResultTitle: String = ""
    private var payCreditVisa: PayCreditVisaDirect? = null
    private var idCurrency: Int? = null
    private var paymentDate: String? = ""

    init {
        infoUser = savedStateHandle[INFO_USER]
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        creditNumber = savedStateHandle[CREDIT_NUMBER] ?: ""
        idClient = savedStateHandle[ID_CLIENT]
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT]
        idCurrency = savedStateHandle[ID_CURRENCY]
        minimumPayment = savedStateHandle[MINIMUM_PAYMENT] ?: 0.00F
        minimumPaymentLabel = savedStateHandle[MINIMUM_PAYMENT_LABEL] ?: ""
        maximumPayment = savedStateHandle[MAXIMUM_PAYMENT] ?: 0.00F
        maximumPaymentLabel = savedStateHandle[MAXIMUM_PAYMENT_LABEL] ?: ""
        paymentDate = savedStateHandle[PAYMENT_DATE] ?: ""
    }

    private fun onInitializeInteractionValues() {
        uiState = uiState.copy(
            titleResource = R.string.payment_amount_card_title_sv,
            minimumPaymentLabel = minimumPaymentLabel,
            maximumPaymentLabel = maximumPaymentLabel,
            currency = minimumPaymentLabel.first().toString()
        )
        onAmountValueChange(
            minimumPaymentLabel.replace(minimumPaymentLabel.first().toString(), "")
        )
    }

    private fun onAmountValueChange(value: String) {
        if (value.isValidAmount()) {
            uiState = uiState.copy(
                currentAmountValueString = value,
                enableButton =
                value.isNotEmpty() && value.toFloat() <= maximumPayment &&
                    value.isNotEmpty() && value.toFloat() > PAYMENT_MUST_HIGHER_THAN_VALUE,
                currentAmountError = if (value.isNotEmpty() && value.toFloat() > maximumPayment) {
                    Pair(true, R.string.payment_amount_card_amount_max_error)
                } else if (value.isNotEmpty() && value.toFloat() <= PAYMENT_MUST_HIGHER_THAN_VALUE) {
                    Pair(true, R.string.payment_amount_card_amount_min_error)
                } else {
                    Pair(false, R.string.empty)
                }
            )
        }
    }

    private fun onAmountButtonClick() =
        onAmountValueChange(minimumPaymentLabel.replace(minimumPaymentLabel.first().toString(), ""))

    fun getFormattedCurrency() =
        if (uiState.currentAmountValueString.isNotEmpty() && uiState.currentAmountValueString.toFloat() > maximumPayment) {
            uiState.maximumPaymentLabel
        } else {
            PaymentAmountViewModel.PAYMENT_MUST_HIGHER_THAN_VALUE
        }

    fun getCurrentAmountFormatted() = CurrencyDoubleTransformation(
        uiState.currency,
        CreditAmountViewModel.CURRENCY_SEPARATOR
    ).filter(AnnotatedString(uiState.currentAmountValueString)).text

    private fun onStart(alertResultTitle: String) {
        onInitializeInteractionValues()
        this.alertResultTitle = alertResultTitle
        uiState = uiState.copy(
            card = savedStateHandle[CARD_SELECTED]
        )
    }

    private fun onStartVisaAnimation() {
        uiState = uiState.copy(isVisaAnimationVisible = true)
    }

    private fun onFinishVisaAnimation() {
        uiState = uiState.copy(isVisaAnimationVisible = false)
        onNavigateToPaymentCardVoucher()
    }

    private fun onNavigateToPaymentCardVoucher() {
        logAdjustEvent()
        popAndNavigateTo(
            route = Screen.PaymentCardVoucherScreen.baseRoute
                .plus(getNavParam(IDENTIFICATION, identification))
                .plus(getNavParam(ID_CLIENT, idClient))
                .plus(getNavParam(ID_LOAN_CLIENT, idLoanClient))
                .plus(getNavParam(CARD_SELECTED, encodeData(uiState.card)))
                .plus(getNavParam(CURRENT_AMOUNT_VALUE, getCurrentAmountFormatted()))
                .plus(getNavParam(IS_AUTOMATIC_PAYMENT_CHECKED, uiState.isAutomaticProgrammedPaymentChecked))
                .plus(getNavParam(REFERENCE_NUMBER, payCreditVisa?.referenceAuthorization))
                .plus(getNavParam(PAYMENT_DATE, paymentDate))
                .plus(getNavParam(INFO_USER, encodeData(infoUser))),
            popTo = Screen.PaymentAmountCardsScreen.route
        )
    }

    private fun logAdjustEvent() {
        viewModelScope.launch {
            if (dataStorePreferences.isAdjustFirstPaymentSuccessEventRegister().first()) {
                registerAdjustEvent(
                    AdjustEventType.PAYMENT_FIRST_FINISH_PROCESS_SUCCESS_5023,
                    applyAdjust = false,
                    data = BaseEventDataDto(
                        user = infoUser?.email ?: "",
                        idBrand = infoUser?.idBrand ?: 0,
                        idClient = idClient,
                        idLoanClient = idLoanClient,
                        identification = identification
                    ).toJson()
                )
                dataStorePreferences.isAdjustFirstPaymentSuccessEventRegister(false)
            }
            restartMetricsPreferences()
        }
    }

    private suspend fun restartMetricsPreferences() {
        dataStorePreferences.isAdjustFirstPaymentSuccessEventRegister(true)
        dataStorePreferences.isAdjustFirstPaymentEventRegister(true)
    }

    private fun onNavigateBack() =
        navigateBack(popTo = Screen.PaymentCardsListScreen.route, isRestart = false)

    private fun onNavigateBackHome() = navigateBack(popTo = Screen.HomeScreen.route, isRestart = false)

    private fun onAutomaticProgrammedPaymentCheckedChanged(value: Boolean) {
        uiState = uiState.copy(isAutomaticProgrammedPaymentChecked = value)
    }

    private fun onShowPaymentBottomSheet() {
        uiState = uiState.copy(bottomSheetVisibleState = ModalBottomSheetState(ModalBottomSheetValue.Expanded))
    }

    private fun onHidePaymentBottomSheet() {
        uiState = uiState.copy(bottomSheetVisibleState = ModalBottomSheetState(ModalBottomSheetValue.Hidden))
    }

    private fun onLoadingValueChange(loading: Boolean) {
        uiState = uiState.copy(isLoading = loading)
    }

    private fun onAlertResultRightButtonClick() {
        uiState = uiState.copy(
            bottomSheetVisibleState = ModalBottomSheetState(ModalBottomSheetValue.Expanded),
            isAlertResultVisible = false
        )
    }

    private fun onContinueClick() {
        onShowPaymentBottomSheet()
    }

    private fun onPayClick() {
        onCallMutationPayCreditVDUseCase()
    }

    private fun onCallMutationPayCreditVDUseCase() = executeUseCase {
        mutationPayCreditVDUseCase.invoke(
            identification = identification.orEmpty(),
            currency = idCurrency.toString(),
            paymentAmount = uiState.currentAmountValueString.toDouble(),
            operationNumber = creditNumber.orEmpty(),
            reference = REFERENCE_PREFIX.plus(creditNumber),
            comment = COMMENT,
            cardMasked = uiState.card?.cardMaskedNumber.orEmpty(),
            idCard = uiState.card?.idCard?.toLong() ?: 0,
            idBrand = infoUser?.idBrand ?: 0
        ).collectLatest { result ->
            result.onSuccess {
                payCreditVisa = it
                if (uiState.isAutomaticProgrammedPaymentChecked) {
                    onCallMutationActivatedCardAutomaticDebitUseCase()
                } else {
                    onUIEvent(OnHidePaymentBottomSheet)
                    onLoadingValueChange(false)
                    onStartVisaAnimation()
                }
            }.onMessage {
                onUIEvent(OnHidePaymentBottomSheet)
                uiState = uiState.copy(isLoading = false)
                uiState = uiState.copy(
                    alertResultTitle = it?.messageError?.message.orEmpty(),
                    alertResultDescription = it?.messageError?.detail.orEmpty(),
                    isAlertResultVisible = true
                )
            }.onFailure {
                onUIEvent(OnHidePaymentBottomSheet)
                onLoadingValueChange(false)
                uiState = uiState.copy(
                    alertResultTitle = alertResultTitle,
                    alertResultDescription = it.getError().orEmpty(),
                    isAlertResultVisible = true
                )
            }.onLoading {
                onLoadingValueChange(true)
            }
        }
    }

    private fun onCallMutationActivatedCardAutomaticDebitUseCase() = executeUseCase {
        mutationActivatedCardAutomaticDebitUseCase.invoke(
            user = infoUser?.email ?: "",
            idBrand = infoUser?.idBrand ?: 0,
            idClient = idClient ?: 0,
            idLoanClient = idLoanClient ?: 0,
            idCard = uiState.card?.idCard?.toLong() ?: 0,
            cardMasked = uiState.card?.cardMaskedNumber.orEmpty(),
            identification = identification.orEmpty()
        ).collectLatest { result ->
            result.onSuccess {
                onActivateClientAutomaticDebitResult(it?.isUpdated ?: false)
            }.onMessage {
                onActivateClientAutomaticDebitResult(false)
            }.onFailure {
                onActivateClientAutomaticDebitResult(false)
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun onActivateClientAutomaticDebitResult(isAutomaticProgrammed: Boolean) {
        onAutomaticProgrammedPaymentCheckedChanged(isAutomaticProgrammed)
        onHidePaymentBottomSheet()
        onLoadingValueChange(false)
        onStartVisaAnimation()
    }

    private fun onAlertResultButtonClick() = onNavigateBackHome()

    data class UIState(
        // Interactions
        val titleResource: Int = R.string.empty,
        val minimumPaymentLabel: String = "",
        val maximumPaymentLabel: String = "",
        val currency: String = "",
        val currentAmountValueString: String = "0.0",
        val currentAmountError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
        val cardVDList: List<CardVisaDirect?>? = null,
        val isCardListEmpty: Boolean = true,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val isVisaAnimationVisible: Boolean = false,
        val enableButton: Boolean = false,
        val card: CardVisaDirect? = null,
        val isAutomaticProgrammedPaymentChecked: Boolean = false,
        val bottomSheetVisibleState: ModalBottomSheetState = ModalBottomSheetState(Hidden),
        val isAlertResultVisible: Boolean = false,
        val alertResultTitle: String = "",
        val alertResultDescription: String = ""
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnStart -> onStart(uiEvent.alertResultTitle)
            is OnMinimumPaymentButtonClick -> onAmountButtonClick()
            is OnAmountValueChange -> onAmountValueChange(uiEvent.value)
            is OnContinueClick -> onContinueClick()
            is OnPayClick -> onPayClick()
            is OnAutomaticProgrammedPaymentCheckedChanged -> onAutomaticProgrammedPaymentCheckedChanged(uiEvent.value)
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnNavigateBackHome -> onNavigateBackHome()
            is UIEvent.OnFinishVisaAnimation -> onFinishVisaAnimation()
            is OnHidePaymentBottomSheet -> onHidePaymentBottomSheet()
            is OnAlertResultButtonClick -> onAlertResultButtonClick()
            is OnAlertResultRightButtonClick -> onAlertResultRightButtonClick()
        }
    }

    sealed class UIEvent {
        class OnStart(val alertResultTitle: String) : UIEvent()
        object OnMinimumPaymentButtonClick : UIEvent()
        data class OnAmountValueChange(val value: String) : UIEvent()
        object OnContinueClick : UIEvent()
        object OnPayClick : UIEvent()
        class OnAutomaticProgrammedPaymentCheckedChanged(val value: Boolean) : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnNavigateBackHome : UIEvent()
        object OnFinishVisaAnimation : UIEvent()
        object OnHidePaymentBottomSheet : UIEvent()
        object OnAlertResultRightButtonClick : UIEvent()
        object OnAlertResultButtonClick : UIEvent()
    }

    companion object {
        const val PAYMENT_MUST_HIGHER_THAN_VALUE = 0.00F
        const val REFERENCE_PREFIX = "Pago - "
        const val COMMENT = "Pago"
    }
}
