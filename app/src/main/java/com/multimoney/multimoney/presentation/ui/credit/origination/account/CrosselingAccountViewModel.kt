package com.multimoney.multimoney.presentation.ui.credit.origination.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.accountsmart.QueryListSinpeAccountUseCase
import com.multimoney.domain.model.accountsmart.SinpeAccount
import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.credit.origination.account.CrosselingAccountViewModel.UIEvent.*
import com.multimoney.multimoney.presentation.util.*
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class CrosselingAccountViewModel @Inject constructor(
    private val queryListSinpeAccountUseCase: QueryListSinpeAccountUseCase
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var idBrand: Int = 0
    private var identification: String = ""
    private var email: String = ""

    private fun onCallQueryListSinpeAccount() = executeUseCase {
        queryListSinpeAccountUseCase.invoke(
            user = email,
            identification = identification ?: "",
            idBrand = idBrand.toInt(),
            country = "",
            idAccount = 0,
            accountNumber = ""
        ).collectLatest { result ->
            result.onSuccess { accountList ->
                uiState = uiState.copy(isLoading = false)
                if (accountList?.data?.isEmpty() == true) {
                    //navigateToAddIbanAccount()
                } else {
                    accountList?.data?.let {
                        uiState = uiState.copy(clientBankAccountList = it)
                    }
                }
            }
            result.onFailure {
                uiState = uiState.copy(isLoading = false)
                uiState = uiState.copy(
                    openDialog = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }
            result.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun getTextResources() {
        uiState = uiState.copy(
            titleResource = when (idBrand) {
                Brand.ElSalvador.id -> R.string.disbursement_account_sv_title
                Brand.Guatemala.id -> R.string.disbursement_account_gt_title
                else -> R.string.disbursement_account_cr_title
            }
        )
    }

    private fun onClientBankAccountSelected(clientBankAccount: ClientBankAccount?) {
        uiState = uiState.copy(clientBankAccountSelected = clientBankAccount)
    }

    private fun onLoadingValueChange(loading: Boolean) {
        uiState = uiState.copy(isLoading = loading)
    }

    private fun onNavigateToDisbursementAddAccount() {

    }

    private fun onMaxAccountNumberDialog() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = R.string.disbursement_account_max_number_title,
                descriptionResource = R.string.disbursement_account_max_number_description,
                positiveResource = R.string.understood,
                isActive = mutableStateOf(true)
            )
        )
    }

    data class UIState(
        // Interactions
        val titleResource: Int = R.string.empty,
        val clientBankAccountList: List<SinpeAccount?>? = null,
        val clientBankAccountSelected: ClientBankAccount? = null,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnCallQueryGetSinpeAccount -> onCallQueryListSinpeAccount()
            is OnClientBankAccountSelected -> onClientBankAccountSelected(uiEvent.clientBankAccount)
            is OnNavigateToDisbursementAddAccount -> onNavigateToDisbursementAddAccount()
            is OnLoadingValueChange -> onLoadingValueChange(uiEvent.isLoading)
            is OnDisclaimerClick -> onMaxAccountNumberDialog()
        }
    }

    sealed class UIEvent {
        object OnCallQueryGetSinpeAccount : UIEvent()
        class OnClientBankAccountSelected(val clientBankAccount: ClientBankAccount?) : UIEvent()
        object OnNavigateToDisbursementAddAccount : UIEvent()
        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()
        object OnDisclaimerClick : UIEvent()
    }

    companion object {
        private const val ID_LOAN_FORM_HARDCODED =
            4 // TODO Change to 1-4 depending on preferences user previously selected (new HU)
        private const val LOAN_FORM_HARDCODED =
            "Transferencia" // TODO Change to Transferencia-PEX depending on preferences user previously selected (new HU)
        const val MAX_ACCOUNT_NUMBER = 3
        private const val DISBURSEMENT_PROCESS =
            "DESEMBOLSO"
    }


}