package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.otherincome

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
class OtherIncomeViewModel @Inject constructor() : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    private fun incomeAmountChange(income: String) {
        if (validateDecimalIncome(income)) {
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
        uiState.incomeAmount.toFloat() > MIN_INCOME

    data class UIState(
        var incomeAmount: String = "",
        var incomeSource: String = "",
        var sourceError: Pair<Boolean, Int> = Pair(false, R.string.empty)
    )

    sealed class UIEvent {
        data class OnIncomeAmountChange(val income: String) : UIEvent()
        data class OnIncomeSourceChange(val income: String) : UIEvent()
        object OnValidateForm : UIEvent()
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnIncomeAmountChange -> incomeAmountChange(uiEvent.income)
            is UIEvent.OnIncomeSourceChange -> incomeSourceChange(uiEvent.income)
            is UIEvent.OnValidateForm -> onValidateForm()
        }
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }
}
