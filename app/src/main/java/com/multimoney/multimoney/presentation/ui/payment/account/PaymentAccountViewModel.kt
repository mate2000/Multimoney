package com.multimoney.multimoney.presentation.ui.payment.account

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.credit.QueryGetClientBankAccountUseCase
import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountViewModel.UIEvent.OnCallQueryGetClientBankAccount
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountViewModel.UIEvent.OnClientBankAccountSelected
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountViewModel.UIEvent.OnGetTextResources
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountViewModel.UIEvent.OnSetIdCurrency
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
    private var idCurrency: Int? = null

    private fun setCurrency(idCurrency: Int) {
        this.idCurrency = idCurrency
    }

    private fun getTextResources() {
        uiState = uiState.copy(
            titleResource = Currency.Search.getAccountIconByIdCurrency(idCurrency).accountTitle
        )
    }

    private fun onCallQueryGetClientBankAccountUseCase(user: String, idBrand: Int, idClient: Int, idLoan: Int) {
        executeUseCase {
            queryGetClientBankAccountUseCase.invoke(
                user = user,
                idBrand = idBrand,
                idClient = idClient,
                idLoan = idLoan
            ).collectLatest { result ->
                result.onSuccess { clientBankAccountList ->
                    uiState = uiState.copy(isLoading = false, clientBankAccountList = clientBankAccountList)
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

    private fun onClientBankAccountSelected(accountIdCurrency: Int) {
        if (idCurrency != accountIdCurrency) {
            uiState = uiState.copy(
                openDialog = DialogParameters(
                    titleResource = R.string.payment_account_different_currency_dialog_title,
                    descriptionResource = R.string.payment_account_different_currency_dialog_description,
                    isActive = mutableStateOf(true),
                    positiveAction = {
                        navigateTo(route = Screen.PaymentAmountScreen.route)
                    }
                )
            )
        }else{

            navigateTo(route = Screen.PaymentAmountScreen.route)
        }
    }

    fun getMaskedAccount(accountNumber: String, maskedText: String) =
        accountNumber.take(ACCOUNT_FIRST_DIGITS).plus(maskedText).plus(accountNumber.takeLast(ACCOUNT_LAST_DIGITS))

    data class UIState(
        // Interactions
        val titleResource: Int = R.string.empty,
        val clientBankAccountList: List<ClientBankAccount?>? = null,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> popAndNavigateTo(
                route = Screen.PaymentFeeScreen.route,
                popTo = Screen.PaymentAccountScreen.route
            )
            is OnSetIdCurrency -> setCurrency(uiEvent.idCurrency)
            is OnGetTextResources -> getTextResources()
            is OnCallQueryGetClientBankAccount -> onCallQueryGetClientBankAccountUseCase(
                user = uiEvent.user,
                idBrand = uiEvent.idBrand,
                idClient = uiEvent.idClient,
                idLoan = uiEvent.idLoan
            )
            is OnClientBankAccountSelected -> onClientBankAccountSelected(uiEvent.accountIdCurrency)
        }
    }

    sealed class UIEvent {
        class OnSetIdCurrency(val idCurrency: Int) : UIEvent()
        class OnCallQueryGetClientBankAccount(
            val user: String,
            val idBrand: Int,
            val idClient: Int,
            val idLoan: Int
        ) : UIEvent()

        class OnClientBankAccountSelected(val accountIdCurrency: Int) : UIEvent()
        object OnGetTextResources : UIEvent()
        object OnNavigateBack : UIEvent()
    }

    companion object {
        const val ACCOUNT_FIRST_DIGITS = 2
        const val ACCOUNT_LAST_DIGITS = 4
    }
}
