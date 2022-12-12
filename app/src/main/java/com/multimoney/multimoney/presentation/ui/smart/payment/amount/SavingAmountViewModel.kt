package com.multimoney.multimoney.presentation.ui.smart.payment.amount

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavingAmountViewModel @Inject constructor() : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    private fun onNavigateBack() = navigateBack(popTo = Screen.PaymentSmartCardsScreen.route, isRestart = false)

    data class UIState @OptIn(ExperimentalMaterialApi::class) constructor(
        // Interactions
        val minimumPaymentLabel: String = "",
        val maximumPaymentLabel: String = "",
        val isMinimumSelected: Boolean = false,
        val isMaximumSelected: Boolean = false,
        val currency: String = "$",
        val accountCurrency: String = "$",
        val currentAmountValueString: String = "0",
        val currentAmountError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
        val enableButton: Boolean = false,
        val isAmountVisible: Boolean = true,
        val isLoading: Boolean = false,
        val bottomSheetVisibleState: ModalBottomSheetState = ModalBottomSheetState(
            ModalBottomSheetValue.Hidden)
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnAmountValueChange -> TODO()
            is UIEvent.OnContinueClick -> TODO()
            is UIEvent.OnHidePaymentBottomSheet -> TODO()
            is UIEvent.OnShowPaymentBottomSheet -> TODO()
            is UIEvent.OnSuggestedAmountClick -> TODO()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        data class OnAmountValueChange(val value: String) : UIEvent()
        object OnSuggestedAmountClick : UIEvent()
        object OnContinueClick : UIEvent()
        object OnShowPaymentBottomSheet : UIEvent()
        object OnHidePaymentBottomSheet : UIEvent()
    }
}