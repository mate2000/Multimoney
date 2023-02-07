package com.multimoney.multimoney.presentation.ui.credit.disbursement.visa.verification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VisaVerifiedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    private fun onNavigateTo() = navigateTo("") // TODO Navigate to next screen

    private fun onNavigateBack() = navigateBack(popTo = Screen.DisbursementAmountScreen.route, isRestart = false) // TODO change route to previous screen

    private fun onNavigateBackHome() = navigateBack(popTo = Screen.HomeScreen.route, isRestart = false)

    private fun onCloseClick() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = R.string.visa_verified_dialog_title,
                descriptionResource = R.string.visa_verified_dialog_description,
                positiveResource = R.string.cancel,
                positiveAction = { onNavigateBackHome() },
                negativeResource = R.string.button_continue,
                isActive = mutableStateOf(true)
            )
        )
    }

    data class UIState(
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnCloseClick -> onCloseClick()
            is UIEvent.OnNavigateTo -> onNavigateTo()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnCloseClick : UIEvent()
        object OnNavigateTo : UIEvent()
    }
}
