package com.multimoney.multimoney.presentation.ui.smart.payment.accounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.accountsmart.QueryListSinpeAccountUseCase
import com.multimoney.domain.model.accountsmart.IbanAccountID
import com.multimoney.domain.model.accountsmart.SinpeAccount
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.metrics.BaseEventDataDto
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
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
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountViewModel.UIEvent.OnInitializeAccounts
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SmartTransferTypes
import com.multimoney.multimoney.presentation.util.toJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmartPaymentAccountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val queryListSinpeAccountUseCaseImpl: QueryListSinpeAccountUseCase,
    private val dataStorePreferences: DataStorePreferences
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
        savedStateHandle.get<Array<SinpeAccount>>(SMART_PAYMENT_ACCOUNTS)?.let { accounts ->
            uiState = uiState.copy(
                sinpeAccountList = accounts.toList()
            )
        }
    }

    private fun callQueryListSinpeAccount() =
        executeUseCase {
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
                    accountList?.data?.let { accounts ->
                        uiState = uiState.copy(
                            sinpeAccountList = accounts
                        )
                    }
                }
                result.onFailure {
                    uiState = uiState.copy(isLoading = false)
                    uiState = uiState.copy(
                        openDialog = DialogParameters(
                            description = it.getError().toString(),
                            isActive = mutableStateOf(true)
                        )
                    )
                }
                result.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        }

    private fun onAddAccountClick() {
        logEvents(AdjustEventType.SETTINGS_FIRST_ADD_ACCOUNT_8003)
        navigateTo(
            route = "${Screen.AddIbanAccountScreen.baseRoute}/$user/$idBrand/$identification/${Screen.SmartPaymentAccountScreenCR.baseRoute}/$idClient/$idLoanClient"
        )
    }

    fun logEvents(adjustEventType: AdjustEventType) {
        viewModelScope.launch {
            getAdjustEvent(adjustEventType).invoke()
        }
    }

    private fun getAdjustEvent(adjustEventType: AdjustEventType): suspend () -> Unit {
        val baseAdjustEvent = BaseEventDataDto(
            user = user,
            idBrand = idBrand,
            idClient = idClient?.toInt(),
            idLoanClient = idLoanClient?.toInt(),
            identification = identification
        )
        return when (adjustEventType) {
            AdjustEventType.SETTINGS_FIRST_ADD_ACCOUNT_8003 -> {
                getAddAccountEvent(baseAdjustEvent)
            }
            else -> suspend {}
        }
    }

    private fun getAddAccountEvent(baseAdjustEvent: BaseEventDataDto) =
        suspend {
            if (dataStorePreferences.isAdjustAddAccountEventRegister().first()) {
                registerAdjustEvent(
                    AdjustEventType.SETTINGS_FIRST_ADD_ACCOUNT_8003,
                    applyAdjust = false,
                    data = baseAdjustEvent.toJson()
                )
                dataStorePreferences.isAdjustAddAccountEventRegister(false)
            }
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
        val sinpeAccountList: List<SinpeAccount?> = listOf(),
        val openDialog: DialogParameters = DialogParameters(),
        var isLoading: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnAddAccountClick -> onAddAccountClick()
            is OnNavigateBack -> onNavigateBack()
            is OnAccountClick -> onAccountClick(uiEvent.account)
            OnInitializeAccounts -> callQueryListSinpeAccount()
        }
    }

    sealed class UIEvent {
        object OnInitializeAccounts : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnAddAccountClick : UIEvent()
        data class OnAccountClick(val account: SinpeAccount?) : UIEvent()
    }
}
