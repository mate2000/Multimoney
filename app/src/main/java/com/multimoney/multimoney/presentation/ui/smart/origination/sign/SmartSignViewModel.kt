package com.multimoney.multimoney.presentation.ui.smart.origination.sign

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus
import com.multimoney.data.util.catalog.SmartOnFidoOrFirmStatus
import com.multimoney.domain.interaction.accountsmart.MutationSaveAutomatedSmartAccountUseCase
import com.multimoney.domain.interaction.accountsmart.SubscriptionAccountSmartContractUseCase
import com.multimoney.domain.model.accountsmart.AccountSmartContractResult
import com.multimoney.domain.model.credit.CreditContractEvent
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.COMING_FROM_CRYPTO
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_USER_REQUEST
import com.multimoney.multimoney.presentation.navigation.navgraph.LAST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.SHOULD_GET_EVICERTIA_LINK
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_GLOBAL_ID
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_ID_PRINT
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_STEP_ARG
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_URL
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.smart.origination.SmartSubscriptionManager
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel.BaseEvent.SimulateUserInteraction
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel.UIEvent.OnChangeScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel.UIEvent.OnInitializeText
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel.UIEvent.OnNavigateToContinueValidatingIdentity
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel.UIEvent.OnShowDialogInformation
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel.UIEvent.OnStartListenerSubscriptionSmartContractEvent
import com.multimoney.multimoney.presentation.util.catalog.CreditSubscriptionStep
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.EVICERTIA_REJECTED_FIRST_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.EVICERTIA_REJECTED_SECOND_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.ONFIDO_REJECTED_FIRST_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.ONFIDO_REJECTED_SECOND_TIME
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.GENERATE_DOCUMENT_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.SIGN_DOCUMENTS_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.VALIDATE_IDENTITY
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SmartSignViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val smartSubscriptionManager: SmartSubscriptionManager
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set
    var dialogDescription = ""
    var idBrand: Int = 0
    var idPrint: Long = 0
    var pkUser: Long = 0
    var identification: String = ""
    var email: String = ""
    var idUserRequest: Long = 0
    var firstName: String = ""
    var lastName: String = ""
    var user: String = ""
    var globalId: Long? = 0
    var comingFromCrypto: Boolean = false
    var shouldGetEvicertiaLink = true

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
        comingFromCrypto = savedStateHandle[COMING_FROM_CRYPTO] ?: false
        shouldGetEvicertiaLink = savedStateHandle[SHOULD_GET_EVICERTIA_LINK] ?: true
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

    private fun onListenSmartContractEventSubscription() {
        onShouldStartSubscription()
        smartSubscriptionManager.idSubscriptionSubscribe(getSmartSubscriptionListener())
        if (smartSubscriptionManager.hasEvisertiaLink()) {
            uiState = uiState.copy(
                signDocumentProcessStep = SIGN_DOCUMENTS_STEP.value,
                signDocumentUrl = smartSubscriptionManager.getEvisertioLink() ?: ""
            )
        }
    }

    private fun getSmartSubscriptionListener() =
        object : SmartSubscriptionManager.SubscriptionEventListener {
            override fun onCapturedEvent(smartContractEvent: AccountSmartContractResult?) {
                handleSubscriptionsSteps(smartContractEvent = smartContractEvent)
            }

            override fun onSubscriptionFailToConnect(httpError: HttpError) {

            }
        }

    private fun handleSubscriptionsSteps(smartContractEvent: AccountSmartContractResult?) {
        when (smartContractEvent?.currentStep) {
            CreditSubscriptionStep.LinkGenerated.step -> {
                uiState = uiState.copy(
                    signDocumentProcessStep = SIGN_DOCUMENTS_STEP.value,
                    signDocumentUrl = smartContractEvent.link ?: ""
                )
            }
            CreditSubscriptionStep.DocumentsRejected.step -> {
                if (isEvisertiaOverCounted(smartContractEvent.statusEvicertia)) {
                    onNavigateToOnfidoAndEvicertiaError(EVICERTIA_REJECTED_SECOND_TIME.value)
                } else {
                    onNavigateToOnfidoAndEvicertiaError(EVICERTIA_REJECTED_FIRST_TIME.value)
                }
            }
        }
    }

    private fun handleEvents(smartContractEvent: CreditContractEvent?) {
        when (uiState.signDocumentProcessStep) {
            GENERATE_DOCUMENT_STEP.value -> {
                if (smartContractEvent?.link.isNullOrEmpty().not()) {
                    emitBaseEvent(SimulateUserInteraction)
                    uiState = uiState.copy(
                        signDocumentProcessStep = SIGN_DOCUMENTS_STEP.value,
                        signDocumentUrl = smartContractEvent?.link ?: ""
                    )
                }
            }
            SIGN_DOCUMENTS_STEP.value -> {
                when (smartContractEvent?.statusEvicertia?.lowercase()) {
                    SmartOnFidoOrFirmStatus.FIRMED.status.lowercase() -> {
                        emitBaseEvent(SimulateUserInteraction)
                        handleOnfidoStatus(smartContractEvent)
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
                if (smartContractEvent?.active == true) {
                    navigateToApprovedByOnfido()
                }
            }
        }
    }

    private fun navigateToApprovedByOnfido() {
        smartSubscriptionManager.destroySubscription()
        popAndNavigateTo(
            route = "${Screen.ApprovedByOnfidoScreen.baseRoute}/$pkUser/$identification/$email/$idBrand/$comingFromCrypto",
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

    private fun onShouldStartSubscription() {
        Log.v("consultas running", smartSubscriptionManager.isSubcriptionRunning.not().toString())
        if (smartSubscriptionManager.isSubcriptionRunning.not() || shouldGetEvicertiaLink) {
            smartSubscriptionManager.cancelSubscription()
            uiState = uiState.copy(isLoading = true)
            smartSubscriptionManager.startSmartSubscription(idUserRequest, idBrand)
        }
    }

    private fun onNavigateToHome() {
        smartSubscriptionManager.destroySubscription()
        /*emitBaseEvent(SimulateUserInteraction)
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true,
            homeState = HomeState.COLLAPSED
        )*/
    }

    private fun onNavigateToOnfidoAndEvicertiaError(error: String) {
        smartSubscriptionManager.destroySubscription()
        popAndNavigateTo(
            route = "${Screen.SmartOnfidoAndEvicertiaErrorsScreen.baseRoute}/$error/$idBrand/$pkUser/$identification/$email/$idUserRequest/$firstName/$lastName/$comingFromCrypto",
            popTo = Screen.SmartSignScreen.route
        )
    }

    private fun onNavigateToContinueValidatingIdentity() {
        smartSubscriptionManager.destroySubscription()
        popAndNavigateTo(
            route = Screen.SmartContinueValidatingOnfidoScreen.route,
            popTo = Screen.SmartSignScreen.route
        )
    }

/*    private fun callMutationSaveSmartAccount() {
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
    }*/

    private fun isEvisertiaOverCounted(evisertiaStatus: String?) =
        evisertiaStatus?.lowercase() == CreditOnFidoOrFirmStatus.OVER_COUNTER.status.lowercase()

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
            is OnChangeScreen -> uiState =
                uiState.copy(signDocumentProcessStep = uiEvent.signDocumentStep)
            is OnInitializeText -> dialogDescription = uiEvent.dialogDescription
            is OnCloseClick -> onNavigateToHome()
            is OnShowDialogInformation -> createDialog()
            is OnNavigateToHome -> onNavigateToHome()
            is OnNavigateToContinueValidatingIdentity -> onNavigateToContinueValidatingIdentity()
            is OnLoadingValueChange -> uiState = uiState.copy(isLoading = uiEvent.isLoading)
            is OnStartListenerSubscriptionSmartContractEvent -> onListenSmartContractEventSubscription()
        }
    }

    sealed class UIEvent {
        object OnStartListenerSubscriptionSmartContractEvent : UIEvent()
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
