package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnCompanyNameChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnProfessionChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnSalaryChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnWorkingAddressChange
import com.multimoney.multimoney.presentation.util.DECIMAL_REGEX
import com.multimoney.multimoney.presentation.util.DESCRIPTION_MAX_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class FormalSalariedSvViewModel @Inject constructor() : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    data class UIState(
        var companyName: String = "",
        var profession: String = "",
        var salary: String = "",
        var workingAddress: String = "",
        var workingAddressError: Pair<Boolean, Int> = Pair(false, R.string.empty)
    )

    sealed class UIEvent {
        data class OnCompanyNameChange(val companyName: String) : UIEvent()
        data class OnProfessionChange(val profession: String) : UIEvent()
        data class OnSalaryChange(val salary: String) : UIEvent()
        data class OnWorkingAddressChange(val address: String) : UIEvent()
        object OnValidateForm : UIEvent()
    }

    fun onUiEvent(event: UIEvent) {
        when (event) {
            is OnCompanyNameChange -> onCompanyNameChange(event.companyName)
            is OnProfessionChange -> onProfessionChange(event.profession)
            is OnSalaryChange -> onSalaryChange(event.salary)
            is OnWorkingAddressChange -> onWorkingAddressChange(event.address)
            is OnValidateForm -> onValidateForm()
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
        if (Pattern.matches(DECIMAL_REGEX, salary) || salary.isEmpty()) {
            uiState = uiState.copy(salary = salary)
        }
        onValidateForm()
    }

    private fun onWorkingAddressChange(address: String) {
        uiState = if (address.length < DESCRIPTION_MAX_LENGTH) {
            uiState.copy(
                workingAddress = address,
                workingAddressError = Pair(false, R.string.empty)
            )
        } else {
            uiState.copy(
                workingAddressError = Pair(true, R.string.smart_own_business_description_max_char_error)
            )
        }
        onValidateForm()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    private fun onValidateForm() = emitBaseEvent(BaseEvent.OnFormValidateCompleted(isFormValid()))

    fun isFormValid(): Boolean =
        uiState.profession.isNotBlank() &&
            uiState.companyName.isNotBlank() &&
            uiState.salary.isNotBlank() &&
            uiState.workingAddress.isNotBlank()
}
