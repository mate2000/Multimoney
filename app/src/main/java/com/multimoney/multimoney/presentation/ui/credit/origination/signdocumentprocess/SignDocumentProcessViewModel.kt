package com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus
import com.multimoney.domain.interaction.credit.QueryGetLinkCreditContractUseCase
import com.multimoney.domain.model.credit.CreditContractEvent
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignDocumentProcessViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val creditSubscriptionManager: CreditSubscriptionManager,
    private val queryGetLinkCreditContractUseCase: QueryGetLinkCreditContractUseCase,
    val mmCountDownTimer: MMCountDownTimer
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
        onShouldStartSubscription()
        onShouldCallGetLinkCreditContract()
    }

    private fun onShouldCallGetLinkCreditContract() {
        if (evisertiaStatus.lowercase() != CreditOnFidoOrFirmStatus.FIRMED.status.lowercase()
            && evisertiaStatus.lowercase() != CreditOnFidoOrFirmStatus.OVER_COUNTER.status.lowercase()
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
        creditSubscriptionManager.subscriptionSubscribe(getCreditSubscriptionListener())
        if (creditSubscriptionManager.hasEvisertiaLink()) {
            uiState = uiState.copy(
                signDocumentProcessStep = SIGN_DOCUMENTS_STEP.value,
                signDocumentUrl = creditSubscriptionManager.getEvisertioLink() ?: ""
            )
        }
    }

    private fun getCreditSubscriptionListener() = object : CreditSubscriptionManager.SubscriptionEventListener {
        override fun onCapturedEvent(creditContractEvent: CreditContractEvent?) {
            handleSubscriptionsSteps(creditContractEvent = creditContractEvent)
            Timber.wtf("${LOG_SUBSCRIPTION_TAG}: ${creditContractEvent?.currentStep}")
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
                        isEvisertiaOverCounted(linkCreditContract?.statusEvicertia) -> {
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
                    uiState = uiState.copy(
                        signDocumentProcessStep = PROCESSING_TRANSACTION.value
                    )
                } else {
                    handleOnfidoStatus(creditContractEvent)
                }
            }
            CreditSubscriptionStep.DocumentsRejected.step -> {
                if (isEvisertiaOverCounted(creditContractEvent.statusEvicertia)) {
                    onNavigateToOnfidoAndEvicertiaError(EVICERTIA_REJECTED_SECOND_TIME.value)
                } else {
                    onNavigateToOnfidoAndEvicertiaError(EVICERTIA_REJECTED_FIRST_TIME.value)
                }
            }
            CreditSubscriptionStep.AccountActivated.step -> {
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

    private fun isEvisertiaOverCounted(evisertiaStatus: String?) =
        evisertiaStatus?.lowercase() == CreditOnFidoOrFirmStatus.OVER_COUNTER.status.lowercase()

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
