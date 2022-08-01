package com.multimoney.multimoney.presentation.ui.credit.creditamount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.BaseEvent.OnOpenConditionOfCreditDialog
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnInitializeText
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnOpenConditionCreditDialog
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnOpenTermAndCondition
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnTermAndConditionCheckedChange
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreditAmountViewModel @Inject constructor() : BaseViewModel() {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // stateless
    var conditionModalDescription: String = ""

    private fun onNextActionClick(nextStepAction: () -> Unit) {
        nextStepAction.invoke()
    }

    private fun onOpenConditionCreditDialog() {
        emitBaseEvent(
            OnOpenConditionOfCreditDialog(
                DialogParameters(
                    title = R.string.credit_amount_condition_of_credit_info,
                    description = conditionModalDescription,
                    isActive = mutableStateOf(true),
                    positiveText = R.string.accept
                )
            )
        )
    }

    private fun onOpenTermAndCondition() {

    }

    data class UIState(
        val isTermAndConditionChecked: Boolean = false,
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnInitializeText -> conditionModalDescription = uiEvent.dialogDescription
            is OnNextActionClick -> onNextActionClick(uiEvent.nextStepAction)
            is OnTermAndConditionCheckedChange -> uiState = uiState.copy(isTermAndConditionChecked = uiEvent.isChecked)
            is OnOpenConditionCreditDialog -> onOpenConditionCreditDialog()
            is OnOpenTermAndCondition -> onOpenTermAndCondition()
        }
    }

    sealed class UIEvent {
        data class OnInitializeText(val dialogDescription: String) : UIEvent()
        data class OnNextActionClick(val nextStepAction: () -> Unit) : UIEvent()
        data class OnTermAndConditionCheckedChange(val isChecked: Boolean) : UIEvent()
        object OnOpenConditionCreditDialog : UIEvent()
        object OnOpenTermAndCondition : UIEvent()
    }

    sealed class BaseEvent {
        data class OnOpenConditionOfCreditDialog(val dialogParameters: DialogParameters) : BaseEvent()
    }
}