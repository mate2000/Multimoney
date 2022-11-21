package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.independentprofessional

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.ADDRESS_MAX_LENGTH
import com.multimoney.multimoney.presentation.util.MIN_INCOME
import com.multimoney.multimoney.presentation.util.validateDecimalIncome
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IndProfessionalViewModel @Inject constructor() : BaseViewModel(true) {
    var uiState by mutableStateOf(UIState())
        private set

    data class UIState(
        var incomeAmount: String = "",
        var address: String = "",
        var addressError: Pair<Boolean, Int> = Pair(false, R.string.empty)
    )

    sealed class UIEvent {
        data class OnIncomeAmountChange(val income: String) : UIEvent()
        data class OnJobAddressValueChange(val address: String) : UIEvent()
        object OnValidateForm : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnIncomeAmountChange -> incomeAmountChange(uiEvent.income)
            is UIEvent.OnJobAddressValueChange -> addressValueChanged(uiEvent.address)
            is UIEvent.OnValidateForm -> onValidateForm()
        }
    }

    private fun incomeAmountChange(income: String) {
        if (validateDecimalIncome(income)) {
            uiState = uiState.copy(incomeAmount = income)
        }
        onValidateForm()
    }

    private fun addressValueChanged(address: String) {
        if (address.length < ADDRESS_MAX_LENGTH) {
            uiState = uiState.copy(
                address = address,
                addressError = Pair(false, R.string.empty)
            )
        }
        if (address.length == ADDRESS_MAX_LENGTH - 1) {
            uiState = uiState.copy(addressError = Pair(true, R.string.max_number_of_characters_reached_error))
        }
        onValidateForm()
    }

    private fun onValidateForm() = emitBaseEvent(BaseEvent.OnFormValidateCompleted(isFormValid()))

    fun isFormValid() = uiState.incomeAmount.isNotBlank() &&
        uiState.address.isNotBlank() &&
        uiState.incomeAmount.toFloat() > MIN_INCOME
}
