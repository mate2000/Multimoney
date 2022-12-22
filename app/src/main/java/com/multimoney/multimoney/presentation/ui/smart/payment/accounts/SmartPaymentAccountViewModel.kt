package com.multimoney.multimoney.presentation.ui.smart.payment.accounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.accountsmart.QueryListSinpeAccountUseCase
import com.multimoney.domain.model.accountsmart.SinpeAccount
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.IBAN_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SMART_IDS
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.ACCOUNT_TOKEN
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountViewModel.UIEvent.OnAccountClick
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountViewModel.UIEvent.OnAddAccountClick
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountViewModel.UIEvent.OnGetSnipeAccounts
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.Companion.ID_NOT_APPLICABLE
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.Companion.NOT_APPLICABLE
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SmartPaymentAccountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val queryListSinpeAccountUseCaseImpl: QueryListSinpeAccountUseCase,
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var smartAccount: SmartAccountID? = null
    private var user: String = ""
    private var idBrand: Int = 0
    private var identification: String? = ""
    private var idClient: String? = ""
    private var idLoanClient: String? = ""
    private var previousScreen: String? = ""

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        idClient = savedStateHandle[ID_CLIENT] ?: ""
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: ""
        smartAccount = savedStateHandle[SMART_IDS]
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
    }

    private fun callQueryBalanceUseCase() = executeUseCase {
        queryListSinpeAccountUseCaseImpl.invoke(
            user = user,
            identification = identification ?: "",
            idBrand = idBrand,
            country = smartAccount?.currencyID?.getCurrencyFromId()?.currency ?: "",
            idAccount = 0,
            accountNumber = smartAccount?.accountNumber ?: ""
        ).collectLatest { result ->
            result.onSuccess { accountList ->
                if (accountList?.data?.isEmpty() == true) {
                    navigateToAddIbanAccount()
                } else {
                    accountList?.data?.let {
                        uiState = uiState.copy(sinpeAccountList = it)
                    }
                }
            }
            result.onFailure {
                onFailure(it)
            }
            result.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun onFailure(error: HttpError) {
        uiState = uiState.copy(
            isLoading = false,
            openDialog = DialogParameters(
                description = error.getError() ?: "",
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun navigateToAddIbanAccount() {
        navigateTo(
            route = "${Screen.AddIbanAccountScreen.baseRoute}/$user/$idBrand/$identification/${Screen.PaymentAccountScreen.baseRoute}/$idClient/$idLoanClient"
        )
    }

    private fun onAddAccountClick() {
        // TODO Implement add sinpeAccount navigation
    }

    private fun onAccountClick(selectedSinpeAccount: SinpeAccount?) {
        navigateTo("${Screen.SmartPaymentSavingAmount.baseRoute}/${encodeData(smartAccount)}?$IBAN_ACCOUNT=" +
                "${encodeData(selectedSinpeAccount)}/$ID_NOT_APPLICABLE/" +
                "${Screen.SmartPaymentAccountScreenCR.baseRoute}/$NOT_APPLICABLE/$NOT_APPLICABLE"
        )
    }

    private fun onNavigateBack() {
        navigateBack(popTo = previousScreen ?: Screen.HomeScreen.baseRoute, isRestart = true)
    }

    data class UIState(
        val sinpeAccountList: List<SinpeAccount?> = listOf(),
        val openDialog: DialogParameters = DialogParameters(),
        var isLoading: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnGetSnipeAccounts -> callQueryBalanceUseCase()
            is OnAddAccountClick -> onAddAccountClick()
            is OnNavigateBack -> onNavigateBack()
            is OnAccountClick -> onAccountClick(uiEvent.account)
        }
    }

    sealed class UIEvent {
        object OnGetSnipeAccounts : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnAddAccountClick : UIEvent()
        data class OnAccountClick(val account: SinpeAccount?) : UIEvent()
    }
}
