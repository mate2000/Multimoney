package com.multimoney.multimoney.presentation.ui.credit.disbursement.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.credit.QueryGetClientBankAccountUseCase
import com.multimoney.domain.model.balance.Summary
import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.NEXT_PAYMENT_DATE
import com.multimoney.multimoney.presentation.navigation.navgraph.QUOTA_TOTAL
import com.multimoney.multimoney.presentation.navigation.navgraph.SELECTED_AMOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.SUMMARY_LIST
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.credit.disbursement.account.DisbursementAccountViewModel.UIEvent.OnCallQueryGetClientBankAccount
import com.multimoney.multimoney.presentation.ui.credit.disbursement.account.DisbursementAccountViewModel.UIEvent.OnClientBankAccountSelected
import com.multimoney.multimoney.presentation.ui.credit.disbursement.account.DisbursementAccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.disbursement.account.DisbursementAccountViewModel.UIEvent.OnNavigateBackHome
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class DisbursementAccountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val queryGetClientBankAccountUseCase: QueryGetClientBankAccountUseCase
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: Int = 0
    private var idClient: Int = 0
    private var idLoanClient: Int = 0
    private var summaryList: List<Summary>? = null
    private var nextPaymentDate: String? = null
    private var quotaTotal: String? = null
    private var selectedAmount: String? = null

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idClient = savedStateHandle[ID_CLIENT] ?: 0
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: 0
        summaryList = savedStateHandle.get<Array<Summary>>(SUMMARY_LIST)?.toList()
        nextPaymentDate = savedStateHandle[NEXT_PAYMENT_DATE]
        quotaTotal = savedStateHandle[QUOTA_TOTAL]
        selectedAmount = savedStateHandle[SELECTED_AMOUNT]
        getTextResources()
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

    private fun onCallQueryGetClientBankAccountUseCase() {
        executeUseCase {
            queryGetClientBankAccountUseCase.invoke(
                user = user,
                idBrand = idBrand,
                idClient = idClient,
                idLoan = idLoanClient
            ).collectLatest { result ->
                result.onSuccess { clientBankAccountList ->
                    uiState = uiState.copy(
                        isLoading = false,
                        clientBankAccountList = clientBankAccountList
                    )
                }.onFailure {
                    uiState = uiState.copy(
                        isLoading = false,
                        openDialog = DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                }.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        }
    }

    private fun onClientBankAccountSelected(clientBankAccount: ClientBankAccount?) {
        // TODO: Display bottom sheet
    }

    private fun onNavigateBack() = navigateBack(popTo = Screen.DisbursementAmountScreen.route, isRestart = false)

    private fun onNavigateBackHome() = navigateBack(popTo = Screen.HomeScreen.route, isRestart = false)

    data class UIState(
        // Interactions
        val titleResource: Int = R.string.empty,
        val clientBankAccountList: List<ClientBankAccount?>? = null,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnNavigateBackHome -> onNavigateBackHome()
            is OnCallQueryGetClientBankAccount -> onCallQueryGetClientBankAccountUseCase()
            is OnClientBankAccountSelected -> onClientBankAccountSelected(uiEvent.clientBankAccount)
        }
    }

    sealed class UIEvent {
        object OnCallQueryGetClientBankAccount : UIEvent()
        class OnClientBankAccountSelected(val clientBankAccount: ClientBankAccount?) : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnNavigateBackHome : UIEvent()
    }
}
