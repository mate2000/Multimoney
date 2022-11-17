package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.otherincome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.DECIMAL_REGEX
import com.multimoney.multimoney.presentation.util.DESCRIPTION_MAX_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class OtherIncomeViewModel @Inject constructor() : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    data class UIState(
        var incomeAmount: String = "",
        var incomeSource: String = "",
        var sourceError: Pair<Boolean, Int> = Pair(false, R.string.smart_other_source_of_income_required)
    )

    sealed class UIEvent {
        data class OnIncomeAmountChange(val income: String) : UIEvent()
        data class OnIncomeSourceChange(val income: String) : UIEvent()
        object OnValidateForm : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnIncomeAmountChange -> incomeAmountChange(uiEvent.income)
            is UIEvent.OnIncomeSourceChange -> incomeSourceChange(uiEvent.income)
            is UIEvent.OnValidateForm -> onValidateForm()
        }
    }
    private fun incomeAmountChange(income: String) {
        if ((Pattern.matches(DECIMAL_REGEX, income) || income.isEmpty()) && income.last() != '0') {
            uiState = uiState.copy(incomeAmount = income)
        }
        onValidateForm()
    }

    private fun incomeSourceChange(source: String) {
        uiState = if (source.length < DESCRIPTION_MAX_LENGTH) {
            uiState.copy(incomeSource = source)
        } else {
            uiState.copy(sourceError = Pair(true, R.string.smart_own_business_description_max_char_error))
        }
        onValidateForm()
    }

    private fun onValidateForm() = emitBaseEvent(BaseEvent.OnFormValidateCompleted(isFormValid()))

    fun isFormValid() = uiState.incomeSource.isNotBlank() &&
        uiState.incomeAmount.isNotBlank() &&
        uiState.incomeAmount.toFloat() > 0
}
