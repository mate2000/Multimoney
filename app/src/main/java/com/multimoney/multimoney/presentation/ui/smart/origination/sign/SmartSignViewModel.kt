package com.multimoney.multimoney.presentation.ui.smart.origination.sign

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.SmartOnFidoOrFirmStatus
import com.multimoney.domain.interaction.accountsmart.SubscriptionAccountSmartContractUseCase
import com.multimoney.domain.model.credit.CreditContractEvent
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_USER_REQUEST
import com.multimoney.multimoney.presentation.navigation.navgraph.LAST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_STEP_ARG
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_URL
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel.BaseEvent.SimulateUserInteraction
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.EVICERTIA_REJECTED_FIRST_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.EVICERTIA_REJECTED_SECOND_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.ONFIDO_REJECTED_FIRST_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.ONFIDO_REJECTED_SECOND_TIME
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.GENERATE_DOCUMENT_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.SIGN_DOCUMENTS_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.VALIDATE_IDENTITY
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class SmartSignViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val subscriptionAccountSmartContractUseCase: SubscriptionAccountSmartContractUseCase
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

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        pkUser = savedStateHandle[PK_USER] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        email = savedStateHandle[EMAIL] ?: ""
        idUserRequest = savedStateHandle[ID_USER_REQUEST] ?: 0
        firstName = savedStateHandle[FIRST_NAME] ?: ""
        lastName = savedStateHandle[LAST_NAME] ?: ""
        uiState = uiState.copy(
            signDocumentProcessStep = savedStateHandle[SIGN_DOCUMENT_STEP_ARG] ?: "",
            signDocumentUrl = savedStateHandle[SIGN_DOCUMENT_URL] ?: ""
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

    private fun onShouldCallSubscription(idRequestSys: Long, idBrand: Int) {
        if (uiState.signDocumentProcessStep != VALIDATE_IDENTITY.value) {
            uiState = uiState.copy(
                loadingIcon = drawable.ic_multimoney_white_logo,
                loadingTitle = string.smart_other_generating_document_title,
                loadingSubtitle = string.smart_other_generating_document_subtitle
            )
            onListenSmartContractEventSubscription(idBrand, idRequestSys)
        }
    }

    private fun onListenSmartContractEventSubscription(idBrand: Int, idRequestSys: Long) {
        executeUseCase {
            subscriptionAccountSmartContractUseCase.invoke(idBrand, idRequestSys)
                .collectLatest { result ->
                    result.onSuccess {
                        handleEvents(
                            creditContractEvent = CreditContractEvent(
                                idPrint = it?.idRequestSysde ?: idRequestSys,
                                idBrand = it?.idBrand,
                                link = it?.link,
                                statusEvicertia = it?.statusEvicertia,
                                statusOnfido = it?.statusOnfido,
                                active = it?.active,
                                currentStep = it?.currentStep
                            )
                        )
                    }.onFailure {
                        while (numAttemptsToStartSubscription < MAX_NUMBER_ATTEMPTS_TO_START_SUBSCRIPTION) {
                            onListenSmartContractEventSubscription(idBrand, idRequestSys)
                            numAttemptsToStartSubscription++
                        }
                    }
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
                    SmartOnFidoOrFirmStatus.FIRMED.status.lowercase() -> {
                        emitBaseEvent(SimulateUserInteraction)
                        handleOnfidoStatus(creditContractEvent)
                    }
                    SmartOnFidoOrFirmStatus.REJECTED.status.lowercase() -> {
                        emitBaseEvent(SimulateUserInteraction)
                        onNavigateToOnfidoAndEvicertiaError(EVICERTIA_REJECTED_FIRST_TIME.value)
                    }
                    SmartOnFidoOrFirmStatus.OVER_COUNTER.status.lowercase() -> {
                        emitBaseEvent(SimulateUserInteraction)
                        onNavigateToOnfidoAndEvicertiaError(EVICERTIA_REJECTED_SECOND_TIME.value)
                    }
                }
            }
            VALIDATE_IDENTITY.value -> {
                emitBaseEvent(SimulateUserInteraction)
                if (creditContractEvent?.active == true) {
                    navigateToApprovedByOnfido()
                }
            }
        }
    }

    private fun navigateToApprovedByOnfido() {
        popAndNavigateTo(
            route = "${Screen.ApprovedByOnfidoScreen.baseRoute}/$pkUser/$identification/$email/$idBrand",
            popTo = Screen.SmartSignScreen.route
        )
    }

    private fun handleOnfidoStatus(
        creditContractEvent: CreditContractEvent?
    ) {
        emitBaseEvent(SimulateUserInteraction)
        when (creditContractEvent?.statusOnfido?.lowercase()) {
            SmartOnFidoOrFirmStatus.PENDING.status.lowercase() -> {
                uiState = uiState.copy(
                    signDocumentProcessStep = VALIDATE_IDENTITY.value
                )
            }
            SmartOnFidoOrFirmStatus.APPROVED.status.lowercase() -> {
                uiState = uiState.copy(
                    signDocumentProcessStep = VALIDATE_IDENTITY.value
                )
            }
            SmartOnFidoOrFirmStatus.REJECTED.status.lowercase() -> {
                onNavigateToOnfidoAndEvicertiaError(ONFIDO_REJECTED_FIRST_TIME.value)
            }
            SmartOnFidoOrFirmStatus.OVER_COUNTER.status.lowercase() -> {
                onNavigateToOnfidoAndEvicertiaError(ONFIDO_REJECTED_SECOND_TIME.value)
            }
        }
    }

    private fun onNavigateToHome() {
        emitBaseEvent(SimulateUserInteraction)
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = true, homeState = HomeState.UNEXPANDED)
    }

    private fun onNavigateToOnfidoAndEvicertiaError(error: String) {
        popAndNavigateTo(
            route = "${Screen.SmartOnfidoAndEvicertiaErrorsScreen.baseRoute}/$error/$idBrand/$pkUser/$identification/$email/$idUserRequest/$firstName/$lastName",
            popTo = Screen.SmartSignScreen.route
        )
    }

    private fun onNavigateToContinueValidatingIdentity() {
        popAndNavigateTo(
            route = Screen.SmartContinueValidatingOnfidoScreen.route,
            popTo = Screen.SmartSignScreen.route
        )
    }

    data class UIState(
        // Interactions
        val signDocumentProcessStep: String = GENERATE_DOCUMENT_STEP.value,
        val dialogParameters: DialogParameters = DialogParameters(),
        val signDocumentUrl: String = "",
        val loadingIcon: Int = drawable.ic_frame,
        val loadingTitle: Int = string.document_generation_title,
        val loadingSubtitle: Int = string.smart_other_generating_document_subtitle
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnCallSubscriptionSmartContractEvent -> onShouldCallSubscription(idPrint, idBrand)
            is UIEvent.OnChangeScreen ->
                uiState =
                    uiState.copy(signDocumentProcessStep = uiEvent.signDocumentStep)
            is UIEvent.OnInitializeText -> dialogDescription = uiEvent.dialogDescription
            is UIEvent.OnCloseClick -> onNavigateToHome()
            is UIEvent.OnShowDialogInformation -> createDialog()
            is UIEvent.OnNavigateToHome -> onNavigateToHome()
            is UIEvent.OnNavigateToContinueValidatingIdentity -> onNavigateToContinueValidatingIdentity()
        }
    }

    sealed class UIEvent {
        object OnCallSubscriptionSmartContractEvent : UIEvent()
        data class OnInitializeText(val dialogDescription: String) : UIEvent()
        object OnCloseClick : UIEvent()
        object OnShowDialogInformation : UIEvent()
        data class OnChangeScreen(val signDocumentStep: String) : UIEvent()
        object OnNavigateToHome : UIEvent()
        object OnNavigateToContinueValidatingIdentity : UIEvent()
    }

    sealed class BaseEvent {
        object SimulateUserInteraction : BaseEvent()
    }

    companion object {
        const val MAX_NUMBER_ATTEMPTS_TO_START_SUBSCRIPTION = 3
        const val TIME_TO_WAIT_GENERATE_DOCUMENT_IN_MILLI_SECOND = 30000L
        const val TIME_TO_WAIT_VALIDATE_IDENTITY_IN_MILLI_SECOND = 30000L
    }
}
