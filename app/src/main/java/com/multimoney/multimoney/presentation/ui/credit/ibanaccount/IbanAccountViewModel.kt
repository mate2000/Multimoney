package com.multimoney.multimoney.presentation.ui.credit.ibanaccount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.security.QueryValidateBankAccountUseCase
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.UIEvent.OnAccountValueChange
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.UIEvent.OnUpdateUserInfo
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.UIEvent.OnValidForm
import com.multimoney.multimoney.presentation.ui.credit.util.SaveCreditStepsHelper
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.multimoney.multimoney.presentation.util.capitalized
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@HiltViewModel
class IbanAccountViewModel @Inject constructor(
    val queryValidateBankAccountUseCase: QueryValidateBankAccountUseCase
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    private val query: MutableStateFlow<String> = MutableStateFlow("")

    // ViewModel variables
    private var idBrand: Int = 0
    private var identification: String = ""
    private var email: String = ""

    init {
        viewModelScope.launch {
            query.debounce(2000).distinctUntilChanged().collectLatest {
                if (it.length in 1..IBAN_MAX_LENGTH.minus(1)) {
                    onValidForm(false)
                    uiState = uiState.copy(accountError = Pair(true, R.string.iban_account_error))
                }
            }
        }
    }

    private fun onIncomeValueChange(
        bankAccount: String,
        onFailureWithDialog: (isLoading: Boolean, dialogParameters: DialogParameters) -> Unit
    ) {
        if (bankAccount.isDigitsOnly() && bankAccount.length <= IBAN_MAX_LENGTH) {
            uiState = uiState.copy(
                accountNumber = bankAccount,
                accountError = Pair(false, R.string.empty),
                validationError = null
            )
            query.value = bankAccount
            if (bankAccount.length == IBAN_MAX_LENGTH) {
                validateIbanAccount(bankAccount) { response ->
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

    private fun validateIbanAccount(bankAccount: String, onFailure: (HttpError) -> Unit) {
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
                        accountNumber = "",
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

    data class UIState(
        val accountNumber: String = "",
        val accountError: Pair<Boolean, Int> = Pair(
            false,
            R.string.empty
        ),
        val validationError: String? = null
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
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
        data class OnLoadCreditSteps(val list: List<CreditCatalog?>?) : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) :
            BaseEvent()
    }

    companion object {
        const val ZERO = 0
        const val IS_VALID = 0
        const val HAS_ERRORS = 1
        const val IBAN_MAX_LENGTH = 20
    }
}

