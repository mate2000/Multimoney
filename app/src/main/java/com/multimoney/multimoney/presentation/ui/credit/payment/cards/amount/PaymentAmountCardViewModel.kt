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
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.virtualcard.MutationActivatedCardAutomaticDebitUseCase
import com.multimoney.domain.interaction.virtualcard.MutationPayCreditVDUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.CARD_SELECTED
import com.multimoney.multimoney.presentation.navigation.navgraph.CREDIT_NUMBER
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.MAXIMUM_PAYMENT
import com.multimoney.multimoney.presentation.navigation.navgraph.MAXIMUM_PAYMENT_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.MINIMUM_PAYMENT
import com.multimoney.multimoney.presentation.navigation.navgraph.MINIMUM_PAYMENT_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.credit.origination.amount.CreditAmountViewModel
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnAlertResultButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnAutomaticProgrammedPaymentCheckedChanged
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnHidePaymentBottomSheet
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnMinimumPaymentButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnPayClick
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrencyFromValue
import com.multimoney.multimoney.presentation.util.isValidAmount
import com.multimoney.multimoney.presentation.util.transformation.CurrencyDoubleTransformation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class PaymentAmountCardViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val mutationPayCreditVDUseCase: MutationPayCreditVDUseCase,
    private val mutationActivatedCardAutomaticDebitUseCase: MutationActivatedCardAutomaticDebitUseCase
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: Int = 0
    private var identification: String? = null
    private var creditNumber: String? = null
    private var idClient: Int? = null
    private var idLoanClient: Int? = null
    private var minimumPayment: Float = 0.00F
    private var minimumPaymentLabel: String = ""
    private var maximumPayment: Float = 0.00F
    private var maximumPaymentLabel: String = ""

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        creditNumber = savedStateHandle[CREDIT_NUMBER] ?: ""
        idClient = savedStateHandle[ID_CLIENT]
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT]
        minimumPayment = savedStateHandle[MINIMUM_PAYMENT] ?: 0.00F
        minimumPaymentLabel = savedStateHandle[MINIMUM_PAYMENT_LABEL] ?: ""
        maximumPayment = savedStateHandle[MAXIMUM_PAYMENT] ?: 0.00F
        maximumPaymentLabel = savedStateHandle[MAXIMUM_PAYMENT_LABEL] ?: ""
        onInitializeInteractionValues()
    }

    private fun onInitializeInteractionValues() {
        uiState = uiState.copy(
            titleResource = if (idBrand == Brand.ElSalvador.id) {
                R.string.payment_amount_card_title_sv
            } else {
                R.string.payment_amount_card_title_gt
            },
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

    private fun onStart() {
        uiState = uiState.copy(
            card = savedStateHandle[CARD_SELECTED]
        )
    }

    private fun onStartVisaAnimation() {
        uiState = uiState.copy(isVisaAnimationVisible = true)
    }

    private fun onFinishVisaAnimation() {
        uiState = uiState.copy(isVisaAnimationVisible = false)
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

    private fun onContinueClick() {
        onShowPaymentBottomSheet()
    }

    private fun onPayClick() {
        onCallMutationPayCreditVDUseCase()
    }

    private fun onCallMutationPayCreditVDUseCase() = executeUseCase {
        mutationPayCreditVDUseCase.invoke(
            identification = identification.orEmpty(),
            currency = uiState.card?.currencyDescription?.getCurrencyFromValue()?.id.toString(),
            paymentAmount = uiState.currentAmountValueString.toDouble(),
            operationNumber = creditNumber.orEmpty(),
            reference = REFERENCE_PREFIX.plus(creditNumber),
            comment = COMMENT,
            cardMasked = uiState.card?.cardMaskedNumber.orEmpty(),
            idCard = uiState.card?.idCard?.toLong() ?: 0,
            idBrand = idBrand
        ).collectLatest { result ->
            result.onSuccess {
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
                    alertResultTitle = it.getError().orEmpty(),
                    isAlertResultVisible = true
                )
            }.onLoading {
                onLoadingValueChange(true)
            }
        }
    }

    private fun onCallMutationActivatedCardAutomaticDebitUseCase() = executeUseCase {
        mutationActivatedCardAutomaticDebitUseCase.invoke(
            user = user,
            idBrand = idBrand,
            idClient = idClient ?: 0,
            idLoanClient = idLoanClient ?: 0,
            idCard = uiState.card?.idCard?.toLong() ?: 0,
            cardMasked = uiState.card?.cardMaskedNumber.orEmpty()
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
            is OnStart -> onStart()
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
        }
    }

    sealed class UIEvent {
        object OnStart : UIEvent()
        object OnMinimumPaymentButtonClick : UIEvent()
        data class OnAmountValueChange(val value: String) : UIEvent()
        object OnContinueClick : UIEvent()
        object OnPayClick : UIEvent()
        class OnAutomaticProgrammedPaymentCheckedChanged(val value: Boolean) : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnNavigateBackHome : UIEvent()
        object OnFinishVisaAnimation : UIEvent()
        object OnHidePaymentBottomSheet : UIEvent()
        object OnAlertResultButtonClick : UIEvent()
    }

    companion object {
        const val PAYMENT_MUST_HIGHER_THAN_VALUE = 0.00F
        const val REFERENCE_PREFIX = "Pago - "
        const val COMMENT = "Pago"
    }
}
