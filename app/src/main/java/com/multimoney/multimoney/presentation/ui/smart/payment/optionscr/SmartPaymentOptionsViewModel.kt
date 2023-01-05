package com.multimoney.multimoney.presentation.ui.smart.payment.optionscr

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
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SMART_IDS_LIST
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.smart.payment.optionscr.SmartPaymentOptionsViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.optionscr.SmartPaymentOptionsViewModel.UIEvent.OnSmartAccountSelected
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SmartPaymentOptionsViewModel @Inject constructor(
    private val queryListSinpeAccountUseCaseImpl: QueryListSinpeAccountUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: Int = 0
    private var idClient: String = ""
    private var idLoanClient: String = ""
    private var identification: String? = ""
    private var smartAccountIDs: List<SmartAccountID>? = listOf()
    private var selectedSmartAccount: SmartAccountID? = null

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idClient = savedStateHandle[ID_CLIENT] ?: ""
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        smartAccountIDs = savedStateHandle.get<Array<SmartAccountID>>(SMART_IDS_LIST)?.toList()
    }

    private fun callQueryBalanceUseCase() = executeUseCase {
        queryListSinpeAccountUseCaseImpl.invoke(
            user = user,
            identification = identification ?: "",
            idBrand = idBrand,
            country = "",
            idAccount = 0,
            accountNumber = ""
        ).collectLatest { result ->
            result.onSuccess { accountList ->
                uiState = uiState.copy(isLoading = false)
                if (accountList?.data?.isEmpty() == true) {
                    navigateToAddIbanAccount()
                } else {
                    accountList?.data?.let {
                        navigateToSmartAccount(it)
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

    private fun onNavigateBack() {
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = false
        )
    }

    private fun navigateToAddIbanAccount() {
        navigateTo(
            route = "${Screen.AddIbanAccountScreen.baseRoute}/$user/$idBrand/$identification/${Screen.PaymentAccountScreen.baseRoute}/$idClient/$idLoanClient"
        )
    }

    private fun navigateToSmartAccount(clientBankAccounts: List<SinpeAccount?>) {
        navigateTo(
            route = "${Screen.SmartPaymentAccountScreenCR.baseRoute}/$user/$idBrand/$identification/${Screen.SmartPaymentOptionsScreenCR.baseRoute}/" +
                "$idClient/$idLoanClient/${encodeData(clientBankAccounts)}/${
                encodeData(
                    selectedSmartAccount
                )
                }"
        )
    }

    private fun onSelectSmartAccount(currencyType: CurrencyType) {
        selectedSmartAccount = when (currencyType) {
            CurrencyType.Colon -> smartAccountIDs?.find { it.currencyID == CurrencyType.Colon.id }
            else -> smartAccountIDs?.find { it.currencyID == CurrencyType.Dollar.id }
        }
        callQueryBalanceUseCase()
    }

    data class UIState(
        val openDialog: DialogParameters = DialogParameters(),
        var isLoading: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnSmartAccountSelected -> onSelectSmartAccount(uiEvent.currencyType)
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        data class OnSmartAccountSelected(var currencyType: CurrencyType) : UIEvent()
    }
}
