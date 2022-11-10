package com.multimoney.multimoney.presentation.ui.credit.payment.schedule.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.credit.QueryGetClientBankAccountUseCase
import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_DATE
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.account.PaymentScheduleAccountViewModel.UIEvent.OnCallQueryGetClientBankAccount
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.account.PaymentScheduleAccountViewModel.UIEvent.OnClientBankAccountSelected
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.account.PaymentScheduleAccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class PaymentScheduleAccountViewModel @Inject constructor(
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
    private var paymentDate: String? = null
    private var previousScreen = ""

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idClient = savedStateHandle[ID_CLIENT] ?: 0
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: 0
        paymentDate = savedStateHandle[PAYMENT_DATE]
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
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

    private fun onClientBankAccountSelected(clientBankAccount: ClientBankAccount?) = popAndNavigateTo(
        route = "${Screen.PaymentScheduleScreen.baseRoute}/$user/$idBrand/$idClient/$idLoanClient/${
        encodeData(
            clientBankAccount
        )
        }/$paymentDate/${true}/$previousScreen",
        popTo = Screen.PaymentScheduleAccountScreen.baseRoute
    )

    private fun onNavigateBack() = navigateBack(popTo = Screen.PaymentScheduleScreen.route, isRestart = false)

    data class UIState(
        // Interactions
        val clientBankAccountList: List<ClientBankAccount?>? = null,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnCallQueryGetClientBankAccount -> onCallQueryGetClientBankAccountUseCase()
            is OnClientBankAccountSelected -> onClientBankAccountSelected(uiEvent.clientBankAccount)
        }
    }

    sealed class UIEvent {
        object OnCallQueryGetClientBankAccount : UIEvent()
        class OnClientBankAccountSelected(val clientBankAccount: ClientBankAccount?) : UIEvent()
        object OnNavigateBack : UIEvent()
    }
}
