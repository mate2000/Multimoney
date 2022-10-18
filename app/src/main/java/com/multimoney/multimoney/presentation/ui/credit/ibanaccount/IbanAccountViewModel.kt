package com.multimoney.multimoney.presentation.ui.credit.ibanaccount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.security.QueryValidateBankAccountUseCase
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.UIEvent.OnIncomeValueChange
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.UIEvent.OnProfessionValueChange
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.UIEvent.OnUpdateUserInfo
import com.multimoney.multimoney.presentation.ui.credit.ibanaccount.IbanAccountViewModel.UIEvent.OnValidForm
import com.multimoney.multimoney.presentation.ui.credit.util.SaveCreditStepsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
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

    init {
        viewModelScope.launch {
            query.debounce(2000).distinctUntilChanged().collectLatest {
                if (it.length in 1..19) {
                    uiState = uiState.copy(incomeError = Pair(true, R.string.iban_account_error))
                } else {
                    if (it.length == 20) {
                        var idBrandIban = ""
                        idBrand = 5
                        if (idBrand == Brand.CostaRica.id){
                            idBrandIban = Brand.CostaRica.iban
                        }
                        executeUseCase {
                            uiState = uiState.copy(incomeError = Pair(false, R.string.iban_account_loading))

                            queryValidateBankAccountUseCase(
                                "$idBrandIban${uiState.accountNumber}",
                                identification,
                                email,
                                5
                            ).collectLatest {
                                it.onSuccess {
                                    it?.let { response ->
                                        when (response.responseCode) {
                                            0 -> {
                                                isAccountValid = true
                                                onValidForm()
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
        }
    }

    // viewmodel variables
    private var idBrand: Int = 0
    private var identification: String = ""
    private var email: String = ""
    private var isAccountValid: Boolean = false
    private var errorMessage: String = ""

    private fun onIncomeValueChange(bankAccount: String) {
        if (bankAccount.isDigitsOnly() && bankAccount.length <= 20) {
            uiState = uiState.copy(accountNumber = bankAccount, incomeError = Pair(false, R.string.empty))
            query.value = bankAccount
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
            OnFormValidateCompleted(
                uiState.accountNumber.isNotEmpty() &&
                        uiState.profession.isNotEmpty() && uiState.accountNumber.toDouble() > ZERO
            )
        )
    }

    data class UIState(
        val accountNumber: String = "",
        val profession: String = "",
        val incomeError: Pair<Boolean, Int> = Pair(
            false,
            R.string.empty
        )
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnIncomeValueChange -> onIncomeValueChange(uiEvent.income)
            is OnProfessionValueChange -> onProfessionValueChange(uiEvent.profession)
            is OnValidForm -> onValidForm()
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

        data class OnIncomeValueChange(val income: String) : UIEvent()
        data class OnProfessionValueChange(val profession: String) : UIEvent()
        object OnValidForm : UIEvent()
        data class OnLoadCreditSteps(val list: List<CreditCatalog?>?) : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) :
            BaseEvent()
    }

    companion object {
        const val ZERO = 0
    }
}

