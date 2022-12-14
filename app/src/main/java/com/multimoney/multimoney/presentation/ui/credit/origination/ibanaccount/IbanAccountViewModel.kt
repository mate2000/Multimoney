package com.multimoney.multimoney.presentation.ui.credit.origination.ibanaccount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.security.QueryValidateBankAccountUseCase
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.security.ValidateAccount
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.ibanaccount.IbanAccountViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.credit.origination.ibanaccount.IbanAccountViewModel.UIEvent.OnAccountValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.ibanaccount.IbanAccountViewModel.UIEvent.OnLoadCreditSteps
import com.multimoney.multimoney.presentation.ui.credit.origination.ibanaccount.IbanAccountViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.origination.ibanaccount.IbanAccountViewModel.UIEvent.OnOpenInformativeDialog
import com.multimoney.multimoney.presentation.ui.credit.origination.ibanaccount.IbanAccountViewModel.UIEvent.OnUpdateUserInfo
import com.multimoney.multimoney.presentation.ui.credit.origination.ibanaccount.IbanAccountViewModel.UIEvent.OnValidForm
import com.multimoney.multimoney.presentation.ui.credit.origination.util.SaveCreditStepsHelper
import com.multimoney.multimoney.presentation.util.capitalized
import com.multimoney.multimoney.presentation.util.catalog.BankAccountType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class IbanAccountViewModel @Inject constructor(
    val queryValidateBankAccountUseCase: QueryValidateBankAccountUseCase
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    // ViewModel variables
    private var idBrand: Int = 0
    private var identification: String = ""
    private var email: String = ""
    var validateAccount: ValidateAccount? = null

    private fun onNextActionClick(
        user: String,
        nextStepAction: () -> Unit,
        saveCreditStepsHelper: SaveCreditStepsHelper
    ) {
        saveCreditStepsHelper.saveStepOneCR(
            user,
            uiState.accountNumber
        )
        nextStepAction()
    }

    private fun onAccountValueValueChange(
        bankAccount: String,
        onFailureWithDialog: (isLoading: Boolean, dialogParameters: DialogParameters) -> Unit
    ) {
        if (bankAccount.isDigitsOnly() && bankAccount.length <= IBAN_MAX_LENGTH) {
            uiState = if (bankAccount.length in 1..IBAN_MAX_LENGTH.minus(1)) {
                onValidForm(false)
                uiState.copy(
                    accountNumber = bankAccount,
                    accountError = Pair(true, R.string.iban_account_error)
                )
            } else {
                uiState.copy(
                    accountNumber = bankAccount,
                    accountError = Pair(false, R.string.empty),
                    validationError = null
                )
            }
            if (bankAccount.length == IBAN_MAX_LENGTH) {
                validateIbanAccount { response ->
                    onFailureWithDialog(
                        false,
                        DialogParameters(
                            description = response.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                }
            }
        }
    }

    private fun validateIbanAccount(onFailure: (HttpError) -> Unit) {
        var idBrandIban = ""
        if (idBrand == Brand.CostaRica.id) {
            idBrandIban = Brand.CostaRica.iban
        }
        executeUseCase {
            uiState = uiState.copy(accountError = Pair(false, R.string.iban_account_loading))

            queryValidateBankAccountUseCase(
                "$idBrandIban${uiState.accountNumber}",
                identification,
                BankAccountType.Credit.value,
                email,
                idBrand
            ).collectLatest {
                it.onSuccess { account ->
                    account?.let { response ->
                        when (response.responseCode) {
                            IS_VALID -> {
                                validateAccount = response
                                uiState = uiState.copy(ibanSuccess = true)
                                onValidForm(true)
                            }
                            HAS_ERRORS -> {
                                uiState =
                                    uiState.copy(
                                        accountError = Pair(true, R.string.empty),
                                        validationError = response.responseMessage.capitalized()
                                    )
                            }
                        }
                    }
                }
                it.onFailure { error ->
                    uiState = uiState.copy(
                        accountError = Pair(false, R.string.empty),
                        accountNumber = ""
                    )
                    onFailure(error)
                }
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

    private fun onValidForm(isValidAccount: Boolean) {
        emitBaseEvent(
            OnFormValidateCompleted(isValidAccount)
        )
    }

    private fun resetAccountNumber() {
        uiState = uiState.copy(
            accountNumber = "",
            accountError = Pair(false, R.string.empty),
            ibanSuccess = false
        )
        onValidForm(false)
    }

    private fun onLoadStep(
        list: List<CreditCatalog?>?,
        onFailureWithDialog: (isLoading: Boolean, dialogParameters: DialogParameters) -> Unit
    ) {
        val ibanNumber = list?.find { it?.description == SaveCreditStepsHelper.ACCOUNT_NUMBER }
        uiState = uiState.copy(accountNumber = ibanNumber?.value ?: "")
        if (uiState.accountNumber.isNotEmpty()) {
            validateIbanAccount { response ->
                onFailureWithDialog(
                    false,
                    DialogParameters(
                        description = response.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }
        }
    }

    data class UIState(
        val accountNumber: String = "",
        val accountError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val validationError: String? = null,
        val ibanSuccess: Boolean = false,
        val dialogParameters: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNextActionClick -> onNextActionClick(
                uiEvent.user,
                uiEvent.nextStepAction,
                uiEvent.saveCreditStepsHelper
            )
            is OnAccountValueChange -> onAccountValueValueChange(
                uiEvent.account,
                uiEvent.onFailureWithDialog
            )
            is OnValidForm -> onValidForm(false)
            is OnUpdateUserInfo -> onUpdateUserInfo(
                uiEvent.identification,
                uiEvent.email,
                uiEvent.idBrand
            )
            is OnOpenInformativeDialog -> uiState = uiState.copy(
                dialogParameters = DialogParameters(
                    descriptionResource = R.string.iban_dialog_message,
                    positiveAction = { resetAccountNumber() },
                    isActive = mutableStateOf(true)
                )
            )
            is OnLoadCreditSteps -> onLoadStep(uiEvent.list, uiEvent.onFailureWithDialog)
        }
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

        data class OnAccountValueChange(
            val account: String,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameters: DialogParameters) -> Unit
        ) : UIEvent()

        object OnValidForm : UIEvent()
        object OnOpenInformativeDialog : UIEvent()
        data class OnLoadCreditSteps(
            val list: List<CreditCatalog?>?,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameters: DialogParameters) -> Unit
        ) : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) :
            BaseEvent()
    }

    companion object {
        const val IS_VALID = 0
        const val HAS_ERRORS = 1
        const val IBAN_MAX_LENGTH = 20
    }
}
