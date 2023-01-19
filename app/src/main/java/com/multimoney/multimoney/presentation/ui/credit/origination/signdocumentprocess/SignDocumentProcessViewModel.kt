package com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus
import com.multimoney.domain.interaction.credit.QueryGetLinkCreditContractUseCase
import com.multimoney.domain.interaction.credit.SubscriptionCreditContractEventUseCase
import com.multimoney.domain.model.credit.CreditContractEvent
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.Screen.ContinueValidatingOnfidoScreen
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_USER_REQUEST
import com.multimoney.multimoney.presentation.navigation.navgraph.LAST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_ID_PRINT
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_ORIGIN
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_STEP_ARG
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.BaseEvent.SimulateUserInteraction
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnAlertButtonClick
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnAlertCloseClick
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnCallGetLinkCreditContractEvent
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnCallSubscriptionCreditContractEvent
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnChangeScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnInitializeText
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnNavigateToContinueValidatingIdentity
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnShowDialogInformation
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.EVICERTIA_REJECTED_FIRST_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.EVICERTIA_REJECTED_SECOND_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.ONFIDO_REJECTED_FIRST_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.ONFIDO_REJECTED_SECOND_TIME
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentOrigin
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.GENERATE_DOCUMENT_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.SIGN_DOCUMENTS_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.VALIDATE_IDENTITY
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class SignDocumentProcessViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val subscriptionCreditContractEventUseCase: SubscriptionCreditContractEventUseCase,
    private val queryGetLinkCreditContractUseCase: QueryGetLinkCreditContractUseCase,
    val mmCountDownTimer: MMCountDownTimer
) : BaseViewModel(true) {
    // uiState
    var uiState by mutableStateOf(UIState())
        private set
    var dialogDescription = ""
    var idBrand: Int = 0
    var idPrint: Long = 0
    var numAttemptsToStartSubscription: Int = 0
    var pkUser: Long = 0
    var identification: String = ""
    var email: String = ""
    var idUserRequest: Long = 0
    var firstName: String = ""
    var lastName: String = ""
    var origin: String = ""

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idPrint = savedStateHandle[SIGN_DOCUMENT_ID_PRINT] ?: 0
        pkUser = savedStateHandle[PK_USER] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        email = savedStateHandle[EMAIL] ?: ""
        idUserRequest = savedStateHandle[ID_USER_REQUEST] ?: 0
        firstName = savedStateHandle[FIRST_NAME] ?: ""
        lastName = savedStateHandle[LAST_NAME] ?: ""
        origin = savedStateHandle[SIGN_DOCUMENT_ORIGIN] ?: SignDocumentOrigin.OnFidoFirstTime.value
        uiState = uiState.copy(
            signDocumentProcessStep = savedStateHandle[SIGN_DOCUMENT_STEP_ARG] ?: ""
        )
    }

    private fun createDialog() {
        uiState = uiState.copy(
            dialogParameters = DialogParameters(
                titleResource = string.sign_credit_dialog_title,
                description = dialogDescription,
                positiveResource = string.sign_credit_dialog_continue,
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onShouldCallSubscription(idPrint: Long, idBrand: Int) {
        if (idBrand != Brand.ElSalvador.id && uiState.signDocumentProcessStep != VALIDATE_IDENTITY.value && idPrint != ID_PRINT_EMPTY) {
            onListenCreditContractEventSubscription(idPrint, idBrand)
        }
    }

    private fun onShouldCallGetLinkCreditContract() {
        if (origin == SignDocumentOrigin.Product.value || origin == SignDocumentOrigin.OnFidoSecondTime.value) {
            callQueryGetLinkCreditContractUseCase()
        }
    }

    private fun onListenCreditContractEventSubscription(idPrint: Long, idBrand: Int) {
        executeUseCase {
            subscriptionCreditContractEventUseCase.invoke(idPrint, idBrand)
                .collectLatest { result ->
                    result.onSuccess {
                        handleEvents(creditContractEvent = it)
                    }.onFailure {
                        while (numAttemptsToStartSubscription < MAX_NUMBER_ATTEMPTS_TO_START_SUBSCRIPTION) {
                            onListenCreditContractEventSubscription(idPrint, idBrand)
                            numAttemptsToStartSubscription++
                        }
                    }
                }
        }
    }

    private fun callQueryGetLinkCreditContractUseCase() =
        executeUseCase {
            queryGetLinkCreditContractUseCase.invoke(
                idPrint = idPrint,
                idBrand = idBrand,
                pkUser = pkUser,
                user = email
            ).collectLatest { result ->
                result.onSuccess { linkCreditContract ->
                    if (linkCreditContract?.linkAvailable == true) {
                        uiState = uiState.copy(
                            signDocumentProcessStep = SIGN_DOCUMENTS_STEP.value,
                            signDocumentUrl = linkCreditContract.link ?: ""
                        )
                    }
                }
                result.onFailure {
                    uiState = uiState.copy(
                        dialogParameters = DialogParameters(
                            description = it.getError().toString(),
                            isActive = mutableStateOf(true)
                        )
                    )
                }
                result.onLoading {
                }
            }
        }

    private fun handleEvents(creditContractEvent: CreditContractEvent?) {
        when (uiState.signDocumentProcessStep) {
            GENERATE_DOCUMENT_STEP.value -> {
                if (creditContractEvent?.link.isNullOrEmpty().not()) {
                    emitBaseEvent(SimulateUserInteraction)
                    uiState = uiState.copy(
                        signDocumentProcessStep = SIGN_DOCUMENTS_STEP.value,
                        signDocumentUrl = creditContractEvent?.link ?: ""
                    )
                }
            }
            SIGN_DOCUMENTS_STEP.value -> {
                when (creditContractEvent?.statusEvicertia?.lowercase()) {
                    CreditOnFidoOrFirmStatus.FIRMED.status.lowercase() -> {
                        handleOnfidoStatus(creditContractEvent)
                    }
                    CreditOnFidoOrFirmStatus.REJECTED.status.lowercase() -> {
                        emitBaseEvent(SimulateUserInteraction)
                        onNavigateToOnfidoAndEvicertiaError(EVICERTIA_REJECTED_FIRST_TIME.value)
                    }
                    CreditOnFidoOrFirmStatus.OVER_COUNTER.status.lowercase() -> {
                        emitBaseEvent(SimulateUserInteraction)
                        onNavigateToOnfidoAndEvicertiaError(EVICERTIA_REJECTED_SECOND_TIME.value)
                    }
                }
            }
            VALIDATE_IDENTITY.value -> {
                emitBaseEvent(SimulateUserInteraction)
                if (creditContractEvent?.active == true) {
                    if (idBrand == Brand.CostaRica.id && idPrint != ID_PRINT_EMPTY) {
                        navigateToProcessingTransaction()
                    } else {
                        setSuccessAlertResult()
                    }
                }
            }
        }
    }

    private fun setSuccessAlertResult() {
        uiState = uiState.copy(
            isAlertResultVisible = true,
            isAlertResultSuccess = true,
            alertResultIconResource = R.drawable.ic_success_symbol,
            alertResultTitleResource = R.string.credit_request_sent_successfully,
            alertResultDescription = "",
            alertResultDescriptionResource = R.string.credit_request_info_verification_wait,
            alertResultButtonResource = R.string.understood
        )
    }

    private fun handleOnfidoStatus(
        creditContractEvent: CreditContractEvent?
    ) {
        emitBaseEvent(SimulateUserInteraction)
        when (creditContractEvent?.statusOnfido?.lowercase()) {
            CreditOnFidoOrFirmStatus.PENDING.status.lowercase() -> {
                uiState = uiState.copy(
                    signDocumentProcessStep = VALIDATE_IDENTITY.value
                )
            }
            CreditOnFidoOrFirmStatus.APPROVED.status.lowercase() -> {
                uiState = uiState.copy(
                    signDocumentProcessStep = VALIDATE_IDENTITY.value
                )
            }
            CreditOnFidoOrFirmStatus.REJECTED.status.lowercase() -> {
                onNavigateToOnfidoAndEvicertiaError(ONFIDO_REJECTED_FIRST_TIME.value)
            }
            CreditOnFidoOrFirmStatus.OVER_COUNTER.status.lowercase() -> {
                onNavigateToOnfidoAndEvicertiaError(ONFIDO_REJECTED_SECOND_TIME.value)
            }
        }
    }

    private fun navigateToProcessingTransaction() =
        popAndNavigateTo(
            route = "${Screen.ProcessingTransactionScreen.baseRoute}/$idBrand/$idPrint/$email",
            popTo = Screen.SignDocumentProcessScreen.route
        )

    private fun onNavigateToContinueValidatingIdentity() {
        popAndNavigateTo(
            route = ContinueValidatingOnfidoScreen.route,
            popTo = Screen.SignDocumentProcessScreen.route
        )
    }

    private fun onNavigateToOnfidoAndEvicertiaError(error: String) {
        popAndNavigateTo(
            route = "${Screen.OnfidoAndEvicertiaErrorsScreen.baseRoute}/$error/$idBrand/$pkUser/$identification/$email/$idUserRequest/$firstName/$lastName",
            popTo = Screen.SignDocumentProcessScreen.route
        )
    }

    private fun onNavigateToHome() {
        emitBaseEvent(SimulateUserInteraction)
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = true, homeState = HomeState.UNEXPANDED)
    }

    data class UIState(
        // Interactions
        val signDocumentProcessStep: String = GENERATE_DOCUMENT_STEP.value,
        val dialogParameters: DialogParameters = DialogParameters(),
        val signDocumentUrl: String = "",
        val loadingIcon: Int = drawable.ic_frame,
        val loadingTitle: Int = string.document_generation_title,
        val loadingSubtitle: Int = string.document_generation_subtitle,
        val isAlertResultSuccess: Boolean = true,
        val isAlertResultVisible: Boolean = false,
        val alertResultIconResource: Int = 0,
        val alertResultTitleResource: Int = R.string.empty,
        val alertResultDescription: String = "",
        val alertResultDescriptionResource: Int = R.string.empty,
        val alertResultButtonResource: Int = R.string.empty
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnCallSubscriptionCreditContractEvent -> onShouldCallSubscription(idPrint, idBrand)
            is OnCallGetLinkCreditContractEvent -> onShouldCallGetLinkCreditContract()
            is OnChangeScreen -> uiState = uiState.copy(signDocumentProcessStep = uiEvent.signDocumentStep)
            is OnInitializeText -> dialogDescription = uiEvent.dialogDescription
            is OnCloseClick -> onNavigateToHome()
            is OnShowDialogInformation -> createDialog()
            is OnNavigateToHome -> onNavigateToHome()
            is OnNavigateToContinueValidatingIdentity -> onNavigateToContinueValidatingIdentity()
            is OnAlertButtonClick -> onNavigateToHome()
            is OnAlertCloseClick -> onNavigateToHome()
        }
    }

    sealed class UIEvent {
        object OnCallSubscriptionCreditContractEvent : UIEvent()
        object OnCallGetLinkCreditContractEvent : UIEvent()
        data class OnInitializeText(val dialogDescription: String) : UIEvent()
        object OnCloseClick : UIEvent()
        object OnShowDialogInformation : UIEvent()
        data class OnChangeScreen(val signDocumentStep: String) : UIEvent()
        object OnNavigateToHome : UIEvent()
        object OnNavigateToContinueValidatingIdentity : UIEvent()
        object OnAlertButtonClick : UIEvent()
        object OnAlertCloseClick : UIEvent()
    }

    sealed class BaseEvent {
        object SimulateUserInteraction : BaseEvent()
    }

    companion object {
        const val MAX_NUMBER_ATTEMPTS_TO_START_SUBSCRIPTION = 3
        const val TIME_TO_WAIT_GENERATE_DOCUMENT_IN_MILLI_SECOND = 60000L
        const val TIME_TO_WAIT_VALIDATE_IDENTITY_IN_MILLI_SECOND = 40000L
        const val ID_PRINT_EMPTY = 0L
    }
}
