package com.multimoney.multimoney.presentation.ui.credit.ibanaccount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewModelScope
import com.multimoney.domain.interaction.security.QueryValidateBankAccountUseCase
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.BaseEvent.OnFormCompleted
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.UIEvent.OnIncomeValueChange
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.UIEvent.OnLoadCreditSteps
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.UIEvent.OnProfessionValueChange
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.UIEvent.OnValidForm
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.UIEvent.OnUpdateUserInfo
import com.multimoney.multimoney.presentation.ui.credit.util.SaveCreditStepsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@HiltViewModel
class IbanAccountViewModel @Inject constructor(
    val queryValidateBankAccountUseCase: QueryValidateBankAccountUseCase
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    // viewmodel variables
    private var idBrand: Int = 0
    private var identification: String = ""
    private var email: String = ""
    private var isAccountValid: Boolean = false
    private var errorMessage: String = ""

    private val textSearch = MutableStateFlow("")

    private fun onIncomeValueChange(bankAccount: String) {
        if (bankAccount.isDigitsOnly()) {
            when (bankAccount.length) {
                in 0..20 -> {
                    uiState = uiState.copy(
                        income = bankAccount
                    )
                    textSearch.value = bankAccount
                    viewModelScope.launch {
                        textSearch.debounce(1500).collect {
                            uiState = if (it.isEmpty() || it.length == 20) {
                                uiState.copy(
                                    income = bankAccount,
                                    incomeError = Pair(false, R.string.empty)
                                )
                            } else {
                                uiState.copy(incomeError = Pair(true, R.string.iban_account_error))
                            }
                            if (it.length == 20) {
                                executeUseCase {
                                    queryValidateBankAccountUseCase(
                                        bankAccount,
                                        identification,
                                        email,
                                        idBrand
                                    ).collectLatest {
                                        it.onSuccess {
                                            it?.let {response ->
                                                when(response.responseCode){
                                                    0 -> {
                                                        isAccountValid = true
                                                    }
                                                    1 -> {
                                                        isAccountValid = false
                                                        errorMessage = response.responseMessage
                                                    }
                                                }
                                            }
                                        }
                                        it.onFailure {

                                        }
                                    }
                                }
                            }
                        }
                    }
                    //onValidateScreen()
                }
                /*val incomeError = if (income.isNotEmpty() && income == ZERO.toString()) {
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
                onValidForm()*/
            }
        }
    }

    private fun onUpdateUserInfo(
        identification: String,
        email: String,
        idBrand: String
    ) {
        this.identification = identification
        this.email = email
        this.idBrand = idBrand.toInt()
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
        // todo save step
        /* saveCreditStepsHelper.saveStepOne(

         )
         onNextStepAction()*/
    }

    data class UIState(
        val income: String = "",
        val profession: String = "",
        val incomeError: Pair<Boolean, Int> = Pair(
            false,
            R.string.empty
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
            is OnUpdateUserInfo -> onUpdateUserInfo(
                uiEvent.identification,
                uiEvent.email,
                uiEvent.idBrand
            )
        }
    }

    private fun loadStepsInfo(list: List<CreditCatalog?>?) {
        val salary = list?.find { it?.description == SaveCreditStepsHelper.SALARY }
        uiState = uiState.copy(income = salary?.value ?: "")
    }

    sealed class UIEvent {
        data class OnNextActionClick(
            val user: String,
            val nextStepAction: () -> Unit,
            val saveCreditStepsHelper: SaveCreditStepsHelper
        ) : UIEvent()

        data class OnUpdateUserInfo(
            val identification: String,
            val email: String,
            val idBrand: String
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

