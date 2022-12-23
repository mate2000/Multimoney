package com.multimoney.multimoney.presentation.ui.smart.payment.sending

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel
import com.multimoney.multimoney.presentation.ui.smart.payment.sending.SmartSelectSendingTypeViewModel.UIEvent.OnNavigateBack
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SmartSelectSendingTypeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(SmartPaymentCardsViewModel.UIState())
        private set

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
        }
    }

    private fun onNavigateBack() {
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = false
        )
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
    }

}