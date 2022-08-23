package com.multimoney.multimoney.presentation.ui.credit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnCurrentLocationButtonValueChange
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnInitializeText
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnMoveToStep
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnNextStep
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnOpenDialogValueChange
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnPreviousStep
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnSetNavigation
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreditViewModel @Inject constructor() : BaseViewModel() {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var closeDialogDescription: String = ""
    var nextAction: () -> Unit = {}
    private var nextStep: Int = CreditStep.One.id
    private var previousStep: Int = CreditStep.One.id

    private fun onInitializeTexts(description: String) {
        closeDialogDescription = description
    }

    private fun onBackClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        previousStep()
    }

    private fun onCloseClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        uiState = uiState.copy(
            openDialog = DialogParameters(
                title = R.string.credit_close_dialog_title,
                description = closeDialogDescription,
                positiveText = R.string.credit_close_dialog_positive_button_text,
                negativeText = R.string.credit_close_dialog_negative_button_text,
                positiveAction = {
                    popAndNavigateTo(
                        route = Screen.HomeScreen.route,
                        popTo = Screen.CreditScreen.route
                    )
                },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onContinueClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        nextAction.invoke()
    }

    private fun moveToStep(step: Int) {
        if (step <= CREDIT_TOTAL_STEPS) {
            uiState = uiState.copy(
                currentStep = step,
                isCloseVisible = step >= CreditStep.One.id
            )
        }
    }

    private fun nextStep() {
        if (nextStep <= CREDIT_TOTAL_STEPS) {
            uiState = uiState.copy(
                currentStep = nextStep,
                isCloseVisible = nextStep >= CreditStep.One.id
            )
        } else {
            documentGenerationProcess()
        }
    }

    private fun previousStep() {
        if (previousStep > CreditStep.One.id || uiState.currentStep == CreditStep.Two.id) {
            uiState = uiState.copy(
                currentStep = previousStep,
                isCloseVisible = previousStep >= CreditStep.One.id
            )
        } else {
            popAndNavigateTo(
                route = Screen.HomeScreen.route,
                popTo = Screen.CreditScreen.route
            )
        }
    }

    private fun documentGenerationProcess() {
        popAndNavigateTo(
            Screen.DocumentGenerationScreen.route,
            Screen.CreditScreen.route
        )
    }

    private fun onSetNavigation(nextAction: () -> Unit, nextStep: Int, previousStep: Int) {
        this.nextAction = nextAction
        this.nextStep = nextStep
        this.previousStep = previousStep
    }

    data class UIState(
        // Interactions
        val currentStep: Int = CreditStep.One.id,
        val isCloseVisible: Boolean = true,
        val isContinueEnabled: Boolean = false,
        val isLoading: Boolean = false,
        val isCurrentLocationButtonVisible: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnInitializeText -> onInitializeTexts(
                event.description,
            )
            is OnSetNavigation -> onSetNavigation(event.nextAction, event.nextStep, event.previousStep)
            is OnBackClick -> onBackClick(event.focusManager)
            is OnCloseClick -> onCloseClick(event.focusManager)
            is OnContinueClick -> onContinueClick(event.focusManager)
            is OnContinueEnable -> uiState = uiState.copy(isContinueEnabled = event.enable)
            is OnLoadingValueChange -> uiState = uiState.copy(isLoading = event.isLoading)
            is OnOpenDialogValueChange -> uiState = uiState.copy(openDialog = event.openDialog)
            is OnFailureWithDialog -> uiState =
                uiState.copy(isLoading = event.isLoading, openDialog = event.openDialog)
            is OnCurrentLocationButtonValueChange -> uiState =
                uiState.copy(isCurrentLocationButtonVisible = event.isVisible)
            is OnNextStep -> nextStep()
            is OnMoveToStep -> moveToStep(event.step)
            is OnPreviousStep -> previousStep()
        }
    }

    sealed class UIEvent {
        data class OnInitializeText(val description: String) : UIEvent()
        data class OnSetNavigation(
            val nextAction: () -> Unit = {},
            val nextStep: Int,
            val previousStep: Int
        ) : UIEvent()

        data class OnBackClick(val focusManager: FocusManager) : UIEvent()
        data class OnCloseClick(val focusManager: FocusManager) : UIEvent()
        data class OnContinueClick(val focusManager: FocusManager) : UIEvent()
        data class OnContinueEnable(val enable: Boolean) : UIEvent()
        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()
        data class OnOpenDialogValueChange(val openDialog: DialogParameters) : UIEvent()
        data class OnFailureWithDialog(val isLoading: Boolean, val openDialog: DialogParameters) : UIEvent()
        data class OnCurrentLocationButtonValueChange(val isVisible: Boolean) : UIEvent()
        object OnNextStep : UIEvent()
        object OnPreviousStep : UIEvent()
        data class OnMoveToStep(val step: Int) : UIEvent()
    }

    companion object {
        const val CREDIT_TOTAL_STEPS = 3
        const val CREDIT_INDICATOR_TOTAL_STEPS = 3
    }
}