package com.multimoney.multimoney.presentation.ui.credit.payment.cards

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
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.PaymentCardListViewModel.UIEvent.OnCallQueryGetClientCards
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.PaymentCardListViewModel.UIEvent.OnCardSelected
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.PaymentCardListViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.PaymentCardListViewModel.UIEvent.OnNavigateBackHome
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class PaymentCardListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val queryListCardVDUseCase: QueryListCardVDUseCase
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

    private fun onCallQueryGetClientCardsUseCase() {
        executeUseCase {
            queryListCardVDUseCase.invoke(
                user = user,
                idBrand = idBrand,
                identification = identification ?: ""
            ).collectLatest { result ->
                result.onSuccess { cardsList ->
                    uiState = uiState.copy(
                        isLoading = false,
                        cardVDList = cardsList,
                        isCardListEmpty = cardsList.isNullOrEmpty()
                    )
                }.onFailure {
                    uiState = uiState.copy(
                        isLoading = false,
                        openDialog = DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        ),
                        cardVDList = listOf(),
                        isCardListEmpty = true
                    )
                }.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        }
    }

    private fun onCardSelected(cardSelected: CardVisaDirect?) =
        navigateTo(
            route = "${Screen.PaymentAmountCardsScreen.baseRoute}/$idBrand/$identification/$user/${
            encodeData(cardSelected)
            }"
        )

    private fun onNavigateBack() =
        navigateBack(popTo = Screen.PaymentOptionsScreen.route, isRestart = false)

    private fun onNavigateBackHome() = navigateBack(popTo = Screen.HomeScreen.route, isRestart = false)

    data class UIState(
        // Interactions
        val cardVDList: List<CardVisaDirect?>? = null,
        val isCardListEmpty: Boolean = true,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val isVisaAnimationVisible: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnNavigateBackHome -> onNavigateBackHome()
            is OnCallQueryGetClientCards -> onCallQueryGetClientCardsUseCase()
            is OnCardSelected -> onCardSelected(uiEvent.cardSelected)
        }
    }

    sealed class UIEvent {
        object OnCallQueryGetClientCards : UIEvent()

        class OnCardSelected(val cardSelected: CardVisaDirect?) : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnNavigateBackHome : UIEvent()
    }
}
