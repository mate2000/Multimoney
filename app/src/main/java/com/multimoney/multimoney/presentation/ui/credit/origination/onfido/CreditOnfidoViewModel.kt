package com.multimoney.multimoney.presentation.ui.credit.origination.onfido

import androidx.activity.result.ActivityResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.credit.MutationSaveCreditOperationUseCase
import com.multimoney.domain.interaction.security.MutationOnFidoInitialProcessUseCase
import com.multimoney.domain.interaction.security.MutationOnfidoCheckProcessUseCase
import com.multimoney.domain.model.security.OnfidoToken
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
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
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_ID_PRINT
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_URL
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnCallInFidoToken
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnConfigureOnFidoSdk
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnOpenDialogValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnOpenOnfidoSdk
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnSetCloseDialogTexts
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.RefreshOnFidoToken
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.GENERATE_DOCUMENT_STEP
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
import javax.inject.Inject

@HiltViewModel
class CreditOnfidoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val onFidoHelper: OnFidoHelper,
    private val mutationOnFidoInitialProcessUseCase: MutationOnFidoInitialProcessUseCase,
    private val mutationOnfidoCheckProcessUseCase: MutationOnfidoCheckProcessUseCase,
    private val mutationSaveCreditOperationUseCase: MutationSaveCreditOperationUseCase,
    val countDownTimer: MMCountDownTimer
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
    var evicertiaUrl: String = ""

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        pkUser = savedStateHandle[PK_USER] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        email = savedStateHandle[EMAIL] ?: ""
        firstName = savedStateHandle[FIRST_NAME] ?: ""
        lastName = savedStateHandle[LAST_NAME] ?: ""
        idUserRequest = savedStateHandle[ID_USER_REQUEST] ?: 0
        idPrint = savedStateHandle[SIGN_DOCUMENT_ID_PRINT] ?: 0
        evicertiaUrl = savedStateHandle[SIGN_DOCUMENT_URL] ?: ""
    }

    // Events
    val onFidoTokenEvent = MutableSharedFlow<MultimoneyResult<OnfidoToken?>>()

    private fun onInitializeTexts(title: Int, description: String) {
        closeDialogTitle = title
        closeDialogDescription = description
    }

    private fun onRefreshToken(
        names: String,
        lastNames: String,
        identification: String,
        applicationId: String,
        user: String,
        injectNewToken: (String?) -> Unit
    ) {
        viewModelScope.launch {
            mutationOnFidoInitialProcessUseCase.invoke(
                names,
                lastNames,
                identification,
                applicationId,
                Brand.CostaRica.id,
                user
            ).collectLatest { result ->
                result.onSuccess {
                    injectNewToken(it?.sdkToken ?: "")
                }
                result.onFailure {
                    // Close the sdk
                }
            }
        }
    }

    private fun callMutationOnFidoInitialProcess(
        names: String,
        lastNames: String,
        identification: String,
        applicationId: String,
        user: String
    ) {
        viewModelScope.launch {
            mutationOnFidoInitialProcessUseCase.invoke(
                names,
                lastNames,
                identification,
                applicationId,
                Brand.CostaRica.id,
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
                        onCallOnfidoCheckProcess(pkUser, identification, idBrand ?: 0, idUserRequest, email)
                    }

                    override fun userExited(exitCode: ExitCode) {
                        countDownTimer.resumeTimer()
                        // Empty on purpose
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
        user: String,
        onNextStep: () -> Unit
    ) {
        executeUseCase {
//            mutationOnfidoCheckProcessUseCase.invoke(
//                identification,
//                PACKAGE_NAME,
//                AppFlow.CREDIT_ORIGINATION.flow,
//                pkUser,
//                idUserRequest,
//                idBrand,
//                user
//            ).collectLatest { result ->
//                result.onSuccess {
//                    // nothing to do here
//                }
//                result.onFailure {
//                    // nothing to do here
//                }
//            }
            val signDocumentStep = if (idBrand == Brand.ElSalvador.id) {
                VALIDATE_IDENTITY.value
            } else {
                GENERATE_DOCUMENT_STEP.value
            }
            onNavigateToSignDocumentScreen(signDocumentStep)
        }
    }

    private fun onNavigateToSignDocumentScreen(signDocumentStep: String) {
        popAndNavigateTo(
            "${Screen.SignDocumentProcessScreen.baseRoute}/$signDocumentStep/$evicertiaUrl/$idPrint/$idBrand/$pkUser/$identification/$email/$idUserRequest/$firstName/$lastName",
            Screen.CreditOnfidoScreen.route
        )
    }

    private fun onCallSaveCreditOperation(
        idUserRequest: Long,
        pkUser: Long,
        user: String,
        idBrand: Int
    ) {
        executeUseCase {
            mutationSaveCreditOperationUseCase.invoke(
                idUserRequest,
                pkUser,
                user,
                idBrand
            ).collectLatest { result ->
                result.onSuccess {}
                result.onFailure {}
            }
        }
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
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.CreditScreen.route
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
                event.applicationId,
                event.user
            )

            is RefreshOnFidoToken -> onRefreshToken(
                event.firstName,
                event.lastName,
                event.identification,
                event.applicationId,
                event.user,
                event.injectNewToken
            )
            is OnOpenDialogValueChange -> uiState = uiState.copy(openDialog = event.openDialog)
            is OnCloseClick -> onCloseClick()
            is OnContinueClick -> continueAction()
            is OnContinueEnable -> uiState = uiState.copy(isContinueEnabled = event.isEnable)
            is OnLoadingValueChange -> uiState = uiState.copy(isLoading = event.isLoading)
            is OnNavigateToHome -> onNavigateToHome()
            is OnFailureWithDialog -> uiState = uiState.copy(isLoading = event.isLoading, openDialog = event.openDialog)
            is OnOpenOnfidoSdk -> onOpenOnfidoSdk(event.onOpenOnfidoSdk)
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
            val applicationId: String,
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
            val applicationId: String,
            val injectNewToken: (String?) -> Unit
        ) : UIEvent()

        object OnNavigateToHome : UIEvent()
        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()
        object OnCloseClick : UIEvent()
        object OnContinueClick : UIEvent()
        data class OnContinueEnable(val isEnable: Boolean) : UIEvent()
        data class OnFailureWithDialog(val isLoading: Boolean, val openDialog: DialogParameters) : UIEvent()
        data class OnOpenOnfidoSdk(val onOpenOnfidoSdk: () -> Unit) : UIEvent()
    }

    companion object {
        const val PACKAGE_NAME = "com.multimoney.multimoney.sv"
    }
}
