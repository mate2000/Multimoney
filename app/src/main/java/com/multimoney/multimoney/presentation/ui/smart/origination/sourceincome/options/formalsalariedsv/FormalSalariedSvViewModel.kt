package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.model.accountsmart.AccountSmartData
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnCompanyNameChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnLoadCurrentStepData
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnProfessionChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnSalaryChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.util.MIN_INCOME
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.validateDecimalIncome
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FormalSalariedSvViewModel @Inject constructor() : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    /**
     * this function is intended to load the form data on the UI, after getting the
     * data coming from the current step (provided from the backend)
     */
    private fun onLoadCurrentStepData(accountSmartData: AccountSmartData?) {
        accountSmartData?.let {
            onCompanyNameChange(it.companyName.orEmpty())
            onProfessionChange(it.positionJob.orEmpty())
            onSalaryChange(it.income.toString())
        }
    }

    private fun onCompanyNameChange(companyName: String) {
        uiState = uiState.copy(companyName = companyName)
        onValidateForm()
    }

    private fun onProfessionChange(profession: String) {
        uiState = uiState.copy(profession = profession)
        onValidateForm()
    }

    private fun onSalaryChange(salary: String) {
        if (validateDecimalIncome(salary)) {
            uiState = uiState.copy(salary = salary)
        }
        onValidateForm()
    }

    private fun onValidateForm() = emitBaseEvent(BaseEvent.OnFormValidateCompleted(isFormValid()))

    fun isFormValid(): Boolean =
        uiState.profession.isNotBlank() &&
            uiState.companyName.isNotBlank() &&
            uiState.salary.isNotBlank() &&
            uiState.salary.toFloat() > MIN_INCOME

    data class UIState(
        var companyName: String = "",
        var profession: String = "",
        var salary: String = "",
        val isLoading: Boolean = false,
        val dialogParameters: DialogParameters = DialogParameters()
    )

    sealed class UIEvent {
        data class OnCompanyNameChange(val companyName: String) : UIEvent()
        data class OnProfessionChange(val profession: String) : UIEvent()
        data class OnSalaryChange(val salary: String) : UIEvent()
        data class OnLoadCurrentStepData(val accountSmartData: AccountSmartData?) : UIEvent()
        object OnValidateForm : UIEvent()
    }

    fun onUiEvent(event: UIEvent) {
        when (event) {
            is OnCompanyNameChange -> onCompanyNameChange(event.companyName)
            is OnProfessionChange -> onProfessionChange(event.profession)
            is OnSalaryChange -> onSalaryChange(event.salary)
            is OnLoadCurrentStepData -> onLoadCurrentStepData(event.accountSmartData)
            is OnValidateForm -> onValidateForm()
        }
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }
}
