package com.multimoney.multimoney.presentation.ui.credit.creditamount

import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnNextActionClick
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreditAmountViewModel @Inject constructor() : BaseViewModel() {

    private fun onNextActionClick(nextStepAction: () -> Unit) {
        nextStepAction.invoke()
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNextActionClick -> onNextActionClick(uiEvent.nextStepAction)
        }
    }

    sealed class UIEvent {
        data class OnNextActionClick(val nextStepAction: () -> Unit) : UIEvent()
    }
}