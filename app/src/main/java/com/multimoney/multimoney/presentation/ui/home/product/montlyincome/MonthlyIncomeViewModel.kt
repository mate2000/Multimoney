package com.multimoney.multimoney.presentation.ui.home.product.montlyincome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.home.product.montlyincome.MonthlyIncomeViewModel.BaseEvent.OnFormCompleted
import com.multimoney.multimoney.presentation.ui.home.product.montlyincome.MonthlyIncomeViewModel.UIEvent.OnIncomeValueChange
import com.multimoney.multimoney.presentation.ui.home.product.montlyincome.MonthlyIncomeViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.home.product.montlyincome.MonthlyIncomeViewModel.UIEvent.OnProfessionValueChange
import com.multimoney.multimoney.presentation.ui.home.product.montlyincome.MonthlyIncomeViewModel.UIEvent.OnValidForm
import com.multimoney.multimoney.presentation.ui.home.product.montlyincome.MonthlyIncomeViewModel.UIEvent.OnValidateIncome
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MonthlyIncomeViewModel @Inject constructor() : BaseViewModel() {

    var uiState by mutableStateOf(UIState())
        private set

    val country = 1


    private fun onIncomeValueChange(income: String) {
        uiState = uiState.copy(
            income = income, incomeError = Pair(
                false,
                R.string.home_credit_origination_monthly_income_greater_than_zero_error
            )
        )
        onValidForm()
    }

    private fun onValidateIncome() {
        if (uiState.income.isNotEmpty() && uiState.income.toDouble() <= ZERO) {
            uiState = uiState.copy(
                incomeError = Pair(
                    true,
                    R.string.home_credit_origination_monthly_income_greater_than_zero_error
                )
            )
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

    }

    data class UIState(
        val income: String = "",
        val profession: String = "",
        val incomeError: Pair<Boolean, Int> = Pair(
            false,
            R.string.home_credit_origination_monthly_income_greater_than_zero_error
        )
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNextActionClick -> onNextActionClick(uiEvent.nextStepAction)
            is OnIncomeValueChange -> onIncomeValueChange(uiEvent.income)
            is OnProfessionValueChange -> onProfessionValueChange(uiEvent.profession)
            is OnValidForm -> onValidForm()
            is OnValidateIncome -> onValidateIncome()
        }
    }

    sealed class UIEvent {
        data class OnNextActionClick(val nextStepAction: () -> Unit) : UIEvent()
        data class OnIncomeValueChange(val income: String) : UIEvent()
        data class OnProfessionValueChange(val profession: String) : UIEvent()
        object OnValidateIncome : UIEvent()
        object OnValidForm : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormCompleted(val isFormCompleted: Boolean) : BaseEvent()
    }
}

const val ZERO = 0