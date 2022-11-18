package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.salariedcr

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.accountsmart.QueryProfessionUseCase
import com.multimoney.domain.model.accountsmart.ProfessionSmart
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.salariedcr.SmartCrSalaryViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.salariedcr.SmartCrSalaryViewModel.UIEvent.OnCallQueryProfessionUseCase
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.salariedcr.SmartCrSalaryViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.salariedcr.SmartCrSalaryViewModel.UIEvent.OnPaymentAmountChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.salariedcr.SmartCrSalaryViewModel.UIEvent.OnProfessionChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.salariedcr.SmartCrSalaryViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.util.MIN_INCOME
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.validateDecimalIncome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SmartCrSalaryViewModel @Inject constructor(private val queryProfessionUseCase: QueryProfessionUseCase) :
    BaseViewModel(true) {
    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun validateForm() {
        emitBaseEvent(OnFormValidateCompleted(isFormValid()))
    }

    fun isFormValid() = uiState.profession.isNotBlank() &&
        uiState.paymentAmount.isNotBlank() &&
        uiState.paymentAmount.toFloat() > MIN_INCOME

    private fun onAmountValueChange(paymentAmount: String) {
        if (validateDecimalIncome(paymentAmount)) {
            uiState = uiState.copy(paymentAmount = paymentAmount)
        }
        validateForm()
    }

    private fun onProfessionValueChange(
        profession: String
    ) {
        uiState = uiState.copy(profession = profession)
        validateForm()
    }

    private fun callQueryProfessionUseCase(user: String, idBrand: Int) =
        executeUseCase {
            queryProfessionUseCase.invoke(
                user = user,
                idBrand = idBrand
            ).collectLatest { result ->
                result.onSuccess { successfulResult ->
                    successfulResult?.let {
                        uiState = uiState.copy(professionSmartList = it.status)
                    }
                    uiState = uiState.copy(isLoading = false)
                }
                result.onFailure {
                    onUIEvent(
                        OnFailureWithDialog(
                            isLoading = false,
                            openDialog = DialogParameters(
                                description = it.getError() ?: "",
                                isActive = mutableStateOf(true)
                            )
                        )
                    )
                }
                result.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        }

    data class UIState(
        // Fields
        val profession: String = "",
        val paymentAmount: String = "",
        val professionSmartList: List<ProfessionSmart?> = listOf(),
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnValidateForm -> validateForm()
            is OnPaymentAmountChange -> onAmountValueChange(event.paymentAmount)
            is OnProfessionChange -> onProfessionValueChange(event.profession)
            is OnFailureWithDialog ->
                uiState =
                    uiState.copy(isLoading = event.isLoading, openDialog = event.openDialog)
            is OnCallQueryProfessionUseCase -> callQueryProfessionUseCase(event.user, event.idBrand)
        }
    }

    sealed class UIEvent {
        data class OnPaymentAmountChange(val paymentAmount: String) : UIEvent()
        data class OnProfessionChange(val profession: String) : UIEvent()

        data class OnFailureWithDialog(val isLoading: Boolean, val openDialog: DialogParameters) :
            UIEvent()

        data class OnCallQueryProfessionUseCase(val user: String, val idBrand: Int) : UIEvent()
        object OnValidateForm : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }
}
