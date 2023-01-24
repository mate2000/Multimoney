package com.multimoney.multimoney.presentation.ui.crypto.purchase

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PurchaseCryptoSharedViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    private fun navigateToStep(step: Int) = executeUseCase {
        when (step) {
            1 -> {}
            2 -> {
                uiState = uiState.copy(currentStep = step)
                onNavigateNext()
            }
            3 -> {
                uiState = uiState.copy(currentStep = step)
                onNavigateNext()
            }
            4 -> {
                onNavigateNext()
                uiState = uiState.copy(currentStep = step)
            }
        }
    }

    private fun onNavigateNext() {
        val nextStep = uiState.currentStep + 1
        uiState = uiState.copy(currentStep = nextStep)
        navigateToStep(nextStep)
    }

    data class UIState(
        val currentStep: Int = 0
    )
}