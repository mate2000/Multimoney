package com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.credit.QueryGetCardAutomaticDebitUseCase
import com.multimoney.domain.interaction.virtualcard.MutationActivatedCardAutomaticDebitUseCase
import com.multimoney.domain.interaction.virtualcard.QueryListCardVDUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.CLIENT_CARD_VISA_DIRECT
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.IS_EDIT_BANK_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.IS_EDIT_PAYMENT_SCHEDULE
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_DATE
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnAlertButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnAlertCloseClick
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnEditCardVisaDirect
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnGetClientCardVisaDirect
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnNavigateToAddCard
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnOpenDisclaimerDialog
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel.UIEvent.OnProgramClick
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.util.API_DATE_FORMAT
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getDayFromString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class PaymentScheduleCardViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    private val queryListCardVDUseCase: QueryListCardVDUseCase,
    private val mutationActivatedCardAutomaticDebitUseCase: MutationActivatedCardAutomaticDebitUseCase,
    private val getCardAutomaticDebitUseCase: QueryGetCardAutomaticDebitUseCase
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var identification: String = ""
    private var idBrand: Int = 0
    private var idClient: Int = 0
    private var idLoanClient: Int = 0
    private var isEditBankAccount: Boolean = false
    private var isEditPaymentSchedule: Boolean = false
    private var paymentDate: String? = null
    private var previousScreen = ""
    private var getCardAttempts = 0
    private var getPaymentScheduleAttempts = 0
    private var setPaymentScheduleAttempts = 0

    init {
        user = savedStateHandle[USER] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idClient = savedStateHandle[ID_CLIENT] ?: 0
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: 0
        paymentDate = savedStateHandle[PAYMENT_DATE]
        isEditBankAccount = savedStateHandle[IS_EDIT_BANK_ACCOUNT] ?: false
        isEditPaymentSchedule = savedStateHandle[IS_EDIT_PAYMENT_SCHEDULE] ?: false
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
        uiState = uiState.copy(day = getDayFromString(paymentDate, API_DATE_FORMAT))
    }

    private fun getClientCardVisaDirect() = when {
        isEditBankAccount || previousScreen == Screen.PaymentCardVoucherScreen.baseRoute ->
            uiState =
                uiState.copy(
                    cardVisaDirect = savedStateHandle[CLIENT_CARD_VISA_DIRECT]
                )
        previousScreen == Screen.HomeScreen.route && isEditPaymentSchedule.not() -> onCallQueryGetCardsUseCase()
        else -> onCallGetCardsAutomaticDebitUseCase()
    }

    private fun onCallQueryGetCardsUseCase() = executeUseCase {
        queryListCardVDUseCase.invoke(
            user = user,
            idBrand = idBrand,
            identification = identification
        ).collectLatest { result ->
            result.onSuccess { cardsList ->
                uiState = uiState.copy(
                    isLoading = false,
                    cardVisaDirect = cardsList?.first(),
                    isCardListEmpty = cardsList.isNullOrEmpty()
                )
            }.onFailure {
                setErrorAlertResult(attempts = getCardAttempts)
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun onCallGetCardsAutomaticDebitUseCase() = executeUseCase {
        getCardAutomaticDebitUseCase.invoke(
            user = user,
            identification = identification,
            idBrand = idBrand,
            idClient = idClient.toLong(),
            idLoanClient = idLoanClient.toLong()
        ).collectLatest { result ->
            getPaymentScheduleAttempts++
            result.onSuccess { cardsList ->
                uiState = uiState.copy(
                    cardVisaDirect = cardsList?.first(),
                    isLoading = false,
                    isCardListEmpty = cardsList.isNullOrEmpty()
                )
            }.onFailure {
                setErrorAlertResult(attempts = getPaymentScheduleAttempts)
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun onCallMutationActivatedCardAutomaticDebitUseCase() = executeUseCase {
        mutationActivatedCardAutomaticDebitUseCase.invoke(
            user = user,
            idBrand = idBrand,
            idClient = idClient,
            idLoanClient = idLoanClient,
            idCard = uiState.cardVisaDirect?.idCard?.toLong() ?: 0,
            cardMasked = uiState.cardVisaDirect?.cardMaskedNumber.orEmpty()
        ).collectLatest { result ->
            setPaymentScheduleAttempts++
            result.onSuccess {
                if (it?.isUpdated == true) {
                    setSuccessAlertResult()
                } else {
                    setErrorAlertResult(attempts = setPaymentScheduleAttempts)
                }
            }.onMessage {
                setErrorAlertResult(attempts = setPaymentScheduleAttempts)
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

    private fun onEditCardVisaDirect() = navigateTo(
        route = "${Screen.PaymentScheduleCardListScreen.baseRoute}/$user/$idBrand/$idClient/$idLoanClient/$paymentDate/$previousScreen/$identification"
    )

    private fun onOpenDisclaimerDialog() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                descriptionResource = R.string.payment_schedule_card_info_dialog_description,
                positiveResource = R.string.understood,
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onNavigateBack() = when (previousScreen) {
        Screen.HomeScreen.route -> onNavigateBackHome(false)
        Screen.PaymentCardVoucherScreen.baseRoute -> navigateBack(
            popTo = Screen.PaymentCardVoucherScreen.route,
            isRestart = false
        )
        else -> onNavigateBackHome(true)
    }

    private fun onNavigateToAddCard() {
        // TODO - navigate to add card screen
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

    private fun onCloseClick() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = R.string.payment_schedule_card_list_dialog_title,
                descriptionResource = R.string.payment_schedule_card_list_dialog_description,
                positiveResource = R.string.accept,
                negativeResource = R.string.cancel,
                positiveAction = {
                    navigateBack(
                        popTo = Screen.HomeScreen.route,
                        isRestart = false
                    )
                },
                isActive = mutableStateOf(true)
            )
        )
    }

    data class UIState(
        // Interactions
        val cardVisaDirect: CardVisaDirect? = null,
        val day: String = "",
        val isAlertResultSuccess: Boolean = true,
        val isAlertResultVisible: Boolean = false,
        val alertResultIconResource: Int = 0,
        val alertResultTitleResource: Int = R.string.empty,
        val alertResultDescription: String = "",
        val alertResultDescriptionResource: Int = R.string.empty,
        val alertResultButtonResource: Int = R.string.empty,
        val isLoading: Boolean = false,
        val isCardListEmpty: Boolean = true,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnGetClientCardVisaDirect -> getClientCardVisaDirect()
            is OnAlertButtonClick -> onAlertButtonClick()
            is OnAlertCloseClick -> onAlertCloseClick()
            is OnCloseClick -> onCloseClick()
            is OnProgramClick -> onCallMutationActivatedCardAutomaticDebitUseCase()
            is OnEditCardVisaDirect -> onEditCardVisaDirect()
            is OnOpenDisclaimerDialog -> onOpenDisclaimerDialog()
            is OnNavigateBack -> onNavigateBack()
            is OnNavigateToAddCard -> onNavigateToAddCard()
        }
    }

    sealed class UIEvent {
        object OnGetClientCardVisaDirect : UIEvent()
        object OnAlertButtonClick : UIEvent()
        object OnAlertCloseClick : UIEvent()
        object OnCloseClick : UIEvent()
        object OnProgramClick : UIEvent()
        object OnEditCardVisaDirect : UIEvent()
        object OnOpenDisclaimerDialog : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnNavigateToAddCard : UIEvent()
    }

    companion object {
        const val ATTEMPT_ONE = 1
    }
}
