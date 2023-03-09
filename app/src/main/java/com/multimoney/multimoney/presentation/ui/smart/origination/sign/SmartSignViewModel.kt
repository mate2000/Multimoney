package com.multimoney.multimoney.presentation.ui.smart.origination.sign

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus
import com.multimoney.data.util.catalog.SmartOnFidoOrFirmStatus
import com.multimoney.domain.model.accountsmart.AccountSmartContractResult
import com.multimoney.domain.model.util.error.HttpError
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
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_STEP_ARG
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_URL
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.BaseEvent.OpenWhatsAppLink
import com.multimoney.multimoney.presentation.ui.home.HomeState
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
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.catalog.CreditSubscriptionStep
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.EVICERTIA_REJECTED_FIRST_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.EVICERTIA_REJECTED_SECOND_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.ONFIDO_REJECTED_FIRST_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.ONFIDO_REJECTED_SECOND_TIME
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.GENERATE_DOCUMENT_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.SIGN_DOCUMENTS_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.VALIDATE_IDENTITY
import com.multimoney.multimoney.presentation.util.toJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmartSignViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val smartSubscriptionManager: SmartSubscriptionManager,
    private val dataStorePreferences: DataStorePreferences
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
                negativeResource = string.payment_points_dialog_negative_button,
                negativeAction = { onUIEvent(OnNavigateToHome) },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onListenSmartContractEventSubscription() {
        onShouldStartSubscription()
        smartSubscriptionManager.idSubscriptionSubscribe(getSmartSubscriptionListener())
        if (smartSubscriptionManager.hasEvicertiaLink()) {
            uiState = uiState.copy(
                signDocumentProcessStep = SIGN_DOCUMENTS_STEP.value,
                signDocumentUrl = smartSubscriptionManager.getEvicertiaLink() ?: ""
            )
        }
    }

    private fun getSmartSubscriptionListener() =
        object : SmartSubscriptionManager.SubscriptionEventListener {
            override fun onCapturedEvent(smartContractEvent: AccountSmartContractResult?) {
                handleSubscriptionsSteps(smartContractEvent = smartContractEvent)
            }

            override fun onSubscriptionFailToConnect(httpError: HttpError) {
                showSubscriptionError()
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
            CreditSubscriptionStep.DocumentsFirmed.step -> {
                trackAdjustOriginationEvicertiaDone(smartContractEvent)
                emitBaseEvent(SimulateUserInteraction)
                handleOnfidoStatus(smartContractEvent)
            }
            CreditSubscriptionStep.DocumentsRejected.step -> {
                emitBaseEvent(SimulateUserInteraction)
                if (isEvicertiaOverCounted(smartContractEvent.statusEvicertia)) {
                    onNavigateToOnfidoAndEvicertiaError(EVICERTIA_REJECTED_SECOND_TIME.value, smartContractEvent)
                } else {
                    onNavigateToOnfidoAndEvicertiaError(EVICERTIA_REJECTED_FIRST_TIME.value, smartContractEvent)
                }
            }
            CreditSubscriptionStep.AccountActivated.step -> {
                setSuccessAlertResult()
            }
            CreditSubscriptionStep.ErrorActivatingAccount.step, CreditSubscriptionStep.DocumentsFailed.step -> {
                showSubscriptionError()
            }
        }
    }

    private fun showSubscriptionError() {
        uiState = uiState.copy(
            isAlertResultVisible = true,
            alertResultIsRightButtonVisible = true,
            alertResultIconResource = drawable.ic_error_symbol,
            alertResultTitleResource = string.rejected_by_onfido_title,
            alertResultDescriptionResource = string.smart_rejected_by_onfido_subtitle,
            alertResultButtonResource = string.contact,
            alertResultRightButtonClick = { onUIEvent(OnNavigateToHome) },
            alertResultButtonAction = {
                emitBaseEvent(OpenWhatsAppLink)
                onUIEvent(OnNavigateToHome)
            }
        )
    }

    private fun setSuccessAlertResult() {
        uiState = uiState.copy(
            isAlertResultVisible = true,
            alertResultIsRightButtonVisible = true,
            alertResultIconResource = drawable.ic_success_symbol,
            alertResultTitleResource = string.approved_by_onfido_title,
            alertResultButtonResource = string.understood,
            alertResultRightButtonClick = { onUIEvent(OnNavigateToHome) },
            alertResultButtonAction = { onUIEvent(OnNavigateToHome) }
        )
    }

    private fun navigateToApprovedByOnfido() {
        smartSubscriptionManager.destroySubscription()
        popAndNavigateTo(
            route = "${Screen.ApprovedByOnfidoScreen.baseRoute}/$pkUser/$identification/$email/$idBrand/$comingFromCrypto",
            popTo = Screen.SmartSignScreen.route
        )
    }

    private fun handleOnfidoStatus(
        smartContractEvent: AccountSmartContractResult?
    ) {
        emitBaseEvent(SimulateUserInteraction)
        when (smartContractEvent?.statusOnfido?.lowercase()) {
            SmartOnFidoOrFirmStatus.PENDING.status.lowercase() -> {
                trackAdjustOriginationPending(smartContractEvent)
                uiState = uiState.copy(
                    signDocumentProcessStep = VALIDATE_IDENTITY.value
                )
            }
            SmartOnFidoOrFirmStatus.APPROVED.status.lowercase() -> {
                trackAdjustOriginationApproved(smartContractEvent)
                uiState = uiState.copy(
                    signDocumentProcessStep = VALIDATE_IDENTITY.value
                )
            }
            SmartOnFidoOrFirmStatus.REJECTED.status.lowercase() -> {
                onNavigateToOnfidoAndEvicertiaError(ONFIDO_REJECTED_FIRST_TIME.value, smartContractEvent)
            }
            SmartOnFidoOrFirmStatus.OVER_COUNTER.status.lowercase() -> {
                onNavigateToOnfidoAndEvicertiaError(ONFIDO_REJECTED_SECOND_TIME.value, smartContractEvent)
            }
        }
    }

    private fun onShouldStartSubscription() {
        if (smartSubscriptionManager.isSubcriptionRunning.not() || shouldGetEvicertiaLink) {
            smartSubscriptionManager.cancelSubscription()
            uiState = uiState.copy(isLoading = true)
            smartSubscriptionManager.startSmartSubscription(idUserRequest, idBrand)
        }
    }

    private fun onNavigateToHome() {
        smartSubscriptionManager.destroySubscription()
        emitBaseEvent(SimulateUserInteraction)
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true,
            homeState = HomeState.COLLAPSED
        )
    }

    private fun onNavigateToOnfidoAndEvicertiaError(error: String, smartContractEvent: AccountSmartContractResult?) {
        trackAdjustOriginationRejected(smartContractEvent)
        smartSubscriptionManager.destroySubscription()
        popAndNavigateTo(
            route = "${Screen.SmartOnfidoAndEvicertiaErrorsScreen.baseRoute}/$error/$idBrand/$pkUser/$identification/$email/$idUserRequest/$firstName/$lastName/$comingFromCrypto/$user/$globalId",
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

    private fun isEvicertiaOverCounted(evicertiaStatus: String?) =
        evicertiaStatus?.lowercase() == CreditOnFidoOrFirmStatus.OVER_COUNTER.status.lowercase()

    private fun trackAdjustOriginationEvicertiaDone(smartContractEvent: AccountSmartContractResult?) {
        val parameters = buildParamsListFromCreditContractEvent(smartContractEvent)

        val data = smartContractEvent?.toJson()

        viewModelScope.launch {
            if (dataStorePreferences.isAdjustSmartFirstTimeEvicertiaDone().first()) {
                dataStorePreferences.setAdjustSmartFirstTimeEvicertiaDone(false)
                registerAdjustEvent(
                    AdjustEventType.ORIGINATION_SMART_FIRST_TIME_EVICERTIA_DONE,
                    listParameters = parameters,
                    data = data ?: ""
                )
            }
        }
    }

    private fun trackAdjustOriginationRejected(smartContractEvent: AccountSmartContractResult?) {
        val parameters = buildParamsListFromCreditContractEvent(smartContractEvent)

        val data = smartContractEvent?.toJson()

        viewModelScope.launch {
            if (dataStorePreferences.isAdjustSmartFirstTimeRejected().first()) {
                dataStorePreferences.setAdjustSmartFirstTimeRejected(false)
                registerAdjustEvent(
                    AdjustEventType.ORIGINATION_SMART_FIRST_TIME_REJECTED,
                    listParameters = parameters,
                    data = data ?: ""
                )
            }
        }
    }

    private fun trackAdjustOriginationPending(smartContractEvent: AccountSmartContractResult?) {
        val parameters = buildParamsListFromCreditContractEvent(smartContractEvent)

        val data = smartContractEvent?.toJson() ?: ""

        registerAdjustEvent(
            AdjustEventType.ORIGINATION_SMART_WAITING,
            applyAdjust = false,
            listParameters = parameters,
            data = data
        )
    }

    private fun trackAdjustOriginationApproved(smartContractEvent: AccountSmartContractResult?) {
        val parameters = buildParamsListFromCreditContractEvent(smartContractEvent)

        val data = smartContractEvent?.toJson() ?: ""

        viewModelScope.launch {
            if (dataStorePreferences.isAdjustSmartFirstTimeSuccessful().first()) {
                dataStorePreferences.setAdjustSmartFirstTimeSuccessful(false)
                registerAdjustEvent(
                    AdjustEventType.ORIGINATION_SMART_FIRST_TIME_SUCCESSFUL,
                    listParameters = parameters,
                    data = data
                )
                resetOriginationSmartPreferences()
            }
        }
    }

    private fun buildParamsListFromCreditContractEvent(smartContractEvent: AccountSmartContractResult?) : List<Pair<String, String>> {
        return buildList<Pair<String, String>> {
            add(ID_PRINT to smartContractEvent?.idBrand.toString())
            add(LINK to (smartContractEvent?.link ?: ""))
            add(STATUS_EVICERTIA to (smartContractEvent?.statusEvicertia ?: ""))
            add(STATUS_ONFIDO to (smartContractEvent?.statusOnfido ?: ""))
            add(ACTIVE to smartContractEvent?.active.toString())
            add(CURRENT_STEP to (smartContractEvent?.currentStep ?: ""))
        }
    }

    private suspend fun resetOriginationSmartPreferences() {
        dataStorePreferences.apply {
            setAdjustSmartFirstTime(true)
            setAdjustSmartFirstTimePersonal(true)
            setAdjustSmartFirstTimeHome(true)
            setAdjustSmartFirstTimeIncome(true)
            setAdjustSmartFirstTimeIncomeInformation(true)
            setAdjustSmartFirstTimeBeneficiary(true)
            setAdjustSmartFirstTimePep(true)
            setAdjustSmartFirstTimeOnfidoStart(true)
            setAdjustSmartFirstTimeOnfidoDocument(true)
            setAdjustSmartFirstTimeOnfidoSelfie(true)
            setAdjustSmartFirstTimeOnfidoDone(true)
            setAdjustSmartFirstTimeEvicertiaDone(true)
            setAdjustSmartFirstTimeRejected(true)
            setAdjustSmartFirstTimeSuccessful(true)
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
        val loadingSubtitle: Int = string.smart_other_generating_document_subtitle,
        val isAlertResultVisible: Boolean = false,
        val alertResultIconResource: Int = 0,
        val alertResultIsLeftButtonVisible: Boolean = false,
        val alertResultIsRightButtonVisible: Boolean = false,
        val alertResultTitleResource: Int = string.empty,
        val alertResultDescriptionResource: Int = string.empty,
        val alertResultButtonResource: Int = string.empty,
        val alertResultRightButtonClick: () -> Unit = {},
        val alertResultButtonAction: () -> Unit = {}
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnChangeScreen -> uiState = uiState.copy(
                signDocumentProcessStep = uiEvent.signDocumentStep
            )
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

        private const val ID_PRINT = "idPrint"
        private const val LINK = "link"
        private const val STATUS_EVICERTIA = "statusEvicertia"
        private const val STATUS_ONFIDO = "statusOnfido"
        private const val ACTIVE = "active"
        private const val CURRENT_STEP = "currentStep"
    }
}
