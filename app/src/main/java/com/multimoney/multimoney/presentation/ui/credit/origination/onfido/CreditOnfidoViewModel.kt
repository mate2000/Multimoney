package com.multimoney.multimoney.presentation.ui.credit.origination.onfido

import androidx.activity.result.ActivityResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus
import com.multimoney.domain.interaction.security.MutationOnFidoInitialProcessUseCase
import com.multimoney.domain.interaction.security.MutationOnfidoCheckProcessUseCase
import com.multimoney.domain.model.security.OnfidoToken
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.BuildConfig
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CROSSELING
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
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
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnStartSubscription
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.CreditSubscriptionManager
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.catalog.AppFlow
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.GENERATE_DOCUMENT_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.VALIDATE_IDENTITY
import com.multimoney.multimoney.presentation.util.getNavParam
import com.multimoney.multimoney.presentation.util.onfido.OnFidoHelper
import com.onfido.android.sdk.capture.ExitCode
import com.onfido.android.sdk.capture.Onfido.OnfidoResultListener
import com.onfido.android.sdk.capture.errors.OnfidoException
import com.onfido.android.sdk.capture.upload.Captures
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class CreditOnfidoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val onFidoHelper: OnFidoHelper,
    private val mutationOnFidoInitialProcessUseCase: MutationOnFidoInitialProcessUseCase,
    private val mutationOnfidoCheckProcessUseCase: MutationOnfidoCheckProcessUseCase,
    val countDownTimer: MMCountDownTimer,
    private val creditSubscriptionManager: CreditSubscriptionManager
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
    var idUserRequest: Long = 0
    var idPrint: Long = 0
    var evicertiaStatus: String = ""
    var applicantId: String? = ""
    var whatsAppLink: String = ""

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        pkUser = savedStateHandle[PK_USER] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        email = savedStateHandle[EMAIL] ?: ""
        firstName = savedStateHandle[FIRST_NAME] ?: ""
        lastName = savedStateHandle[LAST_NAME] ?: ""
        idUserRequest = savedStateHandle[ID_USER_REQUEST] ?: 0
        idPrint = savedStateHandle[SIGN_DOCUMENT_ID_PRINT] ?: 0
        evicertiaStatus = savedStateHandle[EVICERTIA_STATUS] ?: ""
    }

    // Events
    val onFidoTokenEvent = MutableSharedFlow<MultimoneyResult<OnfidoToken?>>()

    private fun onStartSubscription() {
        if (idBrand != Brand.ElSalvador.id && idPrint != ID_PRINT_EMPTY) {
            creditSubscriptionManager.startCreditSubscription(idBrand ?: 0, idPrint)
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
                user,
            ).collectLatest { result ->
                result.onSuccess {
                    applicantId = it?.applicantId
                    injectNewToken(it?.sdkToken ?: "")
                }
                result.onFailure {
                    // The sdk shows an error.
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
        executeUseCase {
            mutationOnFidoInitialProcessUseCase.invoke(
                names,
                lastNames,
                identification,
                BuildConfig.APPLICATION_ID,
                idBrand ?: 0,
                user,
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
                        onCallOnfidoCheckProcess(
                            pkUser,
                            identification,
                            idBrand
                                ?: 0,
                            idUserRequest,
                            email,
                        )
                        navigateToCorrectScreen()
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
                                isActive = mutableStateOf(true),
                            ),
                        )
                    }
                },
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
        GlobalScope.launch {
            mutationOnfidoCheckProcessUseCase.invoke(
                identification,
                applicantId ?: "",
                AppFlow.CREDIT_ORIGINATION.flow,
                pkUser,
                idUserRequest,
                idBrand,
                user,
            ).collectLatest { result ->
                result.onSuccess {
                    // nothing to do here
                }
                result.onFailure {
                    // nothing to do here
                }
            }
        }
    }

    private fun navigateToCorrectScreen() {
        val signDocumentStep = if (idBrand == Brand.ElSalvador.id || idPrint == ID_PRINT_EMPTY) {
            VALIDATE_IDENTITY.value
        } else {
            if (evicertiaStatus.lowercase() == CreditOnFidoOrFirmStatus.FIRMED.status.lowercase()) {
                VALIDATE_IDENTITY.value
            } else {
                GENERATE_DOCUMENT_STEP.value
            }
        }
        onNavigateToSignDocumentScreen(signDocumentStep)
    }

    private fun onNavigateToSignDocumentScreen(signDocumentStep: String) {
        popAndNavigateTo(
            Screen.SignDocumentProcessScreen.baseRoute
                .plus(getNavParam(SIGN_DOCUMENT_STEP_ARG, signDocumentStep))
                .plus(getNavParam(SIGN_DOCUMENT_ID_PRINT, idPrint))
                .plus(getNavParam(ID_BRAND, idBrand))
                .plus(getNavParam(PK_USER, pkUser))
                .plus(getNavParam(IDENTIFICATION, identification))
                .plus(getNavParam(EMAIL, email))
                .plus(getNavParam(ID_USER_REQUEST, idUserRequest))
                .plus(getNavParam(FIRST_NAME, firstName))
                .plus(getNavParam(LAST_NAME, lastName))
                .plus(getNavParam(CROSSELING, false))
                .plus(getNavParam(SHOULD_GET_EVICERTIA_LINK, false))
                .plus(getNavParam(EVICERTIA_STATUS, evicertiaStatus)),
            Screen.CreditOnfidoScreen.route,
        )
    }

    private fun onCloseClick() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = closeDialogTitle,
                description = closeDialogDescription,
                positiveResource = string.credit_close_dialog_positive_button_text,
                negativeResource = string.credit_close_dialog_negative_button_text,
                positiveAction = {
                    onNavigateToHome()
                },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onNavigateToHome() {
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = true, homeState = HomeState.COLLAPSED)
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnSetCloseDialogTexts -> onInitializeTexts(event.title, event.description)
            is UIEvent.OnSetWhatsAppLink -> whatsAppLink = event.whatsAppLink
            is UIEvent.OnConfigureOnFidoSdk -> onConfigureOnFidoSDK(event.result)
            is UIEvent.OnCallInFidoToken -> callMutationOnFidoInitialProcess(
                event.firstName,
                event.lastName,
                event.identification,
                event.user
            )

            is UIEvent.RefreshOnFidoToken -> onRefreshToken(
                event.firstName,
                event.lastName,
                event.identification,
                event.user,
                event.injectNewToken
            )
            is UIEvent.OnOpenDialogValueChange -> uiState = uiState.copy(openDialog = event.openDialog)
            is UIEvent.OnCloseClick -> onCloseClick()
            is UIEvent.OnContinueClick -> continueAction()
            is UIEvent.OnContinueEnable -> uiState = uiState.copy(isContinueEnabled = event.isEnable)
            is UIEvent.OnLoadingValueChange -> uiState = uiState.copy(isLoading = event.isLoading)
            is UIEvent.OnNavigateToHome -> onNavigateToHome()
            is UIEvent.OnFailureWithDialog -> uiState =
                uiState.copy(isLoading = event.isLoading, openDialog = event.openDialog)
            is UIEvent.OnOpenOnfidoSdk -> onOpenOnfidoSdk(event.onOpenOnfidoSdk)
            is OnStartSubscription -> onStartSubscription()
        }
    }

    data class UIState(
        val isLoading: Boolean = false,
        val isAlertVisible: Boolean = false,
        val isContinueEnabled: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        var isAlertResultVisible: Boolean = false
    )

    sealed class UIEvent {
        data class OnSetWhatsAppLink(val whatsAppLink: String) : UIEvent()
        data class OnSetCloseDialogTexts(val title: Int, val description: String) : UIEvent()
        data class OnCallInFidoToken(
            val firstName: String,
            val lastName: String,
            val identification: String,
            val user: String
        ) : UIEvent()

        data class OnConfigureOnFidoSdk(
            val result: ActivityResult,
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
        data class OnFailureWithDialog(val isLoading: Boolean, val openDialog: DialogParameters) : UIEvent()
        data class OnOpenOnfidoSdk(val onOpenOnfidoSdk: () -> Unit) : UIEvent()
        object OnStartSubscription : UIEvent()
    }

    companion object {
        const val ID_PRINT_EMPTY = 0L
    }
}
