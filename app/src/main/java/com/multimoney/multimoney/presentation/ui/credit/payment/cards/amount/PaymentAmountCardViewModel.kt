package com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.credit.CardVisaDirect
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.CARD_SELECTED
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnAutomaticProgrammedPaymentCheckedChanged
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnHidePaymentBottomSheet
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnPayClick
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class PaymentAmountCardViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: Int = 0
    private var identification: String? = null

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
    }

    private fun onStart() {
        uiState = uiState.copy(
            card = savedStateHandle[CARD_SELECTED]
        )
    }

    private fun onShowVisaAnimation() {
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

    private fun onContinueClick() {
        onShowPaymentBottomSheet()
    }

    private fun onPayClick() {
        onHidePaymentBottomSheet()
        onShowVisaAnimation()
    }

    // TODO: Value hardcoded. Must be replaced when user selects the value
    fun getCurrentAmountFormatted() = "$100"

    data class UIState(
        // Interactions
        val cardVDList: List<CardVisaDirect?>? = null,
        val isCardListEmpty: Boolean = true,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val isVisaAnimationVisible: Boolean = false,
        val enableButton: Boolean = true,
        val card: CardVisaDirect? = null,
        val isAutomaticProgrammedPaymentChecked: Boolean = false,
        val bottomSheetVisibleState: ModalBottomSheetState = ModalBottomSheetState(Hidden)
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnStart -> onStart()
            is OnContinueClick -> onContinueClick()
            is OnPayClick -> onPayClick()
            is OnAutomaticProgrammedPaymentCheckedChanged -> onAutomaticProgrammedPaymentCheckedChanged(uiEvent.value)
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnNavigateBackHome -> onNavigateBackHome()
            is UIEvent.OnFinishVisaAnimation -> onFinishVisaAnimation()
            is OnHidePaymentBottomSheet -> onHidePaymentBottomSheet()
        }
    }

    sealed class UIEvent {
        object OnStart : UIEvent()
        object OnContinueClick : UIEvent()
        object OnPayClick : UIEvent()
        class OnAutomaticProgrammedPaymentCheckedChanged(val value: Boolean) : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnNavigateBackHome : UIEvent()
        object OnFinishVisaAnimation : UIEvent()
        object OnHidePaymentBottomSheet : UIEvent()
    }
}
