package com.multimoney.multimoney.presentation.ui.credit.origination

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.domain.interaction.credit.MutationSaveCreditFlowStepUseCase
import com.multimoney.domain.interaction.credit.MutationSaveCreditOperationUseCase
import com.multimoney.domain.interaction.credit.QueryScreenConfigUseCase
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.CREDIT_STEP
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.EVICERTIA_STATUS
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_USER_REQUEST
import com.multimoney.multimoney.presentation.navigation.navgraph.LAST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.ONFIDO_STATUS
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_ID_PRINT
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnBackVisibilityValueChanged
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnCallMutationSaveCreditFlowStep
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnCurrencySymbolValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnCurrentLocationButtonValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnMoveToStep
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnNextStep
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnOpenDialogValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnPreviousStep
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnSetCloseDialogTexts
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnSetNavigation
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnSetWhatsAppLink
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnShowBottomSheet
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnUpdateScreenConfigData
import com.multimoney.multimoney.presentation.ui.credit.origination.util.SaveCreditStepsHelper
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.GENERATE_DOCUMENT_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.SIGN_DOCUMENTS_STEP
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class CreditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val dataStorePreferences: DataStorePreferences,
    val saveCreditStepsHelper: SaveCreditStepsHelper,
    private val mutationSaveCreditFlowStepUseCase: MutationSaveCreditFlowStepUseCase,
    val queryScreenConfigUseCase: QueryScreenConfigUseCase,
    private val mutationSaveCreditOperationUseCase: MutationSaveCreditOperationUseCase
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var closeDialogTitle: Int = string.empty
    var closeDialogDescription: String = ""
    var whatsAppLink: String = ""
    var nextAction: () -> Unit = {}
    private var nextStep: Int = CreditStep.One.id
    private var previousStep: Int = CreditStep.One.id
    var currencySymbol = ""
    var idBrand: String = ""
    var pkUser: String = ""
    var identification: String = ""
    var email: String = ""
    var idUserRequest: Int = 0
    var firstName: String = ""
    var lastName: String = ""
    var idPrint: Long = 0
    var statusOnfido: String = ""
    var statusEvicertia: String = ""
    var linkEvicertia: String = URL_EMPTY

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: ""
        pkUser = savedStateHandle[PK_USER] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        email = savedStateHandle[EMAIL] ?: ""
        idUserRequest = savedStateHandle[ID_USER_REQUEST] ?: 0
        firstName = savedStateHandle[FIRST_NAME] ?: ""
        lastName = savedStateHandle[LAST_NAME] ?: ""
        statusOnfido = savedStateHandle[ONFIDO_STATUS] ?: ""
        statusEvicertia = savedStateHandle[EVICERTIA_STATUS] ?: ""
        idPrint = savedStateHandle[SIGN_DOCUMENT_ID_PRINT] ?: 0
        uiState = uiState.copy(
            lastStep = savedStateHandle[CREDIT_STEP] ?: CreditStep.One.id,
            loadContent = true
        )
    }

    private fun onInitializeTexts(title: Int, description: String) {
        closeDialogTitle = title
        closeDialogDescription = description
    }

    private fun onBackClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        previousStep()
    }

    private fun onCloseClick(focusManager: FocusManager) {
        focusManager.clearFocus()
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
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = true, homeState = HomeState.UNEXPANDED)
    }

    private fun onContinueClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        nextAction.invoke()
    }

    fun onHideBottomSheet() {
        emitBaseEvent(BaseEvent.OnHideBottomSheet)
    }

    fun onShowBottomSheet() {
        emitBaseEvent(BaseEvent.OnShowBottomSheet)
    }

    private fun moveToStep(step: Int) {
        if (step <= CREDIT_TOTAL_STEPS) {
            uiState = uiState.copy(
                currentStep = step,
                isCloseVisible = step >= CreditStep.One.id,
                lastStep = CreditStep.One.id
            )
        } else {
            moveToCorrectStep()
        }
    }

    private fun moveToCorrectStep() {
        when {
            statusOnfido.lowercase() != CreditOnFidoOrFirmStatus.APPROVED.status.lowercase() &&
                statusOnfido.lowercase() != CreditOnFidoOrFirmStatus.OVER_COUNTER.status.lowercase() -> {
                navigateToOnfido()
            }
            statusEvicertia.lowercase() != CreditOnFidoOrFirmStatus.FIRMED.status.lowercase() -> {
                navigateToSignDocumentProcess(
                    if (linkEvicertia == URL_EMPTY) {
                        GENERATE_DOCUMENT_STEP.value
                    } else {
                        SIGN_DOCUMENTS_STEP.value
                    }
                )
            }
        }
    }

    private fun nextStep() {
        if (nextStep <= CREDIT_TOTAL_STEPS) {
            uiState = uiState.copy(
                currentStep = nextStep,
                isCloseVisible = nextStep >= CreditStep.One.id
            )
        } else {
            navigateToOnfido()
        }
    }

    private fun previousStep() {
        if (previousStep > CreditStep.One.id || uiState.currentStep == CreditStep.Two.id) {
            uiState = uiState.copy(
                currentStep = previousStep,
                isCloseVisible = previousStep >= CreditStep.One.id
            )
        } else {
            navigateBack(popTo = Screen.HomeScreen.route, isRestart = true, homeState = HomeState.UNEXPANDED)
        }
    }

    private fun navigateToOnfido() {
        popAndNavigateTo(
            "${Screen.CreditOnfidoScreen.baseRoute}/$idBrand/$pkUser/$identification/$email/$idUserRequest/$firstName/$lastName/$idPrint/$URL_EMPTY/$statusEvicertia",
            Screen.CreditScreen.route
        )
    }

    private fun navigateToSignDocumentProcess(signDocumentStep: String) {
        popAndNavigateTo(
            "${Screen.SignDocumentProcessScreen.baseRoute}/$signDocumentStep/$URL_EMPTY/$idPrint/$idBrand/$pkUser/$identification/$email/$idUserRequest/$firstName/$lastName/${false}",
            Screen.CreditScreen.route
        )
    }

    private fun onSetNavigation(nextAction: () -> Unit, nextStep: Int, previousStep: Int) {
        this.nextAction = nextAction
        this.nextStep = nextStep
        this.previousStep = previousStep
    }

    private fun onCallMutationSaveCreditFlowStep(shouldCallSaveCreditOperation: Boolean) {
        executeUseCase {
            mutationSaveCreditFlowStepUseCase.invoke(
                user = email,
                idBrand = idBrand.toInt(),
                infoQuestion = saveCreditStepsHelper.creditFlowData,
                idLogUserRequest = idUserRequest,
                idUser = pkUser.toInt(),
                currentStep = CreditStep.Search.getNameById(nextStep)
            ).collectLatest { result ->
                result.onSuccess {
                    if (shouldCallSaveCreditOperation) {
                        onCallSaveCreditOperation()
                    } else {
                        uiState = uiState.copy(isLoading = false)
                        nextStep()
                    }
                }.onFailure {
                    uiState = uiState.copy(
                        isLoading = false,
                        openDialog = DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                }.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        }
    }

    fun queryCreditSteps() {
        executeUseCase {
            queryScreenConfigUseCase(pkUser, email, idBrand.toInt(), idUserRequest).collectLatest { result ->
                result.onSuccess {
                    // Wait 2 seconds to show banner
                    delay(BANNER_TIME)
                    onUIEvent(OnUpdateScreenConfigData(it))
                    moveToStep(uiState.lastStep)
                }
            }
        }
    }

    private fun onCallSaveCreditOperation() {
        executeUseCase {
            mutationSaveCreditOperationUseCase.invoke(
                idUserRequest.toLong(),
                pkUser.toLong(),
                email,
                idBrand.toInt()
            ).collectLatest { result ->
                result.onSuccess {
                    idPrint = it.idPrint
                    nextStep()
                }
                result.onFailure {
                    uiState = uiState.copy(
                        isLoading = false,
                        isAlertResultVisible = true
                    )
                }.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        }
    }

    fun getLoadingString(): Int = if (idBrand.isNotEmpty()) {
        when (idBrand.toInt()) {
            Brand.Guatemala.id -> string.credit_glad_to_see_you_gt
            else -> string.credit_glad_to_see_you
        }
    } else {
        string.empty
    }

    data class UIState(
        // Interactions
        val currentStep: Int = CreditStep.One.id,
        val isCloseVisible: Boolean = true,
        val isBackVisible: Boolean = true,
        val isContinueEnabled: Boolean = false,
        val isLoading: Boolean = false,
        val isCurrentLocationButtonVisible: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        var lastStep: Int = 1,
        var loadContent: Boolean = false,
        var isAlertResultVisible: Boolean = false
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnSetWhatsAppLink -> whatsAppLink = event.whatsAppLink
            is OnSetCloseDialogTexts -> onInitializeTexts(
                event.title,
                event.description
            )
            is OnSetNavigation -> onSetNavigation(
                event.nextAction,
                event.nextStep,
                event.previousStep
            )
            is OnBackClick -> onBackClick(event.focusManager)
            is OnCloseClick -> onCloseClick(event.focusManager)
            is OnContinueClick -> onContinueClick(event.focusManager)
            is OnContinueEnable -> uiState = uiState.copy(isContinueEnabled = event.enable)
            is OnLoadingValueChange -> uiState = uiState.copy(isLoading = event.isLoading)
            is OnOpenDialogValueChange -> uiState = uiState.copy(openDialog = event.openDialog)
            is OnFailureWithDialog -> uiState = uiState.copy(isLoading = event.isLoading, openDialog = event.openDialog)
            is OnCurrentLocationButtonValueChange ->
                uiState =
                    uiState.copy(isCurrentLocationButtonVisible = event.isVisible)
            is OnNextStep -> nextStep()
            is OnMoveToStep -> moveToStep(event.step)
            is OnPreviousStep -> previousStep()
            is OnCurrencySymbolValueChange -> currencySymbol = event.currencySymbol
            is OnUpdateScreenConfigData -> {
                saveCreditStepsHelper.start(event.screenConfigData)
            }
            is OnCallMutationSaveCreditFlowStep -> onCallMutationSaveCreditFlowStep(event.shouldCallSaveCreditOperation)
            is OnBackVisibilityValueChanged -> uiState = uiState.copy(isBackVisible = event.isVisible)
            is OnShowBottomSheet -> onShowBottomSheet()
            is OnNavigateToHome -> onNavigateToHome()
        }
    }

    sealed class UIEvent {
        data class OnSetWhatsAppLink(val whatsAppLink: String) : UIEvent()
        data class OnSetCloseDialogTexts(val title: Int, val description: String) : UIEvent()
        data class OnSetNavigation(
            val nextAction: () -> Unit = {},
            val nextStep: Int,
            val previousStep: Int
        ) : UIEvent()

        data class OnBackClick(val focusManager: FocusManager) : UIEvent()
        data class OnCloseClick(val focusManager: FocusManager) : UIEvent()
        data class OnContinueClick(val focusManager: FocusManager) : UIEvent()
        data class OnContinueEnable(val enable: Boolean) : UIEvent()
        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()
        data class OnOpenDialogValueChange(val openDialog: DialogParameters) : UIEvent()
        data class OnFailureWithDialog(val isLoading: Boolean, val openDialog: DialogParameters) : UIEvent()

        data class OnCurrentLocationButtonValueChange(val isVisible: Boolean) : UIEvent()
        object OnNextStep : UIEvent()
        object OnPreviousStep : UIEvent()
        data class OnMoveToStep(val step: Int) : UIEvent()

        data class OnCurrencySymbolValueChange(val currencySymbol: String) : UIEvent()
        data class OnUpdateScreenConfigData(val screenConfigData: List<CreditCatalog?>?) : UIEvent()
        data class OnCallMutationSaveCreditFlowStep(val shouldCallSaveCreditOperation: Boolean = false) : UIEvent()
        data class OnBackVisibilityValueChanged(val isVisible: Boolean) : UIEvent()
        object OnShowBottomSheet : UIEvent()
        object OnNavigateToHome : UIEvent()
    }

    sealed class BaseEvent {
        object OnShowBottomSheet : BaseEvent()
        object OnHideBottomSheet : BaseEvent()
    }

    companion object {
        const val CREDIT_TOTAL_STEPS = 7
        const val CREDIT_INDICATOR_TOTAL_STEPS = 6
        const val BANNER_TIME = 2000L
        const val URL_EMPTY = "url"
    }
}
