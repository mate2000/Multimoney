package com.multimoney.multimoney.presentation.ui.credit.origination

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
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
import com.multimoney.multimoney.presentation.navigation.CROSSELING
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
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_ORIGIN
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_STEP_ARG
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.NavigateToAccountScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnBackVisibilityValueChanged
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnCallMutationSaveCreditFlowStep
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnCurrencySymbolValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnCurrentLocationButtonValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnHideBottomSheet
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnMoveToStep
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnNextStep
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnOpenDialogValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnPreviousStep
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnRestartCrosselingNewAccount
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnSetBankListEmpty
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnSetCloseDialogTexts
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnSetNavigation
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnShowBottomSheet
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnUpdateScreenConfigData
import com.multimoney.multimoney.presentation.ui.credit.origination.util.SaveCreditStepsHelper
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentOrigin
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep
import com.multimoney.multimoney.presentation.util.getNavParam
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
    private val mutationSaveCreditOperationUseCase: MutationSaveCreditOperationUseCase,
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var closeDialogTitle: Int = string.empty
    var closeDialogDescription: String = ""
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
    var crosseling: Boolean = false

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
        crosseling = savedStateHandle[CROSSELING] ?: false
        uiState = uiState.copy(
            lastStep = savedStateHandle[CREDIT_STEP] ?: CreditStep.One.id,
            loadContent = true,
        )
    }

    private fun onInitializeTexts(title: Int, description: String) {
        closeDialogTitle = title
        closeDialogDescription = description
    }

    private fun onBackClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        if (crosseling && uiState.crosselingNewAccount && uiState.currentStep == CreditStep.Two.id && uiState.crosselingIsBankAccountListEmpty) {
            previousStep()
        } else if (crosseling && uiState.crosselingNewAccount && uiState.currentStep == CreditStep.Two.id && uiState.crosselingIsBankAccountListEmpty.not()) {
            onRestartCrosselingNewAccount()
        } else if (crosseling && uiState.crosselingNewAccount.not() && uiState.currentStep == CreditStep.Two.id) {
            previousStep()
        } else {
            if (crosseling && uiState.currentStep == CreditStep.Three.id) {
                onRestartCrosselingNewAccount()
            }
            previousStep()
        }
    }

    private fun onCloseClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = closeDialogTitle,
                description = closeDialogDescription,
                positiveResource = if (crosseling) {
                    string.crosseling_close_dialog_positive_button_text
                } else {
                    string.credit_close_dialog_positive_button_text
                },
                negativeResource = if (crosseling) {
                    string.crosseling_close_dialog_negative_button_text
                } else {
                    string.credit_close_dialog_negative_button_text
                },
                positiveAction = {
                    onNavigateToHome()
                },
                isActive = mutableStateOf(true),
            ),
        )
    }

    private fun onNavigateToHome() {
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = true, homeState = HomeState.COLLAPSED)
    }

    private fun onContinueClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        nextAction.invoke()
    }

    fun onHideBottomSheet() {
        uiState = uiState.copy(
            isBottomSheetVisible = false,
        )
        emitBaseEvent(BaseEvent.OnHideBottomSheet)
    }

    fun onShowBottomSheet() {
        uiState = uiState.copy(
            isBottomSheetVisible = true,
        )
        emitBaseEvent(BaseEvent.OnShowBottomSheet)
    }

    private fun moveToStep(step: Int) {
        if (step <= CREDIT_TOTAL_STEPS) {
            uiState = uiState.copy(
                currentStep = step,
                isCloseVisible = step >= CreditStep.One.id,
                lastStep = CreditStep.One.id,
            )
        } else {
            navigateToOnfido()
        }
    }

    private fun nextStep() {
        if (nextStep <= CREDIT_TOTAL_STEPS) {
            uiState = uiState.copy(
                currentStep = nextStep,
                isCloseVisible = nextStep >= CreditStep.One.id,
            )
        } else {
            navigateToOnfido()
        }
    }

    private fun previousStep() {
        if (previousStep > CreditStep.One.id || uiState.currentStep == CreditStep.Two.id) {
            uiState = uiState.copy(
                currentStep = previousStep,
                isCloseVisible = previousStep >= CreditStep.One.id,
                isBottomSheetVisible = false,
            )
        } else {
            navigateBack(popTo = Screen.HomeScreen.route, isRestart = true, homeState = HomeState.COLLAPSED)
        }
    }

    private fun navigateToOnfido() {
        popAndNavigateTo(
            "${Screen.CreditOnfidoScreen.baseRoute}/$idBrand/$pkUser/$identification/$email/$idUserRequest/$firstName/$lastName/$idPrint/$statusEvicertia",
            Screen.CreditScreen.route,
        )
    }

    private fun onSetNavigation(nextAction: () -> Unit, nextStep: Int, previousStep: Int) {
        this.nextAction = nextAction
        this.nextStep = nextStep
        this.previousStep = previousStep
    }

    private fun onCallMutationSaveCreditFlowStep() {
        executeUseCase {
            mutationSaveCreditFlowStepUseCase.invoke(
                user = email,
                idBrand = idBrand.toInt(),
                infoQuestion = saveCreditStepsHelper.creditFlowData,
                idLogUserRequest = idUserRequest,
                idUser = pkUser.toInt(),
                currentStep = CreditStep.Search.getNameById(nextStep),
            ).collectLatest { result ->
                result.onSuccess {
                    uiState = uiState.copy(isLoading = false)
                    if (crosseling) {
                        saveCreditOfferCrosselingFlow()
                    } else {
                        saveCreditOfferNormalFlow()
                    }
                }.onFailure {
                    uiState = uiState.copy(
                        isLoading = false,
                        openDialog = DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true),
                        ),
                    )
                }.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        }
    }

    private fun saveCreditOfferCrosselingFlow() {
        if ((idBrand.toInt() == Brand.ElSalvador.id && uiState.currentStep == CreditStep.Three.id) || (idBrand.toInt() == Brand.CostaRica.id && uiState.currentStep == CreditStep.Four.id)) {
            onCallSaveCreditOperation(isCrosseling = true)
        } else {
            nextStep()
        }
    }

    private fun saveCreditOfferNormalFlow() {
        if (uiState.currentStep == CreditStep.Eight.id) {
            onCallSaveCreditOperation()
        } else {
            nextStep()
        }
    }

    private fun onCallSaveCreditOperation(isCrosseling: Boolean = false) {
        executeUseCase {
            mutationSaveCreditOperationUseCase.invoke(
                idUserRequest.toLong(),
                pkUser.toLong(),
                email,
                idBrand.toInt(),
            ).collectLatest { result ->
                result.onSuccess {
                    if (isCrosseling) {
                        if (idBrand.toInt() == Brand.ElSalvador.id || it.idPrint == 0L) {
                            uiState = uiState.copy(showSVProcessSendSuccessfully = true)
                        } else {
                            popAndNavigateTo(
                                Screen.SignDocumentProcessScreen.baseRoute
                                    .plus(
                                        getNavParam(
                                            SIGN_DOCUMENT_STEP_ARG,
                                            SignDocumentStep.GENERATE_DOCUMENT_STEP.value,
                                        ),
                                    )
                                    .plus(getNavParam(SIGN_DOCUMENT_ORIGIN, SignDocumentOrigin.Crosseling.value))
                                    .plus(getNavParam(SIGN_DOCUMENT_ID_PRINT, it.idPrint))
                                    .plus(getNavParam(ID_BRAND, idBrand))
                                    .plus(getNavParam(PK_USER, pkUser))
                                    .plus(getNavParam(IDENTIFICATION, identification))
                                    .plus(getNavParam(EMAIL, email))
                                    .plus(getNavParam(ID_USER_REQUEST, idUserRequest))
                                    .plus(getNavParam(FIRST_NAME, firstName))
                                    .plus(getNavParam(LAST_NAME, lastName))
                                    .plus(getNavParam(CROSSELING, crosseling)),
                                Screen.CreditScreen.route,
                            )
                        }
                    } else {
                        nextStep()
                    }
                }.onFailure {
                    uiState = uiState.copy(
                        isLoading = false,
                        openDialog = DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true),
                        ),
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
                result.onFailure {
                    uiState = uiState.copy(
                        openDialog = DialogParameters(
                            description = it.getError().orEmpty(),
                            isActive = mutableStateOf(true),
                            positiveAction = {
                                onUIEvent(OnNavigateToHome)
                            },
                            dismissAction = {
                                onUIEvent(OnNavigateToHome)
                            },
                        ),
                    )
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

    private fun onNavigateToAccountScreen() {
        uiState = uiState.copy(
            crosselingNewAccount = true,
        )
    }

    private fun onRestartCrosselingNewAccount() {
        uiState = uiState.copy(
            crosselingNewAccount = false,
        )
    }

    private fun onSetBankListEmpty(ifBankListEmpty: Boolean) {
        uiState = uiState.copy(
            crosselingIsBankAccountListEmpty = ifBankListEmpty,
        )
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
        val isBottomSheetVisible: Boolean = false,
        val crosselingNewAccount: Boolean = false,
        val crosselingIsBankAccountListEmpty: Boolean = false,
        val showSVProcessSendSuccessfully: Boolean = false,
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnSetCloseDialogTexts -> onInitializeTexts(
                event.title,
                event.description,
            )
            is OnSetNavigation -> onSetNavigation(
                event.nextAction,
                event.nextStep,
                event.previousStep,
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
            is OnCallMutationSaveCreditFlowStep -> onCallMutationSaveCreditFlowStep()
            is OnBackVisibilityValueChanged -> uiState = uiState.copy(isBackVisible = event.isVisible)
            is OnShowBottomSheet -> onShowBottomSheet()
            is OnHideBottomSheet -> onHideBottomSheet()
            is OnNavigateToHome -> onNavigateToHome()
            is NavigateToAccountScreen -> onNavigateToAccountScreen()
            is OnRestartCrosselingNewAccount -> onRestartCrosselingNewAccount()
            is OnSetBankListEmpty -> onSetBankListEmpty(event.ifBankListEmpty)
        }
    }

    sealed class UIEvent {
        data class OnSetCloseDialogTexts(val title: Int, val description: String) : UIEvent()
        data class OnSetNavigation(
            val nextAction: () -> Unit = {},
            val nextStep: Int,
            val previousStep: Int,
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
        object OnCallMutationSaveCreditFlowStep : UIEvent()
        data class OnBackVisibilityValueChanged(val isVisible: Boolean) : UIEvent()
        data class OnSetBankListEmpty(val ifBankListEmpty: Boolean) : UIEvent()
        object OnShowBottomSheet : UIEvent()
        object OnHideBottomSheet : UIEvent()
        object OnNavigateToHome : UIEvent()
        object NavigateToAccountScreen : UIEvent()
        object OnRestartCrosselingNewAccount : UIEvent()
    }

    sealed class BaseEvent {
        object OnShowBottomSheet : BaseEvent()
        object OnHideBottomSheet : BaseEvent()
    }

    companion object {
        const val CREDIT_TOTAL_STEPS = 7
        const val CREDIT_INDICATOR_TOTAL_STEPS = 6
        const val BANNER_TIME = 2000L
    }
}
