package com.multimoney.multimoney.presentation.ui.smart.payment.transfer

import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen.HomeScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.transfer.SavingMethodTransferViewModel.BaseEvent.OnCopyTextToClipboardEvent
import com.multimoney.multimoney.presentation.ui.smart.payment.transfer.SavingMethodTransferViewModel.UIEvent.OnCopyTextToClipboard
import com.multimoney.multimoney.presentation.ui.smart.payment.transfer.SavingMethodTransferViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.transfer.SavingMethodTransferViewModel.UIEvent.OnNavigateBackHome
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavingMethodTransferViewModel @Inject constructor() : BaseViewModel(true) {

    private fun onCopyTextToClipboard(text: String) {
        emitBaseEvent(OnCopyTextToClipboardEvent(text))
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> navigateBack(
                popTo = HomeScreen.route, // FIXME, send user to proper previous screen
                isRestart = false
            )
            is OnNavigateBackHome -> navigateBack(
                popTo = HomeScreen.route, isRestart = false
            )
            is OnCopyTextToClipboard -> onCopyTextToClipboard(uiEvent.text)
        }
    }

    sealed class UIEvent {
        class OnCopyTextToClipboard(val text: String) : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnNavigateBackHome : UIEvent()
    }

    sealed class BaseEvent {
        data class OnCopyTextToClipboardEvent(val text: String) : BaseEvent()
    }
}
