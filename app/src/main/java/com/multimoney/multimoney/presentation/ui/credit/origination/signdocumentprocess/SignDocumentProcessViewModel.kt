package com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus
import com.multimoney.domain.interaction.credit.QueryGetLinkCreditContractUseCase
import com.multimoney.domain.model.credit.CreditContractEvent
import com.multimoney.domain.model.metrics.OriginationEventDataDto
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CROSSELING
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.Screen.ContinueValidatingOnfidoScreen
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.EVICERTIA_STATUS
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_USER_REQUEST
import com.multimoney.multimoney.presentation.navigation.navgraph.LAST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.SHOULD_GET_EVICERTIA_LINK
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_ID_PRINT
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_STEP_ARG
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.BaseEvent.OpenWhatsAppLink
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnCallGetLinkCreditContractEvent
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnCallGetLinkCreditContractSecondTime
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnChangeScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnNavigateToContinueValidatingIdentity
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnShowDialogInformation
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnStartListenerSubscriptionCreditContractEvent
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.catalog.CreditSubscriptionStep
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.EVICERTIA_REJECTED_FIRST_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.EVICERTIA_REJECTED_SECOND_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.ONFIDO_REJECTED_FIRST_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.ONFIDO_REJECTED_SECOND_TIME
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.GENERATE_DOCUMENT_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.PROCESSING_TRANSACTION
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.SIGN_DOCUMENTS_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.VALIDATE_IDENTITY
import com.multimoney.multimoney.presentation.util.toJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignDocumentProcessViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val creditSubscriptionManager: CreditSubscriptionManager,
    private val queryGetLinkCreditContractUseCase: QueryGetLinkCreditContractUseCase,
    val mmCountDownTimer: MMCountDownTimer,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(true) {
    // uiState
    var uiState by mutableStateOf(UIState())
        private set
    var idBrand: Int = 0
    var idPrint: Long = 0
    var pkUser: Long = 0
    var identification: String = ""
    var email: String = ""
    var idUserRequest: Long = 0
    var firstName: String = ""
    var lastName: String = ""
    var isCrosseling: Boolean = false
    var shouldGetEvicertiaLink = true
    var evisertiaStatus: String = ""

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idPrint = savedStateHandle[SIGN_DOCUMENT_ID_PRINT] ?: 0
        pkUser = savedStateHandle[PK_USER] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        email = savedStateHandle[EMAIL] ?: ""
        idUserRequest = savedStateHandle[ID_USER_REQUEST] ?: 0
        firstName = savedStateHandle[FIRST_NAME] ?: ""
        lastName = savedStateHandle[LAST_NAME] ?: ""
        isCrosseling = savedStateHandle[CROSSELING] ?: false
        shouldGetEvicertiaLink = savedStateHandle[SHOULD_GET_EVICERTIA_LINK] ?: true
        evisertiaStatus = savedStateHandle[EVICERTIA_STATUS] ?: ""
        uiState = uiState.copy(
            signDocumentProcessStep = savedStateHandle[SIGN_DOCUMENT_STEP_ARG] ?: ""
        )
    }

    private fun createDialog() {
        uiState = uiState.copy(
            dialogParameters = DialogParameters(
                titleResource = if (idBrand == Brand.CostaRica.id) string.sign_credit_dialog_title_cr else string.sign_credit_dialog_title,
                descriptionResource = if (idBrand == Brand.CostaRica.id) string.sign_credit_dialog_description_cr else string.sign_credit_dialog_description,
                positiveResource = string.sign_credit_dialog_continue,
                negativeResource = string.payment_points_dialog_negative_button,
                negativeAction = { onUIEvent(OnNavigateToHome) },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onEvaluateWitchRequestCall() {
        if (idBrand != Brand.ElSalvador.id) {
            onShouldStartSubscription()
            onShouldCallGetLinkCreditContract()
        }
    }

    private fun onShouldCallGetLinkCreditContract() {
        if (evisertiaStatus.lowercase() != CreditOnFidoOrFirmStatus.FIRMED.status.lowercase() &&
            evisertiaStatus.lowercase() != CreditOnFidoOrFirmStatus.OVER_COUNTER.status.lowercase()
        ) {
            if (shouldGetEvicertiaLink || (creditSubscriptionManager.hasEvisertiaLink().not() && isCrosseling.not())) {
                callQueryGetLinkCreditContractUseCase()
            }
        }
    }

    private fun onShouldStartSubscription() {
        if (creditSubscriptionManager.isSubcriptionRunning.not() || shouldGetEvicertiaLink) {
            creditSubscriptionManager.cancelSubscription()
            creditSubscriptionManager.startCreditSubscription(idBrand, idPrint)
        }
    }

    private fun onListenCreditContractEventSubscription() {
        if (idBrand != Brand.ElSalvador.id) {
            creditSubscriptionManager.subscriptionSubscribe(getCreditSubscriptionListener())
            if (creditSubscriptionManager.hasEvisertiaLink()) {
                uiState = uiState.copy(
                    signDocumentProcessStep = SIGN_DOCUMENTS_STEP.value,
                    signDocumentUrl = creditSubscriptionManager.getEvisertioLink() ?: ""
                )
            }
        }
    }

    private fun getCreditSubscriptionListener() = object : CreditSubscriptionManager.SubscriptionEventListener {
        override fun onCapturedEvent(creditContractEvent: CreditContractEvent?) {
            handleSubscriptionsSteps(creditContractEvent = creditContractEvent)
            Timber.wtf("$LOG_SUBSCRIPTION_TAG: ${creditContractEvent?.currentStep}")
        }

        override fun onSubscriptionFailToConnect(httpError: HttpError) {
            showSubscriptionError()
        }
    }

    private fun callQueryGetLinkCreditContractUseCase(isSecondTime: Boolean = false) =
        executeUseCase {
            queryGetLinkCreditContractUseCase.invoke(
                idPrint = idPrint,
                idBrand = idBrand,
                pkUser = pkUser,
                user = email
            ).collectLatest { result ->
                result.onSuccess { linkCreditContract ->
                    when {
                        linkCreditContract?.linkAvailable == true -> {
                            uiState = uiState.copy(
                                signDocumentProcessStep = SIGN_DOCUMENTS_STEP.value,
                                signDocumentUrl = linkCreditContract.link ?: ""
                            )
                        }
                        isEvicertiaOverCounted(linkCreditContract?.statusEvicertia) -> {
                            onNavigateToOnfidoAndEvicertiaError(EVICERTIA_REJECTED_SECOND_TIME.value)
                        }
                        else -> {
                            if (isSecondTime) {
                                onUIEvent(OnNavigateToHome)
                            }
                        }
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

    private fun handleSubscriptionsSteps(creditContractEvent: CreditContractEvent?) {
        when (creditContractEvent?.currentStep) {
            CreditSubscriptionStep.LinkGenerated.step -> {
                uiState = uiState.copy(
                    signDocumentProcessStep = SIGN_DOCUMENTS_STEP.value,
                    signDocumentUrl = creditContractEvent.link ?: ""
                )
            }
            CreditSubscriptionStep.DocumentsFirmed.step -> {
                if (isCrosseling) {
                    logEvents(AdjustEventType.CROSSELLING_FIRST_FINNISH_EVICERTIA_5030)
                    uiState = uiState.copy(
                        signDocumentProcessStep = PROCESSING_TRANSACTION.value
                    )
                } else {
                    logEvents(AdjustEventType.ORIGINATION_FIRST_SIGN_CONTRACT_EVICERTIA_5014)
                    handleOnfidoStatus(creditContractEvent)
                }
            }
            CreditSubscriptionStep.DocumentsRejected.step -> {
                if (isEvicertiaOverCounted(creditContractEvent.statusEvicertia)) {
                    onNavigateToOnfidoAndEvicertiaError(EVICERTIA_REJECTED_SECOND_TIME.value)
                } else {
                    logEvents(AdjustEventType.ORIGINATION_FIRST_CUSTOMER_REJECTED_5017)
                    onNavigateToOnfidoAndEvicertiaError(EVICERTIA_REJECTED_FIRST_TIME.value)
                }
            }
            CreditSubscriptionStep.AccountActivated.step -> {
                logEvents(
                    if (isCrosseling) {
                        AdjustEventType.ORIGINATION_FIRST_SUCCESS_EVICERTIA_5018
                    } else {
                        AdjustEventType.CROSSELLING_FIRST_CUSTOMER_COMPLETE_REQUEST_5031
                    }
                )
                if (idBrand == Brand.CostaRica.id && idPrint != ID_PRINT_EMPTY) {
                    navigateToProcessingTransaction()
                } else {
                    setSuccessAlertResult()
                }
            }
            CreditSubscriptionStep.ErrorActivatingAccount.step, CreditSubscriptionStep.DocumentsFailed.step -> {
                showSubscriptionError()
            }
        }
    }

    private suspend fun restartMetricsPreferences() {
        dataStorePreferences.isAdjustFirstOriginationFirstScreenEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationCrosselingFirstScreenEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationCheckTermsEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationCrosselingCheckTermsEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationConfirmAmountEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationCrosselingConfirmAmountEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationFillAccountEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationCrosselingFillAccountEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationMonthlyIncomeEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationJobInformationEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationCrosselingJobInformationEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationJobAddressEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationOwnAddressEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationPEPEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationOnfidoStartsEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationOnfidoFinishEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationEvicertiaSignDocumentEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationCrosselingEvicertiaSignDocumentEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationEvicertiaCustomerRejectedEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationEvicertiaSuccessEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationCrosselingEvicertiaSuccessEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationNonPreApprovedInfoExtraEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationNonPreApprovedRejectedEventRegister(true)
        dataStorePreferences.isAdjustFirstOriginationNonPreApprovedApprovedEventRegister(true)
    }

    private fun isEvicertiaOverCounted(evicertiaStatus: String?) =
        evicertiaStatus?.lowercase() == CreditOnFidoOrFirmStatus.OVER_COUNTER.status.lowercase()

    fun showSubscriptionError() {
        uiState = uiState.copy(
            isAlertResultVisible = true,
            alertResultIsRightButtonVisible = true,
            alertResultIconResource = drawable.ic_error_symbol,
            alertResultTitleResource = string.error_occurred_title,
            alertResultDescriptionResource = string.document_generation_error_description,
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
            alertResultTitleResource = string.credit_request_sent_successfully,
            alertResultDescriptionResource = string.credit_request_info_verification_wait,
            alertResultButtonResource = string.understood,
            alertResultRightButtonClick = { onUIEvent(OnNavigateToHome) },
            alertResultButtonAction = { onUIEvent(OnNavigateToHome) }
        )
    }

    private fun handleOnfidoStatus(
        creditContractEvent: CreditContractEvent?
    ) {
        when (creditContractEvent?.statusOnfido?.lowercase()) {
            CreditOnFidoOrFirmStatus.PENDING.status.lowercase(), CreditOnFidoOrFirmStatus.APPROVED.status.lowercase() -> {
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

    private fun navigateToProcessingTransaction() {
        creditSubscriptionManager.destroySubscription()
        popAndNavigateTo(
            route = "${Screen.OriginationVoucherScreen.baseRoute}/$idBrand/$idPrint/$email",
            popTo = Screen.SignDocumentProcessScreen.route
        )
    }

    private fun onNavigateToContinueValidatingIdentity() {
        creditSubscriptionManager.destroySubscription()
        popAndNavigateTo(
            route = ContinueValidatingOnfidoScreen.route,
            popTo = Screen.SignDocumentProcessScreen.route
        )
    }

    private fun onNavigateToOnfidoAndEvicertiaError(error: String) {
        creditSubscriptionManager.destroySubscription()
        popAndNavigateTo(
            route = "${Screen.OnfidoAndEvicertiaErrorsScreen.baseRoute}/$error/$idBrand/$pkUser/$identification/$email/$idUserRequest/$firstName/$lastName/$evisertiaStatus",
            popTo = Screen.SignDocumentProcessScreen.route
        )
    }

    private fun onNavigateToHome() {
        creditSubscriptionManager.destroySubscription()
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = true, homeState = HomeState.COLLAPSED)
    }

    fun logEvents(adjustEventType: AdjustEventType) {
        viewModelScope.launch {
            getAdjustEvent(adjustEventType).invoke()
        }
    }

    private fun getAdjustEvent(adjustEventType: AdjustEventType): suspend () -> Unit {
        val originationDto = OriginationEventDataDto(
            user = email,
            idBrand = idBrand,
            identification = identification,
            idUserRequest = idUserRequest.toInt(),
            pkUser = pkUser.toString(),
            idPrint = idPrint
        )
        return when (adjustEventType) {
            AdjustEventType.ORIGINATION_FIRST_SIGN_CONTRACT_EVICERTIA_5014,
            AdjustEventType.CROSSELLING_FIRST_FINNISH_EVICERTIA_5030 -> {
                getSignDocumentOriginationEvent(originationDto)
            }
            AdjustEventType.ORIGINATION_FIRST_CUSTOMER_REJECTED_5017 -> {
                getRejectedCustomerOriginationEvent(originationDto)
            }
            AdjustEventType.ORIGINATION_FIRST_SUCCESS_EVICERTIA_5018,
            AdjustEventType.CROSSELLING_FIRST_CUSTOMER_COMPLETE_REQUEST_5031 -> {
                getSuccessOriginationEvent(originationDto)
            }
            AdjustEventType.ORIGINATION_WAIT_SCREEN_EVICERTIA_5015 -> {
                getWaitingScreenOriginationEvent(originationDto)
            }
            AdjustEventType.ORIGINATION_RETRY_SCREEN_EVICERTIA_5016 -> {
                getRetryScreenOriginationEvent(originationDto)
            }
            else -> suspend {}
        }
    }

    private fun getRetryScreenOriginationEvent(originationDto: OriginationEventDataDto): suspend () -> Unit =
        suspend {
            registerAdjustEvent(
                adjustEventType = AdjustEventType.ORIGINATION_RETRY_SCREEN_EVICERTIA_5016,
                data = originationDto.toJson()
            )
        }

    private fun getWaitingScreenOriginationEvent(originationDto: OriginationEventDataDto): suspend () -> Unit =
        suspend {
            registerAdjustEvent(
                adjustEventType = AdjustEventType.ORIGINATION_WAIT_SCREEN_EVICERTIA_5015,
                data = originationDto.toJson()
            )
        }

    private fun getSuccessOriginationEvent(originationDto: OriginationEventDataDto): suspend () -> Unit =
        suspend {
            if (isCrosseling) {
                if (dataStorePreferences.isAdjustFirstOriginationCrosselingEvicertiaSuccessEventRegister().first()) {
                    registerAdjustEvent(
                        adjustEventType = AdjustEventType.CROSSELLING_FIRST_CUSTOMER_COMPLETE_REQUEST_5031,
                        data = originationDto.toJson()
                    )
                    dataStorePreferences.isAdjustFirstOriginationCrosselingEvicertiaSuccessEventRegister(false)
                }
            } else {
                if (dataStorePreferences.isAdjustFirstOriginationEvicertiaSuccessEventRegister().first()) {
                    registerAdjustEvent(
                        adjustEventType = AdjustEventType.ORIGINATION_FIRST_SUCCESS_EVICERTIA_5018,
                        data = originationDto.toJson()
                    )
                    dataStorePreferences.isAdjustFirstOriginationEvicertiaSuccessEventRegister(false)
                }
            }
            restartMetricsPreferences()
        }

    private fun getRejectedCustomerOriginationEvent(originationDto: OriginationEventDataDto): suspend () -> Unit =
        suspend {
            if (dataStorePreferences.isAdjustFirstOriginationEvicertiaCustomerRejectedEventRegister().first()) {
                registerAdjustEvent(
                    adjustEventType = AdjustEventType.ORIGINATION_FIRST_CUSTOMER_REJECTED_5017,
                    data = originationDto.toJson()
                )
                dataStorePreferences.isAdjustFirstOriginationEvicertiaCustomerRejectedEventRegister(false)
            }
        }

    private fun getSignDocumentOriginationEvent(originationDto: OriginationEventDataDto): suspend () -> Unit =
        suspend {
            if (isCrosseling) {
                if (dataStorePreferences.isAdjustFirstOriginationCrosselingEvicertiaSignDocumentEventRegister().first()) {
                    registerAdjustEvent(
                        adjustEventType = AdjustEventType.CROSSELLING_FIRST_FINNISH_EVICERTIA_5030,
                        data = originationDto.toJson()
                    )
                    dataStorePreferences.isAdjustFirstOriginationCrosselingEvicertiaSignDocumentEventRegister(false)
                }
            } else {
                if (dataStorePreferences.isAdjustFirstOriginationEvicertiaSignDocumentEventRegister().first()) {
                    registerAdjustEvent(
                        adjustEventType = AdjustEventType.ORIGINATION_FIRST_SIGN_CONTRACT_EVICERTIA_5014,
                        data = originationDto.toJson()
                    )
                    dataStorePreferences.isAdjustFirstOriginationEvicertiaSignDocumentEventRegister(false)
                }
            }
        }

    data class UIState(
        // Interactions
        val signDocumentProcessStep: String = GENERATE_DOCUMENT_STEP.value,
        val dialogParameters: DialogParameters = DialogParameters(),
        val signDocumentUrl: String = "",
        val loadingIcon: Int = drawable.ic_frame,
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
            is OnCallGetLinkCreditContractEvent -> onEvaluateWitchRequestCall()
            is OnCallGetLinkCreditContractSecondTime -> callQueryGetLinkCreditContractUseCase(true)
            is OnStartListenerSubscriptionCreditContractEvent -> onListenCreditContractEventSubscription()
            is OnChangeScreen -> uiState = uiState.copy(signDocumentProcessStep = uiEvent.signDocumentStep)
            is OnShowDialogInformation -> createDialog()
            is OnNavigateToHome -> onNavigateToHome()
            is OnNavigateToContinueValidatingIdentity -> onNavigateToContinueValidatingIdentity()
        }
    }

    sealed class UIEvent {
        object OnCallGetLinkCreditContractEvent : UIEvent()
        object OnCallGetLinkCreditContractSecondTime : UIEvent()
        object OnStartListenerSubscriptionCreditContractEvent : UIEvent()
        object OnShowDialogInformation : UIEvent()
        data class OnChangeScreen(val signDocumentStep: String) : UIEvent()
        object OnNavigateToHome : UIEvent()
        object OnNavigateToContinueValidatingIdentity : UIEvent()
    }

    sealed class BaseEvent {
        object OpenWhatsAppLink : BaseEvent()
    }

    companion object {
        const val TIME_TO_WAIT_GENERATE_DOCUMENT_IN_MILLI_SECOND = 35000L
        const val TIME_TO_WAIT_VALIDATE_IDENTITY_IN_MILLI_SECOND = 40000L
        const val ID_PRINT_EMPTY = 0L
        const val PHONE_HARDCODED = "50371680915"
        const val LOG_SUBSCRIPTION_TAG = "MM_SUBSCRIPTION_L"
    }
}
