package com.multimoney.multimoney.presentation.ui.smart.origination.sign

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.SmartOnFidoOrFirmStatus
import com.multimoney.domain.interaction.accountsmart.MutationSaveAutomatedSmartAccountUseCase
import com.multimoney.domain.interaction.accountsmart.SubscriptionAccountSmartContractUseCase
import com.multimoney.domain.model.credit.CreditContractEvent
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
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
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_GLOBAL_ID
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_STEP_ARG
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_URL
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.ui.smart.origination.onfido.SmartOnfidoViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel.BaseEvent.SimulateUserInteraction
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.EVICERTIA_REJECTED_FIRST_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.EVICERTIA_REJECTED_SECOND_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.ONFIDO_REJECTED_FIRST_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.ONFIDO_REJECTED_SECOND_TIME
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.GENERATE_DOCUMENT_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.SIGN_DOCUMENTS_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.VALIDATE_IDENTITY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SmartSignViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val subscriptionAccountSmartContractUseCase: SubscriptionAccountSmartContractUseCase,
    private val mutationSaveSmartAccount: MutationSaveAutomatedSmartAccountUseCase
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
    var user: String = ""
    var globalId: Long? = 0

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        pkUser = savedStateHandle[PK_USER] ?: 0
        user = savedStateHandle[USER] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        email = savedStateHandle[EMAIL] ?: ""
        idUserRequest = savedStateHandle[ID_USER_REQUEST] ?: 0
        firstName = savedStateHandle[FIRST_NAME] ?: ""
        lastName = savedStateHandle[LAST_NAME] ?: ""
        uiState = uiState.copy(
            signDocumentProcessStep = savedStateHandle[SIGN_DOCUMENT_STEP_ARG] ?: "",
            signDocumentUrl = savedStateHandle[SIGN_DOCUMENT_URL] ?: ""
        )
        globalId = savedStateHandle[SIGN_DOCUMENT_GLOBAL_ID] ?: 0
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
        if (uiState.signDocumentProcessStep != VALIDATE_IDENTITY.value) onListenSmartContractEventSubscription(
            idBrand,
            idRequestSys
        )
    }

    private fun onListenSmartContractEventSubscription(idBrand: Int, idRequestSys: Long) {
        executeUseCase {
            subscriptionAccountSmartContractUseCase.invoke(idBrand, idRequestSys)
                .collectLatest { result ->
                    result.onSuccess {
                        uiState = uiState.copy(isLoading = false)
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
                    }.onLoading {
                        uiState = uiState.copy(isLoading = true)
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
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = true, homeState = HomeState.COLLAPSED)
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

    private fun callMutationSaveSmartAccount() {
        executeUseCase {
            mutationSaveSmartAccount.invoke(
                user = user,
                idBrand = idBrand,
                identificationNumber = identification,
                idRequest = globalId ?: 0
            ).collectLatest { result ->
                result.onSuccess {
                    onShouldCallSubscription(
                        it?.idAccount ?: 0L,
                        idBrand
                    )
                }
            }
        }
    }

    data class UIState(
        // Interactions
        val isLoading: Boolean = false,
        val signDocumentProcessStep: String = GENERATE_DOCUMENT_STEP.value,
        val dialogParameters: DialogParameters = DialogParameters(),
        val signDocumentUrl: String = "",
        val loadingIcon: Int = drawable.ic_logo_multimoney3,
        val loadingTitle: Int = string.document_generation_title,
        val loadingSubtitle: Int = string.smart_other_generating_document_subtitle
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnCallSubscriptionSmartContractEvent -> callMutationSaveSmartAccount()
            is UIEvent.OnChangeScreen -> uiState =
                uiState.copy(signDocumentProcessStep = uiEvent.signDocumentStep)
            is UIEvent.OnInitializeText -> dialogDescription = uiEvent.dialogDescription
            is UIEvent.OnCloseClick -> onNavigateToHome()
            is UIEvent.OnShowDialogInformation -> createDialog()
            is UIEvent.OnNavigateToHome -> onNavigateToHome()
            is UIEvent.OnNavigateToContinueValidatingIdentity -> onNavigateToContinueValidatingIdentity()
            is UIEvent.OnLoadingValueChange -> uiState = uiState.copy(isLoading = uiEvent.isLoading)
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
        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()
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
