package com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.credit.QueryBanksAndRegularExpressionUseCase
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.domain.model.credit.RegularExpression
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling.CreditCrosselingBankViewModel.BaseEvent.OnFormCompleted
import com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling.CreditCrosselingBankViewModel.UIEvent.OnAccountNumberValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling.CreditCrosselingBankViewModel.UIEvent.OnAccountTypeValueChanged
import com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling.CreditCrosselingBankViewModel.UIEvent.OnBankValueChanged
import com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling.CreditCrosselingBankViewModel.UIEvent.OnCallQueryBanksAndRegularExpression
import com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling.CreditCrosselingBankViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling.CreditCrosselingBankViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.ui.credit.origination.util.SaveCreditStepsHelper
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getRegex
import com.multimoney.multimoney.presentation.util.matchRegex
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class CreditCrosselingBankViewModel @Inject constructor(
    private val queryBanksAndRegularExpressionUseCase: QueryBanksAndRegularExpressionUseCase
) : BaseViewModel(true) {

    // Stateless
    private var bank: CreditCatalog? = null
    private var bankList: List<CreditCatalogOption?>? = listOf()
    var accountTypeList: List<RegularExpression?>? = listOf()

    var uiState by mutableStateOf(UIState())
        private set

    private fun onCallQueryBanksAndRegularExpressions(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int,
        list: List<CreditCatalog?>?,
        onLoadingValueChange: (isLoading: Boolean) -> Unit,
        onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryBanksAndRegularExpressionUseCase.invoke(pkUser, user, idBrand, idUserRequest).collectLatest { result ->
            result.onSuccess {
                bank = it.banks?.first()
                accountTypeList = it.regularExpression
                bankList = bank?.subOptions?.filter { filter ->
                    filter?.description != MIDDLE_DASH
                }
                uiState = uiState.copy(bankList = bankList)
                if (!bank?.pkCatalog.isNullOrEmpty()) {
                    loadStepsInfo(list)
                }
                onLoadingValueChange(false)
            }.onLoading {
                onLoadingValueChange(true)
            }.onFailure {
                onFailureWithDialog(
                    false,
                    DialogParameters(
                        description = it.getError().toString(),
                        isActive = mutableStateOf(true)
                    )
                )
            }
        }
    }

    private fun onBankValueChanged(bankSelected: CreditCatalogOption?) {
        uiState = uiState.copy(
            bankSelected = bankSelected,
            accountTypeListFiltered = accountTypeList?.filter {
                it?.fkRegularExpression == bankSelected?.pkCatalog?.toInt()
            },
            accountTypeSelectedString = "",
            accountTypeSelected = null,
            accountNumber = "",
            accountNumberError = Pair(false, R.string.empty)
        )
        validateForm()
    }

    private fun onAccountTypeValueChange(regulaExpression: RegularExpression?) {
        uiState = uiState.copy(
            accountTypeSelectedString = regulaExpression?.description ?: "",
            accountTypeSelected = regulaExpression,
            accountNumber = "",
            accountNumberError = Pair(false, R.string.empty)
        )
        validateForm()
    }

    private fun onAccountNumberValueChanged(accountNumber: String) {
        uiState = uiState.copy(
            accountNumber = accountNumber,
            accountNumberError =
            if (matchRegex(accountNumber, getRegex(uiState.accountTypeSelected?.regularExpression.orEmpty()))) {
                Pair(false, R.string.empty)
            } else {
                Pair(true, R.string.credit_bank_account_number_error)
            }
        )
        validateForm()
    }

    private fun validateForm() {
        emitBaseEvent(
            OnFormCompleted(
                uiState.bankSelected != null && uiState.accountTypeSelected != null && uiState.accountNumber.isNotEmpty()
            )
        )
    }

    private fun onNextActionClick(
        user: String,
        nextStepAction: () -> Unit,
        saveCreditStepsHelper: SaveCreditStepsHelper
    ) {
        saveCreditStepsHelper.saveStepOne(
            user,
            bank,
            uiState.bankSelected,
            uiState.accountTypeSelected,
            uiState.accountNumber
        )
        nextStepAction()
    }

    data class UIState(
        val accountNumber: String = "",
        val accountNumberError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val bankList: List<CreditCatalogOption?>? = listOf(),
        val bankSelected: CreditCatalogOption? = null,
        val accountTypeListFiltered: List<RegularExpression?>? = listOf(),
        val accountTypeSelectedString: String = "",
        val accountTypeSelectedKey: String = "",
        val accountTypeSelected: RegularExpression? = null
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnNextActionClick -> onNextActionClick(event.user, event.nextStepAction, event.saveCreditStepsHelper)
            is OnCallQueryBanksAndRegularExpression -> onCallQueryBanksAndRegularExpressions(
                event.pkUser,
                event.user,
                event.idBrand,
                event.idUserRequest,
                event.list,
                event.onLoadingValueChange,
                event.onFailureWithDialog
            )
            is OnValidateForm -> validateForm()
            is OnAccountNumberValueChange -> onAccountNumberValueChanged(event.accountNumber)
            is OnBankValueChanged -> onBankValueChanged(event.bankSelected)
            is OnAccountTypeValueChanged -> onAccountTypeValueChange(event.regularExpressionSelected)
        }
    }

    private fun loadStepsInfo(list: List<CreditCatalog?>?) {
        val bankSelected = bankList?.find { it?.pkCatalog == bank?.pkCatalog }
        val accountType = list?.find { it?.description == SaveCreditStepsHelper.ACCOUNT_TYPE }
        val accountTypeListFiltered =
            accountTypeList?.filter { it?.fkRegularExpression == bankSelected?.pkCatalog?.toInt() }
        val accountNumber = list?.find { it?.description == SaveCreditStepsHelper.ACCOUNT_NUMBER }
        val accountTypeSelected = accountTypeListFiltered?.findLast { it?.key == accountType?.value }
        uiState = uiState.copy(
            bankSelected = bankSelected,
            accountTypeListFiltered = accountTypeListFiltered,
            accountTypeSelectedString = accountTypeSelected?.description
                ?: "",
            accountTypeSelected = accountTypeSelected,
            accountNumber = accountNumber?.value ?: ""
        )
        validateForm()
    }

    sealed class UIEvent {
        data class OnNextActionClick(
            val user: String,
            val nextStepAction: () -> Unit,
            val saveCreditStepsHelper: SaveCreditStepsHelper
        ) : UIEvent()

        data class OnCallQueryBanksAndRegularExpression(
            val pkUser: Int,
            val user: String,
            val idBrand: Int,
            val idUserRequest: Int,
            val list: List<CreditCatalog?>?,
            val onLoadingValueChange: (isLoading: Boolean) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
        ) : UIEvent()

        object OnValidateForm : UIEvent()
        data class OnAccountNumberValueChange(val accountNumber: String) : UIEvent()
        data class OnBankValueChanged(val bankSelected: CreditCatalogOption?) : UIEvent()
        data class OnAccountTypeValueChanged(val regularExpressionSelected: RegularExpression?) : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormCompleted(val isFormCompleted: Boolean) : BaseEvent()
    }

    companion object {
        const val MIDDLE_DASH = "-"
    }
}
