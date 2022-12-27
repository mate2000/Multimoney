package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinesspersonaltitle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.model.accountsmart.AccountSmartData
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinesspersonaltitle.OwnBusinessTitleViewModel.UIEvent.OnBusinessNameValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinesspersonaltitle.OwnBusinessTitleViewModel.UIEvent.OnGetUserData
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinesspersonaltitle.OwnBusinessTitleViewModel.UIEvent.OnIncomeAmountChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinesspersonaltitle.OwnBusinessTitleViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.util.MIN_INCOME
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.validateDecimalIncome
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OwnBusinessTitleViewModel @Inject constructor() : BaseViewModel(true) {
    var uiState by mutableStateOf(UIState())
        private set

    /**
     * this function is intended to load the form data on the UI, after getting the
     * data coming from the current step (provided from the backend)
     */
    private fun onLoadCurrentStepData(accountSmartData: AccountSmartData?) {
        accountSmartData?.let {
            incomeAmountChange(it.income.toString())
            addressValueChanged(it.entrepreneurship ?: "")
        }
    }

    private fun incomeAmountChange(income: String) {
        if (validateDecimalIncome(income)) {
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
        uiState.incomeAmount.toFloat() > MIN_INCOME

    data class UIState(
        var incomeAmount: String = "",
        var businessName: String = "",
        val dialogParameters: DialogParameters = DialogParameters()
    )

    sealed class UIEvent {
        data class OnIncomeAmountChange(val income: String) : UIEvent()
        data class OnBusinessNameValueChange(val businessName: String) : UIEvent()
        data class OnGetUserData(
            val accountSmartData: AccountSmartData?
        ) : UIEvent()

        object OnValidateForm : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnIncomeAmountChange -> incomeAmountChange(uiEvent.income)
            is OnBusinessNameValueChange -> addressValueChanged(uiEvent.businessName)
            is OnValidateForm -> onValidateForm()
            is OnGetUserData -> {
                onLoadCurrentStepData(uiEvent.accountSmartData)
            }
        }
    }
}
