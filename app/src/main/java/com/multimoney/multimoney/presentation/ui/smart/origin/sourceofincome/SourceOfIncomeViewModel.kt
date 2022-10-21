package com.multimoney.multimoney.presentation.ui.smart.origin.sourceofincome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.domain.model.smart.origin.sourceofincome.SourceOfIncome
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.test.smart.data.MockDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class SourceOfIncomeViewModel @Inject constructor() : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun onCallQueryGetSourceOfIncomeUseCase() {
        // FIXME, mimic success response for now
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            delay(1000L)
            uiState = uiState.copy(
                sourceOfIncomeList = MockDataSource.sourceOfIncomeList,
                isLoading = false
            )
        }
    }

    data class UIState(
        // Interactions
        val sourceOfIncomeList: List<SourceOfIncome?>? = listOf(),
        val isLoading: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnCallQueryGetSourceOfIncome -> onCallQueryGetSourceOfIncomeUseCase()
        }
    }

    sealed class UIEvent {
        object OnCallQueryGetSourceOfIncome : UIEvent()
    }
}
