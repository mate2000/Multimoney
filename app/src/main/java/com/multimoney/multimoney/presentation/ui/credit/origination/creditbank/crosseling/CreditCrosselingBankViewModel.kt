package com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.accountsmart.MutationSaveSinpeAccountUseCase
import com.multimoney.domain.interaction.credit.QueryBankList365TypeAndAccountTypeUseCase
import com.multimoney.domain.interaction.credit.QueryBanksAndRegularExpressionUseCase
import com.multimoney.domain.model.accountsmart.BankTransfer365
import com.multimoney.domain.model.accountsmart.SmartAccountType
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
import com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling.CreditCrosselingBankViewModel.UIEvent.OnCallQueryBankList365TypeAccountType
import com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling.CreditCrosselingBankViewModel.UIEvent.OnCallQueryBanksAndRegularExpression
import com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling.CreditCrosselingBankViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling.CreditCrosselingBankViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.ui.credit.origination.util.SaveCreditStepsHelper
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class CreditCrosselingBankViewModel @Inject constructor(
    private val queryBanksAndRegularExpressionUseCase: QueryBanksAndRegularExpressionUseCase,
    private val mutationSaveSinpeAccountUseCase: MutationSaveSinpeAccountUseCase,
    private val queryBankList365TypeAndAccountTypeUseCase: QueryBankList365TypeAndAccountTypeUseCase
) : BaseViewModel(true) {

    // Stateless
    private var bank: CreditCatalog? = null
    private var bankListCreditCatalog: List<CreditCatalogOption?>? = listOf()
    private var regularExpression: List<RegularExpression?>? = listOf()
    private var bankList: List<BankTransfer365?>? = listOf()
    var accountTypeList: List<SmartAccountType?>? = listOf()

    var uiState by mutableStateOf(UIState())
        private set

    private fun onCallQueryBankList365TypeAndAccountType(
        user: String,
        idBrand: Int,
        onLoadingValueChange: (isLoading: Boolean) -> Unit,
        onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryBankList365TypeAndAccountTypeUseCase.invoke(idBrand, user).collectLatest { result ->
            result.onSuccess {
                accountTypeList = it?.accountType?.typeList
                bankList = it?.bankList365Type
                uiState = uiState.copy(bankList = bankList)
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

    private fun onCallQueryBanksAndRegularExpressions(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int,
        onFailureWithDialog: (dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryBanksAndRegularExpressionUseCase.invoke(pkUser, user, idBrand, idUserRequest).collectLatest { result ->
            result.onSuccess {
                bank = it.banks?.first()
                regularExpression = it.regularExpression
                bankListCreditCatalog = bank?.subOptions?.filter { filter ->
                    filter?.description != MIDDLE_DASH
                }
            }.onFailure {
                onFailureWithDialog(
                    DialogParameters(
                        description = it.getError().toString(),
                        isActive = mutableStateOf(true)
                    )
                )
            }
        }
    }

    private fun onSaveBankAccount(
        idBrand: Int,
        user: String,
        identification: String,
        onLoadingValueChange: (Boolean) -> Unit,
        onFailureWithDialog: (Boolean, DialogParameters) -> Unit,
        nextStepAction: () -> Unit,
        saveCreditStepsHelper: SaveCreditStepsHelper
    ) = executeUseCase {
        mutationSaveSinpeAccountUseCase(
            user = user,
            idBrand = idBrand,
            identification = identification,
            accountNumber = uiState.accountNumber,
            idCurrency = CurrencyType.Dollar.id.toLong(),
            nameAccount = uiState.bankSelected?.bankName ?: "",
            country = Brand.ElSalvador.countryCode,
            idAccount = null,
            option = null,
            email = user,
            isFavorite = false,
            idBank = uiState.bankSelected?.bankId,
            typeAccount = uiState.accountTypeSelected?.typeId?.toInt()
        ).collectLatest {
            it.onSuccess {
                onLoadingValueChange(false)
                onNextActionClick(user, nextStepAction, saveCreditStepsHelper)
            }.onFailure { error ->
                onFailureWithDialog(
                    false,
                    DialogParameters(
                        description = error.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                onLoadingValueChange(true)
            }
        }
    }

    private fun onBankValueChanged(bankSelected: BankTransfer365?) {
        uiState = uiState.copy(
            bankSelected = bankSelected,
            bankSelectedString = bankSelected?.bankName ?: "",
            accountTypeList = accountTypeList,
            accountTypeSelectedString = "",
            accountTypeSelected = null,
            accountNumber = "",
            accountNumberError = Pair(false, R.string.empty)
        )
        validateForm()
    }

    private fun onAccountTypeValueChange(smartAccountType: SmartAccountType?) {
        uiState = uiState.copy(
            accountTypeSelectedString = smartAccountType?.typeName ?: "",
            accountTypeSelected = smartAccountType,
            accountNumber = "",
            accountNumberError = Pair(false, R.string.empty)
        )
        validateForm()
    }

    private fun onAccountNumberValueChanged(accountNumber: String) {
        uiState = uiState.copy(
            accountNumber = accountNumber,
            accountNumberError =
            if (accountNumber.length >= ACCOUNT_LENGTH) {
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
//        saveCreditStepsHelper.saveStepOneCrosselingSv(
//            user,
//            bank,
//            uiState.bankSelected,
//            uiState.accountTypeSelected,
//            uiState.accountNumber
//        )
        nextStepAction()
    }

    data class UIState(
        val accountNumber: String = "",
        val accountNumberError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val bankList: List<BankTransfer365?>? = listOf(),
        val bankSelectedString: String = "",
        val bankSelected: BankTransfer365? = null,
        val accountTypeList: List<SmartAccountType?>? = listOf(),
        val accountTypeSelectedString: String = "",
        val accountTypeSelectedKey: String = "",
        val accountTypeSelected: SmartAccountType? = null
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnNextActionClick -> onNextActionClick(event.user, event.nextStepAction, event.saveCreditStepsHelper)
            is OnCallQueryBankList365TypeAccountType -> onCallQueryBankList365TypeAndAccountType(
                event.user,
                event.idBrand,
                event.onLoadingValueChange,
                event.onFailureWithDialog
            )
            is OnValidateForm -> validateForm()
            is OnAccountNumberValueChange -> onAccountNumberValueChanged(event.accountNumber)
            is OnBankValueChanged -> onBankValueChanged(event.bankSelected)
            is OnAccountTypeValueChanged -> onAccountTypeValueChange(event.smartAccountType)
            is OnCallQueryBanksAndRegularExpression -> onCallQueryBanksAndRegularExpressions(
                event.pkUser,
                event.user,
                event.idBrand,
                event.idUserRequest,
                event.onFailureWithDialog
            )
        }
    }

    sealed class UIEvent {
        data class OnNextActionClick(
            val user: String,
            val nextStepAction: () -> Unit,
            val saveCreditStepsHelper: SaveCreditStepsHelper
        ) : UIEvent()

        data class OnCallQueryBankList365TypeAccountType(
            val user: String,
            val idBrand: Int,
            val onLoadingValueChange: (isLoading: Boolean) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
        ) : UIEvent()

        object OnValidateForm : UIEvent()
        data class OnAccountNumberValueChange(val accountNumber: String) : UIEvent()
        data class OnBankValueChanged(val bankSelected: BankTransfer365?) : UIEvent()
        data class OnAccountTypeValueChanged(val smartAccountType: SmartAccountType?) : UIEvent()

        data class OnCallQueryBanksAndRegularExpression(
            val pkUser: Int,
            val user: String,
            val idBrand: Int,
            val idUserRequest: Int,
            val onFailureWithDialog: (dialogParameter: DialogParameters) -> Unit
        ) : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormCompleted(val isFormCompleted: Boolean) : BaseEvent()
    }

    companion object {
        const val MIDDLE_DASH = "-"
        const val ACCOUNT_LENGTH = 9
    }
}