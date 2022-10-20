package com.multimoney.multimoney.presentation.ui.payment.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.credit.QueryGetClientBankAccountUseCase
import com.multimoney.domain.model.balance.Summary
import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountViewModel.UIEvent.OnCallQueryGetClientBankAccount
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountViewModel.UIEvent.OnClientBankAccountSelected
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountViewModel.UIEvent.OnGetTextResources
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountViewModel.UIEvent.OnSaveArguments
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.Currency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class PaymentAccountViewModel @Inject constructor(
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

    private fun onSaveArguments(
        user: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int,
        summaryList: List<Summary>
    ) {
        this.user = user
        this.idBrand = idBrand
        this.idClient = idClient
        this.idLoanClient = idLoanClient
        this.summaryList = summaryList
        idCurrency = if (summaryList.count() > 1) {
            Currency.All.id
        } else if (summaryList.isNotEmpty() && summaryList.firstOrNull() != null) {
            summaryList.first()?.idCurrency
        } else {
            idCurrency
        }
    }

    private fun getTextResources() {
        uiState = uiState.copy(
            titleResource = Currency.Search.getCurrencyByIdCurrency(idCurrency).accountTitle
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
                    titleResource = R.string.payment_account_different_currency_dialog_title,
                    descriptionResource = R.string.payment_account_different_currency_dialog_description,
                    isActive = mutableStateOf(true),
                    positiveAction = {
                        navigateTo(
                            route = "${Screen.PaymentAmountScreen.baseRoute}/$user/$idBrand/$idClient/$idLoanClient/${
                            encodeData(
                                summaryList
                            )
                            }/${encodeData(clientBankAccount)}"
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
                }/${encodeData(clientBankAccount)}"
            )
        }
    }

    private fun onNavigateBack() {
        val route = if ((summaryList?.count() ?: 0) > 1) {
            "${Screen.PaymentFeeScreen.baseRoute}/$user/$idBrand/$idClient/$idLoanClient/${encodeData(summaryList)}"
        } else {
            Screen.HomeScreen.route
        }
        popAndNavigateTo(route = route, popTo = Screen.PaymentAccountScreen.route)
    }

    fun getMaskedAccount(accountNumber: String, maskedText: String) =
        accountNumber.take(ACCOUNT_FIRST_DIGITS).plus(maskedText).plus(accountNumber.takeLast(ACCOUNT_LAST_DIGITS))

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
            is OnNavigateBack -> onNavigateBack()
            is OnSaveArguments -> onSaveArguments(
                user = uiEvent.user,
                idBrand = uiEvent.idBrand,
                idClient = uiEvent.idClient,
                idLoanClient = uiEvent.idLoanClient,
                summaryList = uiEvent.summaryList
            )
            is OnGetTextResources -> getTextResources()
            is OnCallQueryGetClientBankAccount -> onCallQueryGetClientBankAccountUseCase()
            is OnClientBankAccountSelected -> onClientBankAccountSelected(uiEvent.clientBankAccount)
        }
    }

    sealed class UIEvent {
        class OnSaveArguments(
            val user: String,
            val idBrand: Int,
            val idClient: Int,
            val idLoanClient: Int,
            val summaryList: List<Summary>
        ) : UIEvent()

        object OnCallQueryGetClientBankAccount : UIEvent()

        class OnClientBankAccountSelected(val clientBankAccount: ClientBankAccount?) : UIEvent()
        object OnGetTextResources : UIEvent()
        object OnNavigateBack : UIEvent()
    }

    companion object {
        const val ACCOUNT_FIRST_DIGITS = 2
        const val ACCOUNT_LAST_DIGITS = 4
    }
}
