package com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount

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
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
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
    private var card: CardVisaDirect? = null

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        card = savedStateHandle[CARD_SELECTED]
    }

    private fun onShowVisaAnimation() {
        uiState = uiState.copy(isVisaAnimationVisible = true)
    }

    private fun onFinishVisaAnimation() {
        uiState = uiState.copy(isVisaAnimationVisible = false)
        // onUIEvent(OnNavigateToVoucher)
    }

    private fun onNavigateBack() =
        navigateBack(popTo = Screen.PaymentOptionsScreen.route, isRestart = false)

    private fun onNavigateBackHome() = navigateBack(popTo = Screen.HomeScreen.route, isRestart = false)

    data class UIState(
        // Interactions
        val cardVDList: List<CardVisaDirect?>? = null,
        val isCardListEmpty: Boolean = true,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val isVisaAnimationVisible: Boolean = false,
        val enableButton: Boolean = true
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnNavigateBackHome -> onNavigateBackHome()
            is UIEvent.OnShowVisaAnimation -> onShowVisaAnimation()
            is UIEvent.OnFinishVisaAnimation -> onFinishVisaAnimation()
        }
    }

    sealed class UIEvent {

        object OnNavigateBack : UIEvent()
        object OnNavigateBackHome : UIEvent()
        object OnShowVisaAnimation : UIEvent()
        object OnFinishVisaAnimation : UIEvent()
    }
}
