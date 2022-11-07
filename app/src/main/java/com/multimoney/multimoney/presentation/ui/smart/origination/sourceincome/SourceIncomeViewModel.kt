package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.catalog.SourceIncomeOptionType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SourceIncomeViewModel @Inject constructor() : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    data class UIState(
        // Interactions
        val selectedOption: Int = SourceIncomeOptionType.MainSourceIncomeScreenType.id
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateToSelectedSourceOfIncomeOption -> {
                uiState = uiState.copy(selectedOption = uiEvent.selectedOption)
            }
        }
    }

    sealed class UIEvent {
        data class OnNavigateToSelectedSourceOfIncomeOption(val selectedOption: Int) : UIEvent()
    }
}
