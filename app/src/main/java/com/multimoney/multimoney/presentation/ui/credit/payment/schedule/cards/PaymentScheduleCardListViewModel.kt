package com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.virtualcard.QueryListCardVDUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_DATE
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnCallQueryGetCards
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnCardSelected
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class PaymentScheduleCardListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val queryListCardVDUseCase: QueryListCardVDUseCase
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: Int = 0
    private var idClient: Int = 0
    private var idLoanClient: Int = 0
    private var paymentDate: String = ""
    private var identification: String = ""
    private var previousScreen = ""

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idClient = savedStateHandle[ID_CLIENT] ?: 0
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: 0
        paymentDate = savedStateHandle[PAYMENT_DATE] ?: ""
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
    }

    private fun onCallQueryGetCardsUseCase() = executeUseCase {
        queryListCardVDUseCase.invoke(
            user = user,
            idBrand = idBrand,
            identification = identification
        ).collectLatest { result ->
            result.onSuccess { cardsList ->
                uiState = uiState.copy(
                    isLoading = false,
                    clientCardList = cardsList
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

    private fun onCardSelected(card: CardVisaDirect?) = popAndNavigateTo(
        route = "${Screen.PaymentScheduleCardScreen.baseRoute}/$user/$idBrand/$idClient/$idLoanClient/${
        encodeData(
            card
        )
        }/$paymentDate/${true}/${Screen.HomeScreen.route}/${false}/$identification",
        popTo = Screen.PaymentScheduleCardListScreen.route
    )

    private fun onNavigateBack() = navigateBack(popTo = Screen.PaymentScheduleCardScreen.route, isRestart = false)

    private fun onCloseClick() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource =
                if (idBrand == Brand.Guatemala.id) {
                    R.string.payment_schedule_card_list_dialog_title_gt
                } else {
                    R.string.payment_schedule_card_list_dialog_title_sv
                },
                descriptionResource =
                if (idBrand == Brand.Guatemala.id) {
                    R.string.payment_schedule_card_list_dialog_description_gt
                } else {
                    R.string.payment_schedule_card_list_dialog_description_sv
                },
                positiveResource = R.string.accept,
                negativeResource = R.string.cancel,
                positiveAction = {
                    navigateBack(
                        popTo = Screen.HomeScreen.route,
                        isRestart = false
                    )
                },
                isActive = mutableStateOf(true)
            )
        )
    }

    data class UIState(
        // Interactions
        val clientCardList: List<CardVisaDirect?>? = null,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnCloseClick -> onCloseClick()
            is OnCallQueryGetCards -> onCallQueryGetCardsUseCase()
            is OnCardSelected -> onCardSelected(uiEvent.card)
        }
    }

    sealed class UIEvent {
        object OnCallQueryGetCards : UIEvent()
        class OnCardSelected(val card: CardVisaDirect?) : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnCloseClick : UIEvent()
    }
}
