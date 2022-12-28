package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.independentprofessional

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.model.accountsmart.AccountSmartData
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.independentprofessional.IndProfessionalViewModel.UIEvent.OnIncomeAmountChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.independentprofessional.IndProfessionalViewModel.UIEvent.OnLoadCurrentStepData
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.independentprofessional.IndProfessionalViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.util.MIN_INCOME
import com.multimoney.multimoney.presentation.util.validateDecimalIncome
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IndProfessionalViewModel @Inject constructor() : BaseViewModel(true) {
    var uiState by mutableStateOf(UIState())
        private set

    /**
     * this function is intended to load the form data on the UI, after getting the
     * data coming from the current step (provided from the backend)
     */
    private fun onLoadCurrentStepData(accountSmartData: AccountSmartData?) {
        accountSmartData?.let {
            incomeAmountChange(it.income.toString())
        }
    }

    private fun incomeAmountChange(income: String) {
        if (validateDecimalIncome(income)) {
            uiState = uiState.copy(incomeAmount = income)
        }
        onValidateForm()
    }

    private fun onValidateForm() = emitBaseEvent(BaseEvent.OnFormValidateCompleted(isFormValid()))

    fun isFormValid() = uiState.incomeAmount.isNotBlank() &&
        uiState.incomeAmount.toFloat() > MIN_INCOME

    data class UIState(
        var incomeAmount: String = ""
    )

    sealed class UIEvent {
        data class OnIncomeAmountChange(val income: String) : UIEvent()
        data class OnLoadCurrentStepData(val accountSmartData: AccountSmartData?) : UIEvent()
        object OnValidateForm : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnIncomeAmountChange -> incomeAmountChange(uiEvent.income)
            is OnLoadCurrentStepData -> onLoadCurrentStepData(uiEvent.accountSmartData)
            is OnValidateForm -> onValidateForm()
        }
    }
}
