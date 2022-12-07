package com.multimoney.multimoney.presentation.ui.smart.payment.cards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.credit.QueryListCardVDUseCase
import com.multimoney.domain.model.credit.CardVisaDirect
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnAddCard
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnCallQueryGetClientCards
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnCardSelected
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SmartPaymentCardsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val queryListCards: QueryListCardVDUseCase
) : BaseViewModel(true) {
    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = savedStateHandle[USER] ?: ""
    private var idBrand: Int = savedStateHandle[ID_BRAND] ?: 0
    private var identification: String? = savedStateHandle[IDENTIFICATION] ?: ""

    private fun onCallQueryGetClientCardsUseCase() {
        executeUseCase {
            queryListCards.invoke(
                user = user,
                idBrand = idBrand,
                identification = identification ?: ""
            ).collectLatest { result ->
                result.onSuccess { cardsList ->
                    uiState = uiState.copy(
                        isLoading = false,
                        cardVDList = cardsList ?: emptyList()
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
    }

    private fun onCardSelected(cardSelected: CardVisaDirect) {
        navigateTo(
            "${Screen.SmartPaymentSuccessScreen.baseRoute}/${"$"}/${"500"}/${"true"}/${"300694.10"}/${"601.95 "}/${"12345****0980"}/${"74598621"}"
        )
    }

    private fun onAddCard() {
        // todo navigate
    }

    // todo navigate back to previous payment flow screen
    private fun onNavigateBack() =
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = false)

    data class UIState(
        // Interactions
        val cardVDList: List<CardVisaDirect?> = emptyList(),
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val isVisaAnimationVisible: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnCallQueryGetClientCards -> onCallQueryGetClientCardsUseCase()
            is OnCardSelected -> onCardSelected(uiEvent.cardSelected)
            is OnAddCard -> onAddCard()
        }
    }

    sealed class UIEvent {
        class OnCardSelected(val cardSelected: CardVisaDirect) : UIEvent()
        object OnCallQueryGetClientCards : UIEvent()
        object OnAddCard : UIEvent()

        object OnNavigateBack : UIEvent()
    }
}
