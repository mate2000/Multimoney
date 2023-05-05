package com.multimoney.multimoney.presentation.ui.credit.payment.schedule.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.credit.QueryGetClientBankAccountUseCase
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.credit.ClientBankAccount
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
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_DATE
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.account.PaymentScheduleAccountViewModel.UIEvent.OnAddAccountClick
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.account.PaymentScheduleAccountViewModel.UIEvent.OnCallQueryGetClientBankAccount
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.account.PaymentScheduleAccountViewModel.UIEvent.OnClientBankAccountSelected
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.account.PaymentScheduleAccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getNavParam
import com.multimoney.multimoney.presentation.util.toJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentScheduleAccountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val queryGetClientBankAccountUseCase: QueryGetClientBankAccountUseCase,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: Int = 0
    private var idClient: Int = 0
    private var idLoanClient: Int = 0
    private var paymentDate: String? = null
    private var identification: String? = null
    private var previousScreen = ""

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idClient = savedStateHandle[ID_CLIENT] ?: 0
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: 0
        paymentDate = savedStateHandle[PAYMENT_DATE]
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
    }

    private fun onCallQueryGetClientBankAccountUseCase() {
        executeUseCase {
            queryGetClientBankAccountUseCase.invoke(
                user = user,
                idBrand = idBrand,
                idClient = idClient,
                idLoan = idLoanClient,
                process = ""
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

    private fun getAdjustEvent(adjustEventType: AdjustEventType): suspend () -> Unit {
        val baseAdjustEvent = BaseEventDataDto(
            user = user,
            idBrand = idBrand,
            idClient = idClient,
            idLoanClient = idLoanClient,
            identification = identification
        )
        return when (adjustEventType) {
            AdjustEventType.SETTINGS_FIRST_ADD_ACCOUNT_8003 -> {
                getAddAccountEvent(baseAdjustEvent)
            }
            else -> suspend {}
        }
    }

    fun logEvents(adjustEventType: AdjustEventType) {
        viewModelScope.launch {
            getAdjustEvent(adjustEventType).invoke()
        }
    }

    private fun onClientBankAccountSelected(clientBankAccount: ClientBankAccount?) = popAndNavigateTo(
        route = "${Screen.PaymentScheduleScreen.baseRoute}/$user/$idBrand/$idClient/$idLoanClient/${
        encodeData(
            clientBankAccount
        )
        }/$paymentDate/${true}/$previousScreen/${false}/$identification",
        popTo = Screen.PaymentScheduleAccountScreen.baseRoute
    )

    private fun onNavigateBack() = navigateBack(popTo = Screen.PaymentScheduleScreen.route, isRestart = false)

    private fun onCloseClick() =
        navigateBack(popTo = Screen.PaymentScheduleScreen.route, isRestart = false, homeState = HomeState.COLLAPSED)

    private fun onAddAccountClick() {
        logEvents(AdjustEventType.SETTINGS_FIRST_ADD_ACCOUNT_8003)
        navigateTo(
            route = "${Screen.AddIbanAccountScreen.baseRoute}/$user/$idBrand/$identification/${Screen.PaymentScheduleAccountScreen.baseRoute}/$idClient/$idLoanClient".plus(
                getNavParam(SMART_ACCOUNT, encodeData(SmartAccountID()))
            )
        )
    }

    data class UIState(
        // Interactions
        val clientBankAccountList: List<ClientBankAccount?>? = null,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is UIEvent.OnCloseClick -> onCloseClick()
            is OnCallQueryGetClientBankAccount -> onCallQueryGetClientBankAccountUseCase()
            is OnClientBankAccountSelected -> onClientBankAccountSelected(uiEvent.clientBankAccount)
            is OnAddAccountClick -> onAddAccountClick()
        }
    }

    sealed class UIEvent {
        object OnCallQueryGetClientBankAccount : UIEvent()
        class OnClientBankAccountSelected(val clientBankAccount: ClientBankAccount?) : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnCloseClick : UIEvent()
        object OnAddAccountClick : UIEvent()
    }
}
