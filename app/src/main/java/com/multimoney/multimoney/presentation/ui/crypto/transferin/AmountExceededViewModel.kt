package com.multimoney.multimoney.presentation.ui.crypto.transferin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AmountExceededViewModel @Inject constructor() : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun isFormValid() {
        uiState = uiState.copy(
            isFormValid = when {
                uiState.name.isBlank() -> false
                uiState.platformName.isBlank() -> false
                uiState.reason.isBlank() -> false
                else -> true
            }
        )
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNameChange -> onNameChanged(event.name)
            is UIEvent.OnPlatformNameChange -> onPlatformNameChanged(event.platformName)
            is UIEvent.OnReasonChange -> onReasonChanged(event.reason)
            is UIEvent.OnReleaseDeposit -> onReleaseDeposit()
        }
    }

    private fun onReleaseDeposit() {

    }

    private fun onReasonChanged(reason: String) {
        uiState = uiState.copy(reason = reason)
        isFormValid()
    }

    private fun onPlatformNameChanged(platformName: String) {
        uiState = uiState.copy(platformName = platformName)
        isFormValid()
    }

    private fun onNameChanged(name: String) {
        uiState = uiState.copy(name = name)
        isFormValid()
    }

    sealed interface UIEvent {
        data class OnNameChange(val name: String) : UIEvent
        data class OnPlatformNameChange(val platformName: String) : UIEvent
        data class OnReasonChange(val reason: String) : UIEvent
        object OnReleaseDeposit : UIEvent
    }

    data class UIState(
        val name: String = "",
        val platformName: String = "",
        val reason: String = "",
        val isFormValid: Boolean = false
    )
}