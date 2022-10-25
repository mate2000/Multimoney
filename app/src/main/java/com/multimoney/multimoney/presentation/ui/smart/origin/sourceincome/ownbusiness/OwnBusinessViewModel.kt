package com.multimoney.multimoney.presentation.ui.smart.origin.sourceincome.ownbusiness

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OwnBusinessViewModel @Inject constructor() : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    data class UIState(
        // Interactions
        val companyNameNameValue: String = "",
        val companyDescriptionValue: String = "",
        val monthlyIncomeValue: String = "",
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnCompanyNameChange -> {}
            is UIEvent.OnCompanyDescriptionChange -> {}
            is UIEvent.OnMonthlyIncomeChange -> {}
        }
    }

    sealed class UIEvent {
        object OnCompanyNameChange : UIEvent()
        object OnCompanyDescriptionChange : UIEvent()
        object OnMonthlyIncomeChange : UIEvent()
    }
}
