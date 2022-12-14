package com.multimoney.multimoney.presentation.ui.smart.payment.amount

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.ID_VISA_CARD
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.util.catalog.SuggestedAmount
import com.multimoney.multimoney.presentation.util.catalog.SuggestedAmountSV
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class SavingAmountViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    // stateless
    var idBrand: Int = 0
    var idCard: Int = 0
    var identification: String = ""
    var user: String = ""

    private fun onStart() {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idCard = savedStateHandle[ID_VISA_CARD] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        user = savedStateHandle[USER] ?: ""
        when (idBrand) {
            Brand.CostaRica.id -> TODO()
            else -> uiState = uiState.copy(suggestedDisplay = SuggestedAmountSV.values().toList())
        }
    }

    private fun onAmountChanged(newAmount: String) {
        uiState = when (idBrand) {
            Brand.CostaRica.id -> {
                TODO()
            }
            else -> {
                val possibleSuggestion =
                    SuggestedAmountSV.values()
                        .find { suggestion -> suggestion.display == newAmount }
                if (possibleSuggestion != null) {
                    uiState.copy(
                        currentAmountValueString = newAmount,
                        suggestedAmountSelected = possibleSuggestion
                    )
                } else {
                    uiState.copy(currentAmountValueString = newAmount)
                }
            }
        }
    }

    private fun selectSuggestion(amount: SuggestedAmount) {
        uiState = uiState.copy(
            currentAmountValueString = amount.display,
            suggestedAmountSelected = amount
        )
    }

    private fun onNavigateBack() =
        navigateBack(popTo = Screen.PaymentSmartCardsScreen.route, isRestart = true)

    data class UIState(
        // Interactions
        val suggestedAmountSelected: SuggestedAmount? = null,
        val suggestedDisplay: List<SuggestedAmount> = listOf(),
        val currency: String = "$",
        val accountCurrency: String = "$",
        val currentAmountValueString: String = "0",
        val currentAmountError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
        val enableButton: Boolean = false,
        val isAmountVisible: Boolean = true,
        val isLoading: Boolean = false,
        val bottomSheetVisibleState: ModalBottomSheetState = ModalBottomSheetState(
            ModalBottomSheetValue.Hidden
        )
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnStart -> onStart()
            is UIEvent.OnAmountValueChange -> onAmountChanged(uiEvent.value)
            is UIEvent.OnContinueClick -> TODO()
            is UIEvent.OnHidePaymentBottomSheet -> TODO()
            is UIEvent.OnShowPaymentBottomSheet -> TODO()
            is UIEvent.OnSuggestedAmountClick -> selectSuggestion(uiEvent.suggestion)
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnStart : UIEvent()
        data class OnAmountValueChange(val value: String) : UIEvent()
        data class OnSuggestedAmountClick(val suggestion: SuggestedAmount) : UIEvent()
        object OnContinueClick : UIEvent()
        object OnShowPaymentBottomSheet : UIEvent()
        object OnHidePaymentBottomSheet : UIEvent()
    }
}