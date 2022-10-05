package com.multimoney.multimoney.presentation.ui.credit.montlyincome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.montlyincome.MonthlyIncomeViewModel.BaseEvent.OnFormCompleted
import com.multimoney.multimoney.presentation.ui.credit.montlyincome.MonthlyIncomeViewModel.UIEvent.OnIncomeValueChange
import com.multimoney.multimoney.presentation.ui.credit.montlyincome.MonthlyIncomeViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.montlyincome.MonthlyIncomeViewModel.UIEvent.OnProfessionValueChange
import com.multimoney.multimoney.presentation.ui.credit.montlyincome.MonthlyIncomeViewModel.UIEvent.OnValidForm
import com.multimoney.multimoney.presentation.ui.credit.montlyincome.MonthlyIncomeViewModel.UIEvent.OnLoadCreditSteps
import com.multimoney.multimoney.presentation.ui.credit.util.SaveCreditStepsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MonthlyIncomeViewModel @Inject constructor() : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

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

    private fun onNextActionClick(
        user: String,
        onNextStepAction: () -> Unit,
        saveCreditStepsHelper: SaveCreditStepsHelper
    ) {
        saveCreditStepsHelper.saveStepOne(
            user,
            uiState.income,
            getDummyOccupationCatalog(),
            CreditCatalogOption( "Ama de Casa", "255", 0)
        )
        onNextStepAction()
    }

    private fun getDummyOccupationCatalog(): CreditCatalog {
        return CreditCatalog(
            4076,
            2064,
            "ComboBoxWithInformation",
            "Ocupación",
            "270",
            false,
            false,
            true,
            "COMBO_OCUPACION",
            null,
            "",
            "",
            listOf(CreditCatalogOption("Ama de Casa", "255", 0))
        )
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
            is OnNextActionClick -> onNextActionClick(
                uiEvent.user,
                uiEvent.nextStepAction,
                uiEvent.saveCreditStepsHelper
            )
            is OnIncomeValueChange -> onIncomeValueChange(uiEvent.income)
            is OnProfessionValueChange -> onProfessionValueChange(uiEvent.profession)
            is OnValidForm -> onValidForm()
            is OnLoadCreditSteps -> loadStepsInfo(uiEvent.list)
        }
    }

    private fun loadStepsInfo(list: List<CreditCatalog?>?){
        val salary = list?.find { it?.description == SaveCreditStepsHelper.SALARY }
        uiState = uiState.copy(income = salary?.value ?: "")
    }

    sealed class UIEvent {
        data class OnNextActionClick(
            val user: String,
            val nextStepAction: () -> Unit,
            val saveCreditStepsHelper: SaveCreditStepsHelper
        ) : UIEvent()

        data class OnIncomeValueChange(val income: String) : UIEvent()
        data class OnProfessionValueChange(val profession: String) : UIEvent()
        object OnValidForm : UIEvent()
        data class OnLoadCreditSteps(val list: List<CreditCatalog?>?) : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormCompleted(val isFormCompleted: Boolean) : BaseEvent()
    }

    companion object {
        const val ZERO = 0
    }
}

