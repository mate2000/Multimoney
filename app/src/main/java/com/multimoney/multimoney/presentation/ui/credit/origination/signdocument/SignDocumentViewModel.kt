package com.multimoney.multimoney.presentation.ui.credit.origination.signdocument

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_LINK
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocument.SignDocumentViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocument.SignDocumentViewModel.UIEvent.OnInitializeText
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocument.SignDocumentViewModel.UIEvent.OnRejectClick
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignDocumentViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : BaseViewModel(true) {
    // uiState
    var uiState by mutableStateOf(UIState())
        private set
    var dialogDescription = ""

    init {
        uiState = uiState.copy(signDocumentLink = savedStateHandle[SIGN_DOCUMENT_LINK] ?: "")
    }

    fun createDialog() {
        uiState = uiState.copy(
            dialogParameters = DialogParameters(
                titleResource = string.sign_credit_dialog_title,
                description = dialogDescription,
                positiveResource = string.sign_credit_dialog_continue,
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onRejectClick() {
        uiState = uiState.copy(isAlertResultVisible = true)
    }

    data class UIState(
        // Interactions
        val dialogParameters: DialogParameters = DialogParameters(),
        val isAlertResultVisible: Boolean = false,
        val signDocumentLink: String = ""
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnInitializeText -> dialogDescription = uiEvent.dialogDescription
            is OnRejectClick -> onRejectClick()
            is OnCloseClick -> popAndNavigateTo(
                route = Screen.HomeScreen.route,
                popTo = Screen.SignDocumentScreen.route
            )
        }
    }

    sealed class UIEvent {
        data class OnInitializeText(val dialogDescription: String) : UIEvent()
        object OnRejectClick : UIEvent()
        object OnCloseClick : UIEvent()
    }
}
