package com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.virtualcard.MutationActivatedCardAutomaticDebitUseCase
import com.multimoney.domain.interaction.virtualcard.MutationPayCreditVDUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.domain.model.virtualcard.PayCreditVisaDirect
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.CARD_SELECTED
import com.multimoney.multimoney.presentation.navigation.navgraph.CREDIT_NUMBER
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnAlertResultButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnAlertResultRightButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnAutomaticProgrammedPaymentCheckedChanged
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnHidePaymentBottomSheet
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnPayClick
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrencyFromValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

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
    private var alertResultTitle: String = ""
    private var payCreditVisa: PayCreditVisaDirect? = null

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        creditNumber = savedStateHandle[CREDIT_NUMBER] ?: ""
        idClient = savedStateHandle[ID_CLIENT]
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT]
    }

    private fun onStart(alertResultTitle: String) {
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
        popAndNavigateTo(
            "${Screen.PaymentCardVoucherScreen.baseRoute}/$user/$idBrand/$identification/$idClient/$idLoanClient/${uiState.card}/${uiState.paymentAmount}/${uiState.card?.currencyDescription?.getCurrencyFromValue()?.symbol}/${uiState.isAutomaticProgrammedPaymentChecked}/${payCreditVisa?.referenceAuthorization}",
            Screen.PaymentAmountCardsScreen.route
        )
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
            currency = uiState.card?.currencyDescription?.getCurrencyFromValue()?.id.toString(),
            paymentAmount = uiState.paymentAmount,
            operationNumber = creditNumber.orEmpty(),
            reference = REFERENCE_PREFIX.plus(creditNumber),
            comment = COMMENT,
            cardMasked = uiState.card?.cardMaskedNumber.orEmpty(),
            idCard = uiState.card?.idCard?.toLong() ?: 0,
            idBrand = idBrand
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

    // TODO: Value hardcoded. Must be replaced when user selects the value
    fun getCurrentAmountFormatted() = "$100"

    data class UIState(
        // Interactions
        val paymentAmount: Double = 10.0,
        val cardVDList: List<CardVisaDirect?>? = null,
        val isCardListEmpty: Boolean = true,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val isVisaAnimationVisible: Boolean = false,
        val enableButton: Boolean = true,
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
        const val REFERENCE_PREFIX = "Pago - "
        const val COMMENT = "Pago"
    }
}
