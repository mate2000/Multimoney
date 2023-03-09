package com.multimoney.multimoney.presentation.ui.smart.origination.onfido

import androidx.activity.result.ActivityResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.SmartOnFidoOrFirmStatus
import com.multimoney.data.util.catalog.SmartWorkflow
import com.multimoney.domain.interaction.accountsmart.MutationAccountStatusUseCase
import com.multimoney.domain.interaction.accountsmart.MutationSaveAutomatedSmartAccountUseCase
import com.multimoney.domain.interaction.security.MutationOnFidoInitialProcessUseCase
import com.multimoney.domain.interaction.security.MutationOnfidoCheckProcessUseCase
import com.multimoney.domain.model.accountsmart.AccountSmartContractResult
import com.multimoney.domain.model.security.OnfidoToken
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.BuildConfig
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SYS_ID_ACCOUNT_REQUEST
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.WORK_FLOW
import com.multimoney.multimoney.presentation.navigation.navgraph.COMING_FROM_CRYPTO
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.EVICERTIA_STATUS
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.LAST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_GLOBAL_ID
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_URL
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.Companion.URL_EMPTY
import com.multimoney.multimoney.presentation.ui.smart.origination.SmartSubscriptionManager
import com.multimoney.multimoney.presentation.ui.smart.origination.onfido.SmartOnfidoViewModel.UIEvent.OnCallInFidoToken
import com.multimoney.multimoney.presentation.ui.smart.origination.onfido.SmartOnfidoViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.smart.origination.onfido.SmartOnfidoViewModel.UIEvent.OnConfigureOnFidoSdk
import com.multimoney.multimoney.presentation.ui.smart.origination.onfido.SmartOnfidoViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.origination.onfido.SmartOnfidoViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.smart.origination.onfido.SmartOnfidoViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.smart.origination.onfido.SmartOnfidoViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.onfido.SmartOnfidoViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.smart.origination.onfido.SmartOnfidoViewModel.UIEvent.OnOpenDialogValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.onfido.SmartOnfidoViewModel.UIEvent.OnOpenOnfidoSdk
import com.multimoney.multimoney.presentation.ui.smart.origination.onfido.SmartOnfidoViewModel.UIEvent.OnSetCloseDialogTexts
import com.multimoney.multimoney.presentation.ui.smart.origination.onfido.SmartOnfidoViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.smart.origination.onfido.SmartOnfidoViewModel.UIEvent.OnStartSubscription
import com.multimoney.multimoney.presentation.ui.smart.origination.onfido.SmartOnfidoViewModel.UIEvent.RefreshOnFidoToken
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.catalog.AppFlow
import com.multimoney.multimoney.presentation.util.catalog.CreditSubscriptionStep
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.GENERATE_DOCUMENT_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.SIGN_DOCUMENTS_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.VALIDATE_IDENTITY
import com.multimoney.multimoney.presentation.util.onfido.OnFidoHelper
import com.onfido.android.sdk.capture.ExitCode
import com.onfido.android.sdk.capture.Onfido.OnfidoResultListener
import com.onfido.android.sdk.capture.errors.OnfidoException
import com.onfido.android.sdk.capture.upload.Captures
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class SmartOnfidoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val onFidoHelper: OnFidoHelper,
    private val mutationOnFidoInitialProcessUseCase: MutationOnFidoInitialProcessUseCase,
    private val mutationOnfidoCheckProcessUseCase: MutationOnfidoCheckProcessUseCase,
    val countDownTimer: MMCountDownTimer,
    private val smartSubscriptionManager: SmartSubscriptionManager,
    private val mutationAccountStatusUseCase: MutationAccountStatusUseCase,
    private val mutationSaveSmartAccount: MutationSaveAutomatedSmartAccountUseCase
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    var closeDialogTitle: Int = string.empty
    var closeDialogDescription: String = ""
    var onFidoError: String = ""
    var continueAction: () -> Unit = {}

    // Parameters
    var idBrand: Int? = null
    var pkUser: Long = 0
    var identification: String = ""
    var email: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var idRequestSysde: Long = 0
    var evicertiaUrl: String = ""
    var evicertiaStatus: String = ""
    var applicantId: String? = ""
    var user: String = ""
    var globalId: Long? = 0
    var comingFromCrypto: Boolean = false
    var workflow: String = ""

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        pkUser = savedStateHandle[PK_USER] ?: 0
        user = savedStateHandle[USER] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        email = savedStateHandle[EMAIL] ?: ""
        firstName = savedStateHandle[FIRST_NAME] ?: ""
        lastName = savedStateHandle[LAST_NAME] ?: ""
        idRequestSysde = savedStateHandle[SYS_ID_ACCOUNT_REQUEST] ?: 0
        evicertiaUrl = savedStateHandle[SIGN_DOCUMENT_URL] ?: ""
        evicertiaStatus = savedStateHandle[EVICERTIA_STATUS] ?: ""
        globalId = savedStateHandle[SIGN_DOCUMENT_GLOBAL_ID] ?: 0
        comingFromCrypto = savedStateHandle[COMING_FROM_CRYPTO] ?: false
        workflow = savedStateHandle[WORK_FLOW] ?: ""
    }

    // Events
    val onFidoTokenEvent = MutableSharedFlow<MultimoneyResult<OnfidoToken?>>()

    private fun onStart() {
        if (workflow == SmartWorkflow.SMART_CONTRACT_PROCESS.workflow) {
            onCallMutationAccountStatusUseCase(true)
        } else {
            onStartSubscription()
        }
    }

    private fun onStartSubscription() {
        if (idRequestSysde != 0L) {
            onCallMutationAccountStatusUseCase()
            smartSubscriptionManager.idSubscriptionSubscribe(getSmartSubscriptionListener())
            smartSubscriptionManager.startSmartSubscription(
                idRequestSysde,
                idBrand ?: Brand.CostaRica.id
            )
        } else {
            callMutationSaveSmartAccount()
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
                evicertiaUrl = smartContractEvent.link ?: ""
            }
        }
    }

    private fun callMutationSaveSmartAccount() {
        executeUseCase {
            mutationSaveSmartAccount.invoke(
                user = user,
                idBrand = idBrand ?: Brand.CostaRica.id,
                identificationNumber = identification,
                idRequest = globalId ?: 0L
            ).collectLatest { result ->
                result.onSuccess {
                    smartSubscriptionManager.idSubscriptionSubscribe(getSmartSubscriptionListener())
                    smartSubscriptionManager.startSmartSubscription(
                        it?.idAccount ?: 0L,
                        idBrand ?: Brand.CostaRica.id
                    )
                }
            }
        }
    }

    private fun onCallMutationAccountStatusUseCase(
        navigateToEvicertia: Boolean = false
    ) = executeUseCase {
        mutationAccountStatusUseCase.invoke(
            user = user,
            idBrand = idBrand ?: Brand.CostaRica.id,
            identificationNumber = identification,
            newState = ProductViewModel.DEFAULT_NEW_STATE,
            typeState = ProductViewModel.DEFAULT_TYPE_STATE,
            idAccountSysde = idRequestSysde,
            idAccountRequest = globalId
                ?: 0L
        ).collectLatest { result ->
            result.onSuccess {
                evicertiaUrl = it?.urlFirmDocument ?: URL_EMPTY
                if (navigateToEvicertia) {
                    navigateToCorrectScreen()
                }
            }
        }
    }

    private fun onInitializeTexts(title: Int, description: String) {
        closeDialogTitle = title
        closeDialogDescription = description
    }

    private fun onRefreshToken(
        names: String,
        lastNames: String,
        identification: String,
        user: String,
        injectNewToken: (String?) -> Unit
    ) {
        viewModelScope.launch {
            mutationOnFidoInitialProcessUseCase.invoke(
                names,
                lastNames,
                identification,
                BuildConfig.APPLICATION_ID,
                idBrand ?: 0,
                user
            ).collectLatest { result ->
                result.onSuccess {
                    applicantId = it?.applicantId
                    injectNewToken(it?.sdkToken ?: "")
                }
            }
        }
    }

    private fun callMutationOnFidoInitialProcess(
        names: String,
        lastNames: String,
        identification: String,
        user: String
    ) {
        viewModelScope.launch {
            mutationOnFidoInitialProcessUseCase.invoke(
                names,
                lastNames,
                identification,
                BuildConfig.APPLICATION_ID,
                idBrand ?: 0,
                user
            ).collectLatest { result ->
                onFidoTokenEvent.emit(result)
            }
        }
    }

    private fun onConfigureOnFidoSDK(
        result: ActivityResult
    ) {
        onFidoHelper.getOnFidoClient()
            .handleActivityResult(
                result.resultCode,
                result.data,
                object : OnfidoResultListener {
                    override fun userCompleted(captures: Captures) {
                        countDownTimer.resumeTimer()
                        onCallOnfidoCheckProcess(
                            pkUser,
                            identification,
                            idBrand ?: 0,
                            this@SmartOnfidoViewModel.idRequestSysde,
                            email
                        )
                    }

                    override fun userExited(exitCode: ExitCode) {
                        countDownTimer.resumeTimer()
                    }

                    override fun onError(exception: OnfidoException) {
                        countDownTimer.resumeTimer()
                        uiState = uiState.copy(
                            isContinueEnabled = false,
                            openDialog = DialogParameters(
                                description = onFidoError,
                                isActive = mutableStateOf(true)
                            )
                        )
                    }
                }
            )
    }

    private fun onOpenOnfidoSdk(openOnfidoSdk: () -> Unit) {
        uiState = uiState.copy(isLoading = false, isContinueEnabled = true)
        continueAction = openOnfidoSdk
    }

    private fun onCallOnfidoCheckProcess(
        pkUser: Long,
        identification: String,
        idBrand: Int,
        idUserRequest: Long,
        user: String
    ) {
        executeUseCase {
            mutationOnfidoCheckProcessUseCase.invoke(
                identification,
                applicantId ?: "",
                AppFlow.SMART.flow,
                pkUser,
                idUserRequest,
                idBrand,
                user
            ).collectLatest { result ->
                result.onSuccess {
                    it.id
                    // nothing to do here
                }
                result.onFailure {
                    // nothing to do here
                }
            }
        }
        navigateToCorrectScreen()
    }

    private fun navigateToCorrectScreen() {
        if (evicertiaUrl.isBlank()) evicertiaUrl = URL_EMPTY
        val signDocumentStep =
            if (evicertiaStatus.lowercase() == SmartOnFidoOrFirmStatus.FIRMED.status.lowercase()) {
                VALIDATE_IDENTITY.value
            } else if (evicertiaUrl.isNotBlank() && evicertiaUrl != URL_EMPTY) {
                SIGN_DOCUMENTS_STEP.value
            } else {
                GENERATE_DOCUMENT_STEP.value
            }
        onNavigateToSignDocumentScreen(signDocumentStep)
    }

    private fun onNavigateToSignDocumentScreen(signDocumentStep: String) {
        popAndNavigateTo(
            route = "${Screen.SmartSignScreen.baseRoute}/$signDocumentStep/${
            URLEncoder.encode(evicertiaUrl, StandardCharsets.UTF_8.toString())
            }/$idBrand/$pkUser/$identification/$email/$idRequestSysde/$firstName/$lastName/${true}/$globalId/$user/$comingFromCrypto/${!smartSubscriptionManager.hasEvicertiaLink()}",
            popTo = Screen.SmartOnfidoScreen.route
        )
    }

    private fun onNavigateToHome() {
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true,
            homeState = HomeState.COLLAPSED
        )
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnSetCloseDialogTexts -> onInitializeTexts(event.title, event.description)
            is OnConfigureOnFidoSdk -> onConfigureOnFidoSDK(event.result)
            is OnCallInFidoToken -> callMutationOnFidoInitialProcess(
                event.firstName,
                event.lastName,
                event.identification,
                event.user
            )

            is RefreshOnFidoToken -> onRefreshToken(
                event.firstName,
                event.lastName,
                event.identification,
                event.user,
                event.injectNewToken
            )
            is OnOpenDialogValueChange -> uiState = uiState.copy(openDialog = event.openDialog)
            is OnCloseClick -> onNavigateToHome()
            is OnContinueClick -> continueAction()
            is OnContinueEnable -> uiState = uiState.copy(isContinueEnabled = event.isEnable)
            is OnLoadingValueChange -> uiState = uiState.copy(isLoading = event.isLoading)
            is OnNavigateToHome -> onNavigateToHome()
            is OnFailureWithDialog ->
                uiState =
                    uiState.copy(isLoading = event.isLoading, openDialog = event.openDialog)
            is OnOpenOnfidoSdk -> onOpenOnfidoSdk(event.onOpenOnfidoSdk)
            is OnStartSubscription -> onStartSubscription()
            is OnStart -> onStart()
        }
    }

    data class UIState(
        val isLoading: Boolean = false,
        val isAlertVisible: Boolean = false,
        val isContinueEnabled: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    sealed class UIEvent {
        data class OnSetCloseDialogTexts(val title: Int, val description: String) : UIEvent()
        data class OnCallInFidoToken(
            val firstName: String,
            val lastName: String,
            val identification: String,
            val user: String
        ) : UIEvent()

        data class OnConfigureOnFidoSdk(
            val result: ActivityResult
        ) : UIEvent()

        data class OnOpenDialogValueChange(val openDialog: DialogParameters) : UIEvent()
        data class RefreshOnFidoToken(
            val firstName: String,
            val lastName: String,
            val identification: String,
            val user: String,
            val injectNewToken: (String?) -> Unit
        ) : UIEvent()

        object OnNavigateToHome : UIEvent()
        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()
        object OnCloseClick : UIEvent()
        object OnContinueClick : UIEvent()
        data class OnContinueEnable(val isEnable: Boolean) : UIEvent()
        data class OnFailureWithDialog(val isLoading: Boolean, val openDialog: DialogParameters) :
            UIEvent()

        data class OnOpenOnfidoSdk(val onOpenOnfidoSdk: () -> Unit) : UIEvent()

        object OnStartSubscription : UIEvent()
        object OnStart : UIEvent()
    }
}
