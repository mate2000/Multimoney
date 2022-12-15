package com.multimoney.multimoney.presentation.ui.smart.payment.amount

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.ID_VISA_CARD
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.util.catalog.SuggestedAmount
import com.multimoney.multimoney.presentation.util.catalog.SuggestionOrder
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
    var isDollarAccount: Boolean = true

    private fun onStart() {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idCard = savedStateHandle[ID_VISA_CARD] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        user = savedStateHandle[USER] ?: ""
        // Todo get if the account is using dollars
        isDollarAccount = true
        uiState = uiState.copy(
            currency = if (isDollarAccount) "$" else "₡",
            minSuggestion = SuggestedAmount.createSuggestion(
                isDollarAccount,
                SuggestionOrder.MIN
            ),
            mediumSuggestion = SuggestedAmount.createSuggestion(
                isDollarAccount,
                SuggestionOrder.MEDIUM
            ),
            maxSuggestion = SuggestedAmount.createSuggestion(
                isDollarAccount,
                SuggestionOrder.MAX
            )
        )
    }

    private fun onAmountChanged(newAmount: String) {
        val suggestions =
            listOf(uiState.minSuggestion, uiState.mediumSuggestion, uiState.maxSuggestion)
        val possibleSuggestion = suggestions.find { suggestion -> suggestion.value == newAmount }
        uiState = if (possibleSuggestion != null) {
            uiState.copy(
                currentAmountValueString = newAmount,
                suggestedAmountSelected = possibleSuggestion,
                enableButton = newAmount.toDouble() > 0
            )
        } else {
            uiState.copy(
                currentAmountValueString = newAmount,
                suggestedAmountSelected = null,
                enableButton = newAmount.isNotEmpty() && newAmount.toDouble() > 0
            )
        }
    }

    private fun selectSuggestion(amount: SuggestedAmount) {
        uiState = uiState.copy(
            enableButton = amount.value.isNotEmpty() && amount.value.toDouble() > 0,
            currentAmountValueString = amount.value,
            suggestedAmountSelected = amount
        )
    }

    fun verifySuggestionSelected(order: SuggestionOrder) =
        uiState.suggestedAmountSelected?.isSelected(order) == true

    private fun onShowSavingBottomSheet() {
        uiState =
            uiState.copy(bottomSheetVisibleState = ModalBottomSheetState(ModalBottomSheetValue.Expanded))
    }

    private fun onHideSavingBottomSheet() {
        uiState =
            uiState.copy(bottomSheetVisibleState = ModalBottomSheetState(ModalBottomSheetValue.Hidden))
    }

    private fun onContinueClick() {
        onUIEvent(UIEvent.OnShowPaymentBottomSheet)
    }

    private fun onNavigateBack() =
        navigateBack(popTo = Screen.PaymentSmartCardsScreen.route, isRestart = true)

    data class UIState(
        // Interactions
        val suggestedAmountSelected: SuggestedAmount? = null,
        val minSuggestion: SuggestedAmount = SuggestedAmount(),
        val mediumSuggestion: SuggestedAmount = SuggestedAmount(),
        val maxSuggestion: SuggestedAmount = SuggestedAmount(),
        val currency: String = "$",
        val currentAmountValueString: String? = null,
        val enableButton: Boolean = false,
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
            is UIEvent.OnContinueClick -> onContinueClick()
            is UIEvent.OnHidePaymentBottomSheet -> onHideSavingBottomSheet()
            is UIEvent.OnShowPaymentBottomSheet -> onShowSavingBottomSheet()
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

    companion object {
        const val SAVING_PLACEHOLDER = "$0"
    }
}