package com.multimoney.multimoney.presentation.ui.credit.payment.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.credit.MutationActivateClientAutomaticDebitUseCase
import com.multimoney.domain.interaction.credit.QueryGetClientAutomaticDebitUseCase
import com.multimoney.domain.interaction.credit.QueryGetClientBankAccountUseCase
import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.CLIENT_BANK_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.IS_EDIT_BANK_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.IS_EDIT_PAYMENT_SCHEDULE
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_DATE
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.PaymentScheduleViewModel.UIEvent.OnAlertButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.PaymentScheduleViewModel.UIEvent.OnAlertCloseClick
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.PaymentScheduleViewModel.UIEvent.OnEditBankAccount
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.PaymentScheduleViewModel.UIEvent.OnGetClientBankAccount
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.PaymentScheduleViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.PaymentScheduleViewModel.UIEvent.OnOpenDisclaimerDialog
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.PaymentScheduleViewModel.UIEvent.OnProgramClick
import com.multimoney.multimoney.presentation.ui.home.HomeState
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
    private val getClientAutomaticDebitUseCase: QueryGetClientAutomaticDebitUseCase,
    private val queryGetClientBankAccountUseCase: QueryGetClientBankAccountUseCase
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: Int = 0
    private var idClient: Int = 0
    private var idLoanClient: Int = 0
    private var isEditBankAccount: Boolean = false
    private var isEditPaymentSchedule: Boolean = false
    private var paymentDate: String? = null
    private var previousScreen = ""
    private var getBankAccountAttempts = 0
    private var getPaymentScheduleAttempts = 0
    private var setPaymentScheduleAttempts = 0

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idClient = savedStateHandle[ID_CLIENT] ?: 0
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: 0
        paymentDate = savedStateHandle[PAYMENT_DATE]
        isEditBankAccount = savedStateHandle[IS_EDIT_BANK_ACCOUNT] ?: false
        isEditPaymentSchedule = savedStateHandle[IS_EDIT_PAYMENT_SCHEDULE] ?: false
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
        uiState = uiState.copy(day = getDayFromString(paymentDate, API_DATE_FORMAT))
    }

    private fun getClientBankAccount() = when {
        isEditBankAccount || previousScreen == Screen.PaymentVoucherScreen.baseRoute ->
            uiState =
                uiState.copy(
                    clientBankAccount = savedStateHandle[CLIENT_BANK_ACCOUNT]
                )
        previousScreen == Screen.HomeScreen.route && isEditPaymentSchedule.not() -> onCallQueryGetClientBankAccountUseCase()
        else -> onCallGetClientAutomaticDebitUseCase() // This is when user clicks Edit from Home
    }

    private fun onCallQueryGetClientBankAccountUseCase() = executeUseCase {
        queryGetClientBankAccountUseCase.invoke(
            user = user,
            idBrand = idBrand,
            idClient = idClient,
            idLoan = idLoanClient,
            process = ""
        ).collectLatest { result ->
            getBankAccountAttempts++
            result.onSuccess { clientBankAccountList ->
                uiState = uiState.copy(
                    clientBankAccount = clientBankAccountList?.first(),
                    isLoading = false
                )
            }.onFailure {
                setErrorAlertResult(attempts = getBankAccountAttempts)
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun onCallGetClientAutomaticDebitUseCase() = executeUseCase {
        getClientAutomaticDebitUseCase.invoke(
            user = user,
            idBrand = idBrand,
            idClient = idClient,
            idLoanClient = idLoanClient
        ).collectLatest { result ->
            getPaymentScheduleAttempts++
            result.onSuccess {
                uiState = uiState.copy(
                    clientBankAccount = it?.first(),
                    isLoading = false
                )
            }.onFailure {
                setErrorAlertResult(attempts = getPaymentScheduleAttempts)
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
            setPaymentScheduleAttempts++
            result.onSuccess {
                if (it?.isUpdated == true) {
                    setSuccessAlertResult()
                } else {
                    setErrorAlertResult(attempts = setPaymentScheduleAttempts)
                }
            }.onMessage {
                setErrorAlertResult(
                    alertResultDescription = it?.messageError?.message ?: "",
                    attempts = setPaymentScheduleAttempts
                )
            }.onFailure {
                setErrorAlertResult(attempts = setPaymentScheduleAttempts)
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun setSuccessAlertResult() {
        uiState = uiState.copy(
            isAlertResultVisible = true,
            isAlertResultSuccess = true,
            alertResultIconResource = R.drawable.ic_success_symbol,
            alertResultTitleResource = R.string.payment_schedule_success_alert_title,
            alertResultDescription = "",
            alertResultDescriptionResource = R.string.payment_schedule_success_alert_subtitle,
            alertResultButtonResource = R.string.payment_schedule_success_alert_button,
            isLoading = false
        )
    }

    private fun setErrorAlertResult(
        alertResultDescription: String = "",
        attempts: Int
    ) {
        uiState = uiState.copy(
            isAlertResultVisible = true,
            isAlertResultSuccess = false,
            alertResultIconResource = R.drawable.ic_error_symbol,
            alertResultTitleResource = R.string.payment_schedule_error_alert_title,
            alertResultDescription = alertResultDescription,
            alertResultDescriptionResource = if (attempts == ATTEMPT_ONE && alertResultDescription.isEmpty()) {
                R.string.payment_schedule_error_alert_subtitle_one
            } else if (alertResultDescription.isEmpty()) {
                R.string.payment_schedule_error_alert_subtitle_two
            } else {
                R.string.empty
            },
            alertResultButtonResource = if (attempts == ATTEMPT_ONE) {
                R.string.payment_schedule_error_alert_button_one
            } else {
                R.string.payment_schedule_error_alert_button_two
            },
            isLoading = false
        )
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

    private fun onAlertButtonClick() = when {
        uiState.isAlertResultSuccess -> navigateBack(popTo = Screen.HomeScreen.route, isRestart = true)
        uiState.isAlertResultSuccess.not() && (getPaymentScheduleAttempts == ATTEMPT_ONE || setPaymentScheduleAttempts == ATTEMPT_ONE) ->
            uiState =
                uiState.copy(isAlertResultVisible = false)
        else -> navigateBack(popTo = Screen.HomeScreen.route, isRestart = false)
    }

    private fun onAlertCloseClick() = if (uiState.isAlertResultSuccess) {
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = true, homeState = HomeState.COLLAPSED)
    } else {
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = false)
    }

    data class UIState(
        // Interactions
        val clientBankAccount: ClientBankAccount? = null,
        val day: String = "",
        val isAlertResultSuccess: Boolean = true,
        val isAlertResultVisible: Boolean = false,
        val alertResultIconResource: Int = 0,
        val alertResultTitleResource: Int = R.string.empty,
        val alertResultDescription: String = "",
        val alertResultDescriptionResource: Int = R.string.empty,
        val alertResultButtonResource: Int = R.string.empty,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnGetClientBankAccount -> getClientBankAccount()
            is OnAlertButtonClick -> onAlertButtonClick()
            is OnAlertCloseClick -> onAlertCloseClick()
            is OnProgramClick -> onCallMutationActivateClientAutomaticDebitUseCase()
            is OnEditBankAccount -> onEditBankAccount()
            is OnOpenDisclaimerDialog -> onOpenDisclaimerDialog()
            is OnNavigateBack -> onNavigateBack()
        }
    }

    sealed class UIEvent {
        object OnGetClientBankAccount : UIEvent()
        object OnAlertButtonClick : UIEvent()
        object OnAlertCloseClick : UIEvent()
        object OnProgramClick : UIEvent()
        object OnEditBankAccount : UIEvent()
        object OnOpenDisclaimerDialog : UIEvent()
        object OnNavigateBack : UIEvent()
    }

    companion object {
        const val ATTEMPT_ONE = 1
    }
}
