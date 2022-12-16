package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusiness

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.model.accountsmart.AccountSmartData
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

    /**
     * this function is intended to load the form data on the UI, after getting the
     * data coming from the current step (provided from the backend)
     */
    private fun onLoadCurrentStepData(accountSmartData: AccountSmartData?) {
        accountSmartData?.let {
            onCompanyNameValueChange(it.companyName.orEmpty())
            onCompanyDescriptionValueChange(it.aboutCompany.orEmpty())
            onMonthlyIncomeValueChange(it.income.toString())
        }
    }

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
                    R.string.you_have_exceeded_the_max_characters_error
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
            R.string.you_have_exceeded_the_max_characters_error
        )
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnCompanyNameChange -> onCompanyNameValueChange(uiEvent.companyName)
            is UIEvent.OnCompanyDescriptionChange -> onCompanyDescriptionValueChange(uiEvent.description)
            is UIEvent.OnMonthlyIncomeChange -> onMonthlyIncomeValueChange(uiEvent.monthlyIncome)
            is UIEvent.OnLoadCurrentStepData -> onLoadCurrentStepData(uiEvent.accountSmartData)
        }
    }

    sealed class UIEvent {
        data class OnCompanyNameChange(val companyName: String) : UIEvent()
        data class OnCompanyDescriptionChange(val description: String) : UIEvent()
        data class OnMonthlyIncomeChange(val monthlyIncome: String) : UIEvent()
        data class OnLoadCurrentStepData(val accountSmartData: AccountSmartData?) : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }
}
