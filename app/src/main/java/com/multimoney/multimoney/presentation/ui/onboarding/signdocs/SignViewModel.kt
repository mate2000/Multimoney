package com.multimoney.multimoney.presentation.ui.onboarding.signdocs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.DialogParameters

class SignViewModel : BaseViewModel(false) {
    // uiState
    var uiState by mutableStateOf(UIState())
        private set
    var dialogDescription = ""

    fun createDialog() {
        uiState = uiState.copy(
            dialogParameters = DialogParameters(
                titleResource = R.string.sign_credit_dialog_title,
                description = dialogDescription,
                positiveResource = R.string.sign_credit_dialog_continue,
                isActive = mutableStateOf(true)
            )
        )
    }

    sealed class UIEvent {
        data class OnInitializeText(val dialogDescription: String) : UIEvent()
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnInitializeText -> dialogDescription = uiEvent.dialogDescription
        }
    }

    data class UIState(
        // Interactions
        val dialogParameters: DialogParameters = DialogParameters()
    )
}