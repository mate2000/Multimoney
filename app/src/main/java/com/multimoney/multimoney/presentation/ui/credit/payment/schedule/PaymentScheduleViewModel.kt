package com.multimoney.multimoney.presentation.ui.credit.payment.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.credit.MutationActivateClientAutomaticDebitUseCase
import com.multimoney.domain.interaction.credit.QueryGetClientAutomaticDebitUseCase
import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.CLIENT_BANK_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.IS_EDIT
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_DATE
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.PaymentScheduleViewModel.UIEvent.OnEditBankAccount
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.PaymentScheduleViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.PaymentScheduleViewModel.UIEvent.OnOpenDisclaimerDialog
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.PaymentScheduleViewModel.UIEvent.OnProgramClick
import com.multimoney.multimoney.presentation.util.API_DATE_FORMAT
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getDayFromString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class PaymentScheduleViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    private val mutationActivateClientAutomaticDebitUseCase: MutationActivateClientAutomaticDebitUseCase,
    private val getClientAutomaticDebitUseCase: QueryGetClientAutomaticDebitUseCase
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: Int = 0
    private var idClient: Int = 0
    private var idLoanClient: Int = 0
    private var isEdit: Boolean = false
    private var paymentDate: String? = null
    private var previousScreen = ""

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idClient = savedStateHandle[ID_CLIENT] ?: 0
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: 0
        paymentDate = savedStateHandle[PAYMENT_DATE]
        isEdit = savedStateHandle[IS_EDIT] ?: false
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
        getClientBankAccount()
    }

    private fun getClientBankAccount() = when {
        isEdit || previousScreen == Screen.HomeBNScreen.baseRoute || previousScreen == Screen.PaymentVoucherScreen.baseRoute ->
            uiState =
                uiState.copy(
                    clientBankAccount = savedStateHandle[CLIENT_BANK_ACCOUNT],
                    day = getDayFromString(paymentDate, API_DATE_FORMAT)
                )
        else -> onCallGetClientAutomaticDebitUseCase()
    }

    private fun onCallGetClientAutomaticDebitUseCase() = executeUseCase {
        getClientAutomaticDebitUseCase.invoke(
            user = user,
            idBrand = idBrand,
            idClient = idClient,
            idLoanClient = idLoanClient
        ).collectLatest { result ->
            result.onSuccess {
                uiState = uiState.copy(
                    clientBankAccount = it?.first(),
                    isLoading = false
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

    private fun onCallMutationActivateClientAutomaticDebitUseCase() = executeUseCase {
        mutationActivateClientAutomaticDebitUseCase.invoke(
            user = user,
            idBrand = idBrand,
            idClient = idClient.toLong(),
            idLoanClient = idLoanClient.toLong(),
            origin = uiState.clientBankAccount?.origin ?: "",
            idAccount = uiState.clientBankAccount?.id?.toLong() ?: 0,
            idCurrency = uiState.clientBankAccount?.idCurrency ?: 0
        ).collectLatest { result ->
            result.onSuccess {
                uiState = uiState.copy(
                    isLoading = false
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

    private fun onEditBankAccount() = navigateTo(
        route = "${Screen.PaymentScheduleAccountScreen.baseRoute}/$user/$idBrand/$idClient/$idLoanClient/$paymentDate/$previousScreen"
    )

    private fun onOpenDisclaimerDialog() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                descriptionResource = R.string.payment_schedule_info_dialog_description,
                positiveResource = R.string.understood,
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onNavigateBack() = when (previousScreen) {
        Screen.HomeBNScreen.baseRoute -> onNavigateBackHome(false)
        Screen.PaymentVoucherScreen.baseRoute -> navigateBack(
            popTo = Screen.PaymentVoucherScreen.route,
            isRestart = false
        )
        else -> onNavigateBackHome(true)
    }

    private fun onNavigateBackHome(isRestart: Boolean) =
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = isRestart)

    data class UIState(
        // Interactions
        val clientBankAccount: ClientBankAccount? = null,
        val day: String = "",
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnProgramClick -> onCallMutationActivateClientAutomaticDebitUseCase()
            is OnEditBankAccount -> onEditBankAccount()
            is OnOpenDisclaimerDialog -> onOpenDisclaimerDialog()
            is OnNavigateBack -> onNavigateBack()
        }
    }

    sealed class UIEvent {
        object OnProgramClick : UIEvent()
        object OnEditBankAccount : UIEvent()
        object OnOpenDisclaimerDialog : UIEvent()
        object OnNavigateBack : UIEvent()
    }
}
