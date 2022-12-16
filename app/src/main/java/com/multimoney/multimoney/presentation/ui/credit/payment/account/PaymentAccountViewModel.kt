package com.multimoney.multimoney.presentation.ui.credit.payment.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.credit.QueryGetClientBankAccountUseCase
import com.multimoney.domain.model.balance.Summary
import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.Screen.PaymentAmountScreen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.NAME_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_DATE
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.SUMMARY_LIST
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.credit.payment.account.PaymentAccountViewModel.UIEvent.OnAddAccountClick
import com.multimoney.multimoney.presentation.ui.credit.payment.account.PaymentAccountViewModel.UIEvent.OnCallQueryGetClientBankAccount
import com.multimoney.multimoney.presentation.ui.credit.payment.account.PaymentAccountViewModel.UIEvent.OnClientBankAccountSelected
import com.multimoney.multimoney.presentation.ui.credit.payment.account.PaymentAccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.account.PaymentAccountViewModel.UIEvent.OnNavigateBackHome
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class PaymentAccountViewModel @Inject constructor(
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
    private var idCurrency: Int? = 0
    private var identification: String? = null
    private var userName: String? = null
    private var paymentDate: String? = null
    private var previousScreen = ""

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idClient = savedStateHandle[ID_CLIENT] ?: 0
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: 0
        summaryList = savedStateHandle.get<Array<Summary>>(SUMMARY_LIST)?.toList()
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        userName = savedStateHandle[NAME_CLIENT] ?: ""
        paymentDate = savedStateHandle[PAYMENT_DATE]
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
        idCurrency = if ((summaryList?.count() ?: 0) > 1) {
            CurrencyType.All.id
        } else {
            summaryList?.firstOrNull()?.idCurrency
        }
    }

    private fun getTextResources() {
        uiState = uiState.copy(
            titleResource = idCurrency?.getCurrencyFromId()?.accountTitle ?: R.string.empty
        )
    }

    private fun onCallQueryGetClientBankAccountUseCase() {
        getTextResources()
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
                        clientBankAccountList = clientBankAccountList,
                        isClientBankAccountListEmpty = clientBankAccountList.isNullOrEmpty()
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
        if (idCurrency != clientBankAccount?.idCurrency) {
            uiState = uiState.copy(
                openDialog = DialogParameters(
                    titleResource = string.payment_account_different_currency_dialog_title,
                    descriptionResource = string.payment_account_different_currency_dialog_description,
                    isActive = mutableStateOf(true),
                    positiveAction = {
                        navigateTo(
                            route = "${PaymentAmountScreen.baseRoute}/$user/$idBrand/$idClient/$idLoanClient/${
                            encodeData(
                                summaryList
                            )
                            }/${encodeData(clientBankAccount)}/$identification/$userName/$paymentDate"
                        )
                    }
                )
            )
        } else {
            navigateTo(
                route = "${Screen.PaymentAmountScreen.baseRoute}/$user/$idBrand/$idClient/$idLoanClient/${
                encodeData(
                    summaryList
                )
                }/${encodeData(clientBankAccount)}/$identification/$userName/$paymentDate"
            )
        }
    }

    private fun onNavigateBack() = when (previousScreen) {
        Screen.PaymentFeeScreen.baseRoute -> navigateBack(popTo = Screen.PaymentFeeScreen.route, isRestart = false)
        else -> onNavigateBackHome()
    }

    private fun onNavigateBackHome() = navigateBack(popTo = Screen.HomeScreen.route, isRestart = false)

    private fun onAddAccountClick() = navigateTo(
        route = "${Screen.AddIbanAccountScreen.baseRoute}/$user/$idBrand/$identification/${Screen.PaymentAccountScreen.baseRoute}/$idClient/$idLoanClient"
    )

    data class UIState(
        // Interactions
        val titleResource: Int = R.string.empty,
        val clientBankAccountList: List<ClientBankAccount?>? = null,
        val isClientBankAccountListEmpty: Boolean = true,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnAddAccountClick -> onAddAccountClick()
            is OnNavigateBack -> onNavigateBack()
            is OnNavigateBackHome -> onNavigateBackHome()
            is OnCallQueryGetClientBankAccount -> onCallQueryGetClientBankAccountUseCase()
            is OnClientBankAccountSelected -> onClientBankAccountSelected(uiEvent.clientBankAccount)
        }
    }

    sealed class UIEvent {
        object OnCallQueryGetClientBankAccount : UIEvent()

        class OnClientBankAccountSelected(val clientBankAccount: ClientBankAccount?) : UIEvent()
        object OnAddAccountClick : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnNavigateBackHome : UIEvent()
    }
}
