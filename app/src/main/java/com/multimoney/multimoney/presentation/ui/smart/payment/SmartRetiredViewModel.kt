package com.multimoney.multimoney.presentation.ui.smart.payment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.payment.SmartRetiredViewModel.UIEvent.OnInstitutionValueChange
import com.multimoney.multimoney.presentation.ui.smart.payment.SmartRetiredViewModel.UIEvent.OnPaymentAmountValueChange
import com.multimoney.multimoney.presentation.ui.smart.payment.SmartRetiredViewModel.UIEvent.OnValidateForm

class SmartRetiredViewModel : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun validateForm() {
        emitBaseEvent(
            BaseEvent.OnFormValidateCompleted(
                uiState.institution.isNotBlank()
                        && uiState.paymentAmount.isNotBlank())
        )
    }

    private fun onInstitutionValueChange(institution: String) {
        if (institution.length <= INSTITUTION_MAX_LENGTH) {
            uiState = uiState.copy(institution = institution)
        }
        validateForm()
    }

    private fun onAmountValueChange(paymentAmount: String) {
        if (paymentAmount.length <= INSTITUTION_MAX_LENGTH) {
            uiState = uiState.copy(paymentAmount = paymentAmount)
        }
        validateForm()
    }

    data class UIState(
        // Fields
        val institution: String = "",
        val paymentAmount: String = "",
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnValidateForm -> validateForm()
            is OnInstitutionValueChange -> onInstitutionValueChange(event.institution)
            is OnPaymentAmountValueChange -> onAmountValueChange(event.paymentAmount)
        }
    }

    sealed class UIEvent {
        data class OnInstitutionValueChange(val institution: String) : UIEvent()
        data class OnPaymentAmountValueChange(val paymentAmount: String) : UIEvent()
        object OnValidateForm : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    companion object {
        const val INSTITUTION_MAX_LENGTH = 100
    }
}