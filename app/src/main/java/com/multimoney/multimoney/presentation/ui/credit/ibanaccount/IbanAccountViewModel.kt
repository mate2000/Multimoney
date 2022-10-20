package com.multimoney.multimoney.presentation.ui.credit.ibanaccount

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
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.UIEvent.OnAccountValueChange
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.UIEvent.OnLoadCreditSteps
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.UIEvent.OnResetAccountNumber
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.UIEvent.OnUpdateUserInfo
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.UIEvent.OnValidForm
import com.multimoney.multimoney.presentation.ui.credit.util.SaveCreditStepsHelper
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.multimoney.multimoney.presentation.util.capitalized
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@OptIn(FlowPreview::class)
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

    private fun onIncomeValueChange(
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
                validateIbanAccount() { response ->
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
                email,
                idBrand
            ).collectLatest {
                it.onSuccess { account ->
                    account?.let { response ->
                        when (response.responseCode) {
                            IS_VALID -> {
                                validateAccount = response
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

    private fun onResetAccountNumber() {
        uiState = uiState.copy(accountNumber = "", ibanSuccess = false)
    }

    private fun onLoadStep() {
    }

    data class UIState(
        val accountNumber: String = "",
        val accountError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val validationError: String? = null,
        val ibanSuccess: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNextActionClick -> onNextActionClick(
                uiEvent.user,
                uiEvent.nextStepAction,
                uiEvent.saveCreditStepsHelper
            )
            is OnAccountValueChange -> onIncomeValueChange(
                uiEvent.account,
                uiEvent.onFailureWithDialog
            )
            is OnValidForm -> onValidForm(false)
            is OnUpdateUserInfo -> onUpdateUserInfo(
                uiEvent.identification,
                uiEvent.email,
                uiEvent.idBrand
            )
            is OnResetAccountNumber -> onResetAccountNumber()
            is OnLoadCreditSteps -> onLoadStep()
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
        object OnResetAccountNumber : UIEvent()
        data class OnLoadCreditSteps(val list: List<CreditCatalog?>?) : UIEvent()
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
