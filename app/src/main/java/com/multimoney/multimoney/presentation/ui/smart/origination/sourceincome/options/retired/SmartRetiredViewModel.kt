package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.retired

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.model.accountsmart.AccountSmartData
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.retired.SmartRetiredViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.retired.SmartRetiredViewModel.UIEvent.OnInstitutionValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.retired.SmartRetiredViewModel.UIEvent.OnPaymentAmountValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.retired.SmartRetiredViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.util.MIN_INCOME
import com.multimoney.multimoney.presentation.util.validateDecimalIncome

class SmartRetiredViewModel : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun validateForm() =
        emitBaseEvent(OnFormValidateCompleted(isFormValid()))

    fun isFormValid() = uiState.institution.isNotBlank() &&
        uiState.paymentAmount.isNotBlank() &&
        uiState.paymentAmount.toFloat() > MIN_INCOME &&
        uiState.institutionError.first.not()

    /**
     * this function is intended to load the form data on the UI, after getting the
     * data coming from the current step (provided from the backend)
     */
    private fun onLoadCurrentStepData(accountSmartData: AccountSmartData?) {
        accountSmartData?.let {
            onInstitutionValueChange(it.institutionPension)
            onAmountValueChange(it.income.toString())
        }
    }

    private fun onInstitutionValueChange(institution: String) {
        uiState = if (institution.length <= INSTITUTION_MAX_LENGTH) {
            uiState.copy(
                institution = institution,
                institutionError = Pair(false, R.string.empty)
            )
        } else {
            uiState.copy(
                institution = institution,
                institutionError = Pair(
                    true,
                    R.string.you_have_exceeded_the_max_characters_error
                )
            )
        }
        validateForm()
    }

    private fun onAmountValueChange(paymentAmount: String) {
        if (validateDecimalIncome(paymentAmount)) {
            uiState = uiState.copy(paymentAmount = paymentAmount)
        }
        validateForm()
    }

    data class UIState(
        // Fields
        val institution: String = "",
        val institutionError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val paymentAmount: String = ""
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnValidateForm -> validateForm()
            is OnInstitutionValueChange -> onInstitutionValueChange(event.institution)
            is OnPaymentAmountValueChange -> onAmountValueChange(event.paymentAmount)
            is UIEvent.OnLoadCurrentStepData -> onLoadCurrentStepData(event.accountSmartData)
        }
    }

    sealed class UIEvent {
        data class OnInstitutionValueChange(val institution: String) : UIEvent()
        data class OnPaymentAmountValueChange(val paymentAmount: String) : UIEvent()
        data class OnLoadCurrentStepData(val accountSmartData: AccountSmartData?) : UIEvent()
        object OnValidateForm : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    companion object {
        const val INSTITUTION_MAX_LENGTH = 100
    }
}
