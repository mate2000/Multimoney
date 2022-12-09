package com.multimoney.multimoney.presentation.ui.smart.payment.transfer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen.HomeScreen
import com.multimoney.multimoney.presentation.navigation.Screen.SmartPaymentScreen
import com.multimoney.multimoney.presentation.navigation.navgraph.USER_SMART_ACCOUNT
import com.multimoney.multimoney.presentation.ui.smart.payment.transfer.SavingMethodTransferViewModel.BaseEvent.OnCopyTextToClipboardEvent
import com.multimoney.multimoney.presentation.ui.smart.payment.transfer.SavingMethodTransferViewModel.UIEvent.OnCopyTextToClipboard
import com.multimoney.multimoney.presentation.ui.smart.payment.transfer.SavingMethodTransferViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.transfer.SavingMethodTransferViewModel.UIEvent.OnNavigateBackHome
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavingMethodTransferViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    init {
        uiState = uiState.copy(accountNumber = savedStateHandle[USER_SMART_ACCOUNT] ?: "")
    }

    private fun onCopyTextToClipboard(text: String) {
        emitBaseEvent(OnCopyTextToClipboardEvent(text))
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> navigateBack(
                popTo = SmartPaymentScreen.route,
                isRestart = false
            )
            is OnNavigateBackHome -> navigateBack(
                popTo = HomeScreen.route, isRestart = false
            )
            is OnCopyTextToClipboard -> onCopyTextToClipboard(uiEvent.text)
        }
    }

    data class UIState(
        // Fields
        var accountNumber: String = "",
    )

    sealed class UIEvent {
        class OnCopyTextToClipboard(val text: String) : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnNavigateBackHome : UIEvent()
    }

    sealed class BaseEvent {
        data class OnCopyTextToClipboardEvent(val text: String) : BaseEvent()
    }
}
