package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinesspersonaltitle

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
class OwnBusinessTitleViewModel @Inject constructor() : BaseViewModel(true) {
    var uiState by mutableStateOf(UIState())
        private set

    data class UIState(
        var incomeAmount: String = "",
        var businessName: String = "",
        var amountError: Pair<Boolean, Int> = Pair(
            false,
            R.string.smart_business_personal_income_label_required
        ),
        var businessNameError: Pair<Boolean, Int> = Pair(
            false,
            R.string.smart_business_personal_name_required
        )
    )

    sealed class UIEvent {
        data class OnIncomeAmountChange(val income: String) : UIEvent()
        data class OnBusinessNameValueChange(val businessName: String) : UIEvent()
        object OnValidateForm : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnIncomeAmountChange -> incomeAmountChange(uiEvent.income)
            is UIEvent.OnBusinessNameValueChange -> addressValueChanged(uiEvent.businessName)
            is UIEvent.OnValidateForm -> onValidateForm()
        }
    }

    private fun incomeAmountChange(income: String) {
        if ((Pattern.matches(DECIMAL_REGEX, income) || income.isEmpty()) && income.last() != '0') {
            uiState = uiState.copy(incomeAmount = income)
        }
        onValidateForm()
    }

    private fun addressValueChanged(businessName: String) {
        uiState = uiState.copy(businessName = businessName)
        onValidateForm()
    }

    private fun onValidateForm() = emitBaseEvent(BaseEvent.OnFormValidateCompleted(isFormValid()))

    fun isFormValid() = uiState.incomeAmount.isNotBlank() &&
        uiState.businessName.isNotBlank() &&
        uiState.incomeAmount.toFloat() > 0
}
