package com.multimoney.multimoney.presentation.ui.smart.payment.accounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.accountsmart.IbanAccountID
import com.multimoney.domain.model.accountsmart.SinpeAccount
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SMART_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.SMART_PAYMENT_ACCOUNTS
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountViewModel.UIEvent.OnAccountClick
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountViewModel.UIEvent.OnAddAccountClick
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SmartTransferTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SmartPaymentAccountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
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
        smartAccount = savedStateHandle[SMART_ACCOUNT]
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
        uiState = uiState.copy(
            sinpeAccountList = savedStateHandle.get<Array<SinpeAccount>>(SMART_PAYMENT_ACCOUNTS)
                ?.toList()
        )
    }

    private fun onAddAccountClick() {
        navigateTo(
            route = "${Screen.AddIbanAccountScreen.baseRoute}/$user/$idBrand/$identification/${Screen.PaymentAccountScreen.baseRoute}/$idClient/$idLoanClient"
        )
    }

    private fun onAccountClick(selectedSinpeAccount: SinpeAccount?) {
        val ibanAccount = encodeData(
            IbanAccountID(
                bank = selectedSinpeAccount?.bank,
                clientIdentification = selectedSinpeAccount?.clientIdentification,
                sinpeAccount = selectedSinpeAccount?.sinpeAccount,
                currencyId = selectedSinpeAccount?.currencyId,
                nameAccount = selectedSinpeAccount?.nameAccount
            )
        )
        navigateTo(
            "${Screen.SmartPaymentSavingAmountCR.baseRoute}/" +
                "${Screen.SmartPaymentAccountScreenCR.baseRoute}/" +
                "$ibanAccount/${encodeData(smartAccount)}/${SmartTransferTypes.IbanToSmart.id}"
        )
    }

    private fun onNavigateBack() {
        val screen = when (previousScreen) {
            Screen.SmartPaymentOptionsScreenCR.baseRoute -> Screen.SmartPaymentOptionsScreenCR.route
            else -> Screen.HomeScreen.route
        }
        navigateBack(popTo = screen, isRestart = false)
    }

    data class UIState(
        val sinpeAccountList: List<SinpeAccount>? = listOf(),
        val openDialog: DialogParameters = DialogParameters(),
        var isLoading: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnAddAccountClick -> onAddAccountClick()
            is OnNavigateBack -> onNavigateBack()
            is OnAccountClick -> onAccountClick(uiEvent.account)
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnAddAccountClick : UIEvent()
        data class OnAccountClick(val account: SinpeAccount?) : UIEvent()
    }
}
