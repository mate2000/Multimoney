package com.multimoney.multimoney.presentation.ui.smart.payment.cards

import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue.Expanded
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.accountsmart.MutationProcessTransferVisaToSmartVDUseCase
import com.multimoney.domain.interaction.credit.QueryListCardVDUseCase
import com.multimoney.domain.model.credit.CardVisaDirect
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.ACCOUNT_TOKEN
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CURRENCY
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnAddCard
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnCallProcessTransferVisaToSmart
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnCallQueryGetClientCards
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnCardSelected
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterialApi::class)
@HiltViewModel
class SmartPaymentCardsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val queryListCards: QueryListCardVDUseCase,
    private val processTransferVisaToSmart: MutationProcessTransferVisaToSmartVDUseCase
) : BaseViewModel(true) {
    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = savedStateHandle[USER] ?: ""
    private var idBrand: Int = savedStateHandle[ID_BRAND] ?: 0
    private var identification: String = savedStateHandle[IDENTIFICATION] ?: ""
    private var idCurrency: Int = savedStateHandle[ID_CURRENCY] ?: 0
    private var tokenNumber: Long = savedStateHandle[ACCOUNT_TOKEN] ?: 0

    private fun onCallQueryGetClientCardsUseCase() {
        executeUseCase {
            queryListCards.invoke(
                user = user,
                idBrand = idBrand,
                identification = identification
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

    private fun onCallProcessTransferVisaToSmart() {
        executeUseCase {
            processTransferVisaToSmart.invoke(
                uiState.idCard,
                tokenNumber,
                identification,
                uiState.amount,
                idCurrency,
                DEFAULT_DESCRIPTION,
                uiState.cardMasked,
                user,
                idBrand
            ).collectLatest { result ->
                result.onSuccess {
                    Log.d("SmartPayment", "Success: ${it?.referenceNumberVisa}")
                }
                result.onFailure {
                    Log.d("SmartPayment", "Failure: ${it?.getError()}")
                }
                result.onLoading {
                    Log.d("SmartPayment", "Loading Payment")
                }
            }
        }
    }

    private fun onCardSelected(cardSelected: CardVisaDirect) {
        uiState = uiState.copy(
            idCard = cardSelected.idCard?.toLong() ?: 0,
            cardMasked = (cardSelected.cardMaskedNumber ?: ""),
            cardBankName = cardSelected.detail ?: "",
            bottomSheetState = ModalBottomSheetState(Expanded)
        )
        navigateTo(
            "${Screen.SmartPaymentSuccessScreen.baseRoute}/${"$"}/${"500"}/${"true"}/${"300694.10"}/${"601.95 "}/${"12345****0980"}/${"74598621"}"
        )
        // fixme navigate to amount screen
        // navigateTo("${Screen. ROUTE }/${idBrand} +
        // /${user} +
        // /${tokenNumber} +
        // /${identification} +
        // /${uiState.amount} +
        // /${idCurrency} +
        // /${uiState.cardMasked} +
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
        val isVisaAnimationVisible: Boolean = false,
        val currency: String = "$",
        val amount: String = "500",
        val cardBankName: String = "",
        val exchangeRate: String = "",
        val exchangeAmount: String = "",
        val idCard: Long = 0,
        val cardMasked: String = "",
        val bottomSheetState: ModalBottomSheetState = ModalBottomSheetState(Hidden)
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnCallQueryGetClientCards -> onCallQueryGetClientCardsUseCase()
            is OnCardSelected -> onCardSelected(uiEvent.cardSelected)
            is OnAddCard -> onAddCard()
            is OnCallProcessTransferVisaToSmart -> onCallProcessTransferVisaToSmart()
        }
    }

    sealed class UIEvent {
        class OnCardSelected(val cardSelected: CardVisaDirect) : UIEvent()
        object OnCallQueryGetClientCards : UIEvent()
        object OnAddCard : UIEvent()
        object OnNavigateBack : UIEvent()

        object OnCallProcessTransferVisaToSmart : UIEvent()
    }

    companion object {
        const val DEFAULT_DESCRIPTION = "Deposito a cuenta smart"
    }
}
