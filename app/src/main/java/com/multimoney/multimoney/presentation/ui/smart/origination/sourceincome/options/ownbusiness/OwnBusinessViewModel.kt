package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusiness

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OwnBusinessViewModel @Inject constructor() : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun onCompanyNameValueChange(companyName: String) {
        uiState = uiState.copy(companyNameValue = companyName)
        isFormValid()
    }

    private fun onCompanyDescriptionValueChange(description: String) {
        if (description.length <= DESCRIPTION_MAX_LENGTH) {
            uiState = uiState.copy(companyDescriptionValue = description)
            isFormValid()
        } else {
            uiState = uiState.copy(
                openDialog = DialogParameters(
                    isActive = mutableStateOf(true),
                    descriptionResource = R.string.smart_own_business_description_max_char_error
                )
            )
        }
    }

    private fun onMonthlyIncomeValueChange(monthlyIncome: String) {
        uiState = uiState.copy(monthlyIncomeValue = monthlyIncome)
        isFormValid()
    }

    private fun isFormValid() = emitBaseEvent(
        BaseEvent.OnFormValidateCompleted(
            uiState.companyNameValue.isNotBlank() &&
                    uiState.companyDescriptionValue.isNotBlank() &&
                    uiState.monthlyIncomeValue.isNotBlank()
        )
    )

    data class UIState(
        // Interactions
        val companyNameValue: String = "",
        val companyDescriptionValue: String = "",
        val monthlyIncomeValue: String = "",
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnCompanyNameChange -> onCompanyNameValueChange(uiEvent.companyName)
            is UIEvent.OnCompanyDescriptionChange -> onCompanyDescriptionValueChange(uiEvent.description)
            is UIEvent.OnMonthlyIncomeChange -> onMonthlyIncomeValueChange(uiEvent.monthlyIncome)
        }
    }

    sealed class UIEvent {
        data class OnCompanyNameChange(val companyName: String) : UIEvent()
        data class OnCompanyDescriptionChange(val description: String) : UIEvent()
        data class OnMonthlyIncomeChange(val monthlyIncome: String) : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    companion object {
        const val DESCRIPTION_MAX_LENGTH = 150
    }
}
