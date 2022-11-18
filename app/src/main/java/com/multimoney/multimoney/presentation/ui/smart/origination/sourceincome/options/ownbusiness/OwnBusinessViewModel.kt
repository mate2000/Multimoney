package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusiness

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.DESCRIPTION_MAX_LENGTH
import com.multimoney.multimoney.presentation.util.MIN_INCOME
import com.multimoney.multimoney.presentation.util.validateDecimalIncome
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OwnBusinessViewModel @Inject constructor() : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun onCompanyNameValueChange(companyName: String) {
        uiState = uiState.copy(companyNameValue = companyName)
        onValidateForm()
    }

    private fun onCompanyDescriptionValueChange(description: String) {
        uiState = if (description.length <= DESCRIPTION_MAX_LENGTH) {
            uiState.copy(
                companyDescriptionValue = description,
                companyDescriptionError = Pair(false, R.string.empty)
            )
        } else {
            uiState.copy(
                companyDescriptionValue = description,
                companyDescriptionError = Pair(
                    true,
                    R.string.smart_own_business_description_max_char_error
                )
            )
        }
        onValidateForm()
    }

    private fun onMonthlyIncomeValueChange(monthlyIncome: String) {
        if (validateDecimalIncome(monthlyIncome)) {
            uiState = uiState.copy(monthlyIncomeValue = monthlyIncome)
        }
        onValidateForm()
    }

    private fun onValidateForm() = emitBaseEvent(BaseEvent.OnFormValidateCompleted(isFormValid()))

    fun isFormValid() = uiState.companyNameValue.isNotBlank() &&
        uiState.companyDescriptionValue.isNotBlank() &&
        !uiState.companyDescriptionError.first &&
        uiState.monthlyIncomeValue.isNotBlank() &&
        uiState.monthlyIncomeValue.toFloat() > MIN_INCOME

    data class UIState(
        // Interactions
        val companyNameValue: String = "",
        val companyDescriptionValue: String = "",
        val monthlyIncomeValue: String = "",
        val isLoading: Boolean = false,
        var companyDescriptionError: Pair<Boolean, Int> = Pair(
            false,
            R.string.smart_own_business_description_max_char_error
        )
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
}
