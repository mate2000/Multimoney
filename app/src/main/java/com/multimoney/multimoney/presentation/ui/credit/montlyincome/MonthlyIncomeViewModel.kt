package com.multimoney.multimoney.presentation.ui.credit.montlyincome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.montlyincome.MonthlyIncomeViewModel.BaseEvent.OnFormCompleted
import com.multimoney.multimoney.presentation.ui.credit.montlyincome.MonthlyIncomeViewModel.UIEvent.OnIncomeValueChange
import com.multimoney.multimoney.presentation.ui.credit.montlyincome.MonthlyIncomeViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.montlyincome.MonthlyIncomeViewModel.UIEvent.OnProfessionValueChange
import com.multimoney.multimoney.presentation.ui.credit.montlyincome.MonthlyIncomeViewModel.UIEvent.OnValidForm
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MonthlyIncomeViewModel @Inject constructor() : BaseViewModel() {

    var uiState by mutableStateOf(UIState())
        private set

    val country = ZERO

    private fun onIncomeValueChange(income: String) {
        if (income.isDigitsOnly()) {
            val incomeError = if (income.isNotEmpty() && income == ZERO.toString()) {
                Pair(
                    true,
                    R.string.credit_monthly_income_greater_than_zero_error
                )
            } else {
                Pair(
                    false,
                    R.string.credit_monthly_income_greater_than_zero_error
                )
            }
            uiState = uiState.copy(
                income = income,
                incomeError = incomeError
            )
            onValidForm()
        }
    }

    private fun onProfessionValueChange(profession: String) {
        uiState = uiState.copy(profession = profession)
        onValidForm()
    }

    private fun onValidForm() {
        emitBaseEvent(
            OnFormCompleted(
                uiState.income.isNotEmpty() && uiState.profession.isNotEmpty() && uiState.income.toDouble() > ZERO
            )
        )
    }

    private fun onNextActionClick(onNextStepAction: () -> Unit) {
        onNextStepAction()
    }

    data class UIState(
        val income: String = "",
        val profession: String = "",
        val incomeError: Pair<Boolean, Int> = Pair(
            false,
            R.string.credit_monthly_income_greater_than_zero_error
        )
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNextActionClick -> onNextActionClick(uiEvent.nextStepAction)
            is OnIncomeValueChange -> onIncomeValueChange(uiEvent.income)
            is OnProfessionValueChange -> onProfessionValueChange(uiEvent.profession)
            is OnValidForm -> onValidForm()
        }
    }

    sealed class UIEvent {
        data class OnNextActionClick(val nextStepAction: () -> Unit) : UIEvent()
        data class OnIncomeValueChange(val income: String) : UIEvent()
        data class OnProfessionValueChange(val profession: String) : UIEvent()
        object OnValidForm : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormCompleted(val isFormCompleted: Boolean) : BaseEvent()
    }

    companion object {
        const val ZERO = 0
        const val ONE = 1
        const val TWO = 2
    }
}

