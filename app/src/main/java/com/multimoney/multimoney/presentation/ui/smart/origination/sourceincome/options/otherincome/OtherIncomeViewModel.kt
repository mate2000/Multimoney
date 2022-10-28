package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.otherincome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.DECIMAL_REGEX
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class OtherIncomeViewModel @Inject constructor() : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    data class UIState(
        var incomeAmount: String = "",
        var amountError: Pair<Boolean, Int> = Pair(false, R.string.smart_own_business_monthly_income_required),
        var incomeSource: String = "",
        var sourceError: Pair<Boolean, Int> = Pair(false, R.string.smart_other_source_of_income_required)
    )

    sealed class UIEvent {
        data class OnIncomeAmountChange(val income: String) : UIEvent()
        data class OnIncomeSourceChange(val income: String) : UIEvent()
        data class OnNextActionClick(val nextStepAction: () -> Unit) : UIEvent()
        object OnValidateForm : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnIncomeAmountChange -> incomeAmountChange(uiEvent.income)
            is UIEvent.OnIncomeSourceChange -> incomeSourceChange(uiEvent.income)
            is UIEvent.OnNextActionClick -> onNextActionClick(uiEvent.nextStepAction)
            is UIEvent.OnValidateForm -> validateForm()
        }
    }
    private fun incomeAmountChange(income: String) {
        if (Pattern.matches(DECIMAL_REGEX, income) || income.isEmpty()) {
            uiState = uiState.copy(incomeAmount = income)
        }
        validateForm()
    }

    private fun incomeSourceChange(source: String) {
        uiState = if (source.length < 150) {
            uiState.copy(incomeSource = source)
        } else {
            uiState.copy(sourceError = Pair(true, R.string.smart_own_business_description_max_char_error))
        }
        validateForm()
    }

    private fun validateForm() {
        emitBaseEvent(
            BaseEvent.OnFormValidateCompleted(
                uiState.incomeSource.isNotBlank() && uiState.incomeAmount.isNotBlank()
            )
        )
    }

    private fun onNextActionClick(nextStepAction: () -> Unit) {
        nextStepAction()
    }
}
