package com.multimoney.multimoney.presentation.ui.smart.payment.cards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.virtualcard.QueryListCardVDUseCase
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.SMART_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.ACCOUNT_TOKEN
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CURRENCY
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnAddCard
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnCardSelected
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType.Colon
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType.Dollar
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SmartTransferTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmartPaymentCardsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val dataStorePreferences: DataStorePreferences,
    private val queryListCards: QueryListCardVDUseCase
) : BaseViewModel(true) {
    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: Int = 0
    private var identification: String = ""
    private var idCurrency: Int = 0
    private var tokenNumber: Long = 0
    private var smartAccount: SmartAccountID? = null
    var currency: String = ""

    private fun onStart() {
        viewModelScope.launch {
            idCurrency = savedStateHandle[ID_CURRENCY] ?: 0
            tokenNumber = savedStateHandle[ACCOUNT_TOKEN] ?: 0
            user = dataStorePreferences.getUserName().first()
            idBrand = dataStorePreferences.getIdBrand().first().toInt()
            identification = dataStorePreferences.getIdentification().first()
            smartAccount = savedStateHandle[SMART_ACCOUNT]
            currency = if (idCurrency == Dollar.id) Dollar.symbol else Colon.symbol
            onCallQueryGetClientCardsUseCase()
        }
    }

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
                    if (cardsList.isNullOrEmpty()) {
                        onAddCard()
                    }
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
            "${Screen.SmartPaymentSavingAmountSV.baseRoute}/" +
                "${Screen.SmartPaymentCardsScreenSV.baseRoute}/" +
                "${encodeData(cardSelected)}/${encodeData(smartAccount)}/${SmartTransferTypes.VisaToSmart.id}"

        )
    }

    private fun onAddCard() {
        // TODO change to add card navigation
    }

    private fun onNavigateBack() =
        navigateBack(popTo = Screen.SmartPaymentMethodScreenSV.route, isRestart = false)

    data class UIState(
        // Interactions
        val cardVDList: List<CardVisaDirect?> = emptyList(),
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnCardSelected -> onCardSelected(uiEvent.cardSelected)
            is OnAddCard -> onAddCard()
            is OnStart -> onStart()
        }
    }

    sealed class UIEvent {
        data class OnCardSelected(val cardSelected: CardVisaDirect) : UIEvent()
        object OnAddCard : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnStart : UIEvent()
    }
}
