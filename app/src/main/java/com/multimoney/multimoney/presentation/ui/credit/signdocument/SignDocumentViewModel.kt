package com.multimoney.multimoney.presentation.ui.credit.signdocument

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignDocumentViewModel @Inject constructor() : BaseViewModel() {
    // uiState
    var uiState by mutableStateOf(UIState())
        private set
    var dialogDescription = ""

    fun createDialog() {
        uiState = uiState.copy(
            dialogParameters = DialogParameters(
                title = string.sign_credit_dialog_title,
                description = dialogDescription,
                positiveText = string.sign_credit_dialog_continue,
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