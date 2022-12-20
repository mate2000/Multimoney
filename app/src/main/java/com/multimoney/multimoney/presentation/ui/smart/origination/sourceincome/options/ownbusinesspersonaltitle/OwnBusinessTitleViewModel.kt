package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinesspersonaltitle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelOneUseCase
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelThreeUseCase
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelTwoUseCase
import com.multimoney.domain.model.accountsmart.AccountSmartData
import com.multimoney.domain.model.accountsmart.Address
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.BaseEvent
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.Companion
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinesspersonaltitle.OwnBusinessTitleViewModel.UIEvent.OnRequestError
import com.multimoney.multimoney.presentation.util.MIN_INCOME
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.validateDecimalIncome
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class OwnBusinessTitleViewModel @Inject constructor(
    val addressLevelOneUseCase: QueryAddressLevelOneUseCase,
    val addressLevelTwoUseCase: QueryAddressLevelTwoUseCase,
    val addressLevelThreeUseCase: QueryAddressLevelThreeUseCase
) : BaseViewModel(true) {
    var uiState by mutableStateOf(UIState())
        private set

    private fun incomeAmountChange(income: String) {
        if (validateDecimalIncome(income)) {
            uiState = uiState.copy(incomeAmount = income)
        }
        onValidateForm()
    }

    private fun addressValueChanged(businessName: String) {
        uiState = uiState.copy(businessName = businessName)
        onValidateForm()
    }

    private fun getDivisionOne(user: String, idBrand: Int) {
        executeUseCase {
            addressLevelOneUseCase(
                user,
                idBrand
            ).collectLatest {
                it.onSuccess { addressList ->
                    uiState = uiState.copy(
                        divisionOneList = addressList?.addresses,
                        isLoading = false
                    )
                }
                it.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
                it.onFailure { error ->
                    uiState = uiState.copy(
                        dialogParameters = DialogParameters(
                            description = error.getError() ?: "",
                            isActive = mutableStateOf(true)
                        ),
                        isLoading = false
                    )
                    onRequestError()
                }
            }
        }
    }


    private fun onRequestError() = emitBaseEvent(OnRequestError(uiState.dialogParameters))

    /**
     * this function is intended to load the form data on the UI, after getting the
     * data coming from the current step (provided from the backend)
     */
    private fun onLoadCurrentStepData(accountSmartData: AccountSmartData?) {
        accountSmartData?.let {
            incomeAmountChange(it.income.toString())
            addressValueChanged(it.entrepreneurship)
        }
    }

    private fun onValidateForm() = emitBaseEvent(BaseEvent.OnFormValidateCompleted(isFormValid()))

    fun isFormValid() = uiState.incomeAmount.isNotBlank() &&
        uiState.businessName.isNotBlank() &&
        uiState.incomeAmount.toFloat() > MIN_INCOME

    data class UIState(
        var incomeAmount: String = "",
        var businessName: String = "",
        val divisionOneList: List<Address?>? = listOf(),
        val divisionTwoList: List<Address?>? = listOf(),
        val divisionThreeList: List<Address?>? = listOf(),
        val divisionOneSelected: Address? = null,
        val divisionTwoSelected: Address? = null,
        val divisionThreeSelected: Address? = null,
        val isLoading: Boolean = false,
        val dialogParameters: DialogParameters = DialogParameters()
    )

    sealed class UIEvent {
        data class OnIncomeAmountChange(val income: String) : UIEvent()
        data class OnBusinessNameValueChange(val businessName: String) : UIEvent()
        data class OnLoadCurrentStepData(val accountSmartData: AccountSmartData?) : UIEvent()
        data class OnRequestError(val dialogParameters: DialogParameters) : UIEvent()
        object OnValidateForm : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnIncomeAmountChange -> incomeAmountChange(uiEvent.income)
            is UIEvent.OnBusinessNameValueChange -> addressValueChanged(uiEvent.businessName)
            is UIEvent.OnValidateForm -> onValidateForm()
            is UIEvent.OnLoadCurrentStepData -> onLoadCurrentStepData(uiEvent.accountSmartData)
        }
    }
}
