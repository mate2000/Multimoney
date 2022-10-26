package com.multimoney.multimoney.presentation.ui.credit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.data.util.catalog.CreditStep.Search
import com.multimoney.domain.interaction.credit.MutationSaveCreditFlowStepUseCase
import com.multimoney.domain.interaction.credit.QueryScreenConfigUseCase
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnCallMutationSaveCreditFlowStep
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnCurrencySymbolValueChange
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnCurrentLocationButtonValueChange
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnMoveToStep
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnNextStep
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnOpenDialogValueChange
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnPreviousStep
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnSetCloseDialogTexts
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnSetNavigation
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnUpdateScreenConfigData
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnUpdateUserData
import com.multimoney.multimoney.presentation.ui.credit.documentgeneration.DUMMY_URL
import com.multimoney.multimoney.presentation.ui.credit.util.SaveCreditStepsHelper
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class CreditViewModel @Inject constructor(
    val dataStorePreferences: DataStorePreferences,
    val saveCreditStepsHelper: SaveCreditStepsHelper,
    val mutationSaveCreditFlowStepUseCase: MutationSaveCreditFlowStepUseCase,
    val queryScreenConfigUseCase: QueryScreenConfigUseCase
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var closeDialogTitle: Int = R.string.empty
    var closeDialogDescription: String = ""
    var nextAction: () -> Unit = {}
    private var nextStep: Int = CreditStep.One.id
    private var previousStep: Int = CreditStep.One.id
    var currencySymbol = ""

    var idBrand: String = ""
    var pkUser: String = ""
    var identification: String = ""
    var email: String = ""
    var idUserRequest: String = ""

    private fun onUpdateUserData(
        idBrand: String,
        pkUser: String,
        identification: String,
        email: String,
        currentStep: Int,
        idUserRequest: String
    ) {
        this.idBrand = idBrand
        this.pkUser = pkUser
        this.identification = identification
        this.email = email
        this.idUserRequest = idUserRequest
        uiState = uiState.copy(
            lastStep = currentStep,
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
                positiveResource = R.string.credit_close_dialog_positive_button_text,
                negativeResource = R.string.credit_close_dialog_negative_button_text,
                positiveAction = {
                    popAndNavigateTo(
                        route = Screen.HomeScreen.route,
                        popTo = Screen.CreditScreen.route
                    )
                },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onContinueClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        nextAction.invoke()
    }

    private fun moveToStep(step: Int) {
        if (step <= CREDIT_TOTAL_STEPS) {
            uiState = uiState.copy(
                currentStep = step,
                isCloseVisible = step >= CreditStep.One.id,
                lastStep = CreditStep.One.id
            )
        } else {
            popAndNavigateTo(
                route = "${Screen.SignDocumentScreen.baseRoute}/".plus(DUMMY_URL),
                popTo = Screen.DocumentGenerationScreen.route
            )
        }
    }

    private fun nextStep() {
        if (nextStep <= CREDIT_TOTAL_STEPS) {
            uiState = uiState.copy(
                currentStep = nextStep,
                isCloseVisible = nextStep >= CreditStep.One.id
            )
        } else {
            documentGenerationProcess()
        }
    }

    private fun previousStep() {
        if (previousStep > CreditStep.One.id || uiState.currentStep == CreditStep.Two.id) {
            uiState = uiState.copy(
                currentStep = previousStep,
                isCloseVisible = previousStep >= CreditStep.One.id
            )
        } else {
            popAndNavigateTo(
                route = Screen.HomeScreen.route,
                popTo = Screen.CreditScreen.route
            )
        }
    }

    private fun documentGenerationProcess() {
        popAndNavigateTo(
            Screen.DocumentGenerationScreen.route,
            Screen.CreditScreen.route
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
                idLogUserRequest = idUserRequest.toInt(),
                idUser = pkUser.toInt(),
                currentStep = Search.getNameById(nextStep)
            ).collectLatest { result ->
                result.onSuccess {
                    uiState = uiState.copy(isLoading = false)
                    nextStep()
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
            queryScreenConfigUseCase(pkUser, email, idBrand.toInt(), idUserRequest).collectLatest {
                it.onSuccess {
                    // Wait 3 seconds to show banner
                    delay(BANNER_TIME)
                    onUIEvent(OnUpdateScreenConfigData(it))
                    moveToStep(uiState.lastStep)
                }
            }
        }
    }

    data class UIState(
        // Interactions
        val currentStep: Int = CreditStep.One.id,
        val isCloseVisible: Boolean = true,
        val isContinueEnabled: Boolean = false,
        val isLoading: Boolean = false,
        val isCurrentLocationButtonVisible: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        var lastStep: Int = 1,
        var loadContent: Boolean = false
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
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
            is OnFailureWithDialog ->
                uiState =
                    uiState.copy(isLoading = event.isLoading, openDialog = event.openDialog)
            is OnCurrentLocationButtonValueChange ->
                uiState =
                    uiState.copy(isCurrentLocationButtonVisible = event.isVisible)
            is OnNextStep -> nextStep()
            is OnMoveToStep -> moveToStep(event.step)
            is OnPreviousStep -> previousStep()
            is OnCurrencySymbolValueChange -> currencySymbol = event.currencySymbol
            is OnUpdateUserData -> onUpdateUserData(
                event.idBrand,
                event.pkUser,
                event.identification,
                event.email,
                event.step,
                event.idUserRequest
            )
            is OnUpdateScreenConfigData -> {
                saveCreditStepsHelper.start(event.screenConfigData)
            }
            is OnCallMutationSaveCreditFlowStep -> onCallMutationSaveCreditFlowStep()
        }
    }

    fun getLoadingString(): Int =
        if (idBrand.isNotEmpty()) {
            when (idBrand.toInt()) {
                Brand.Guatemala.id -> R.string.credit_glad_to_see_you_gt
                else -> R.string.credit_glad_to_see_you
            }
        } else {
            R.string.empty
        }

    sealed class UIEvent {
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
        data class OnFailureWithDialog(val isLoading: Boolean, val openDialog: DialogParameters) :
            UIEvent()

        data class OnCurrentLocationButtonValueChange(val isVisible: Boolean) : UIEvent()
        object OnNextStep : UIEvent()
        object OnPreviousStep : UIEvent()
        data class OnMoveToStep(val step: Int) : UIEvent()
        data class OnUpdateUserData(
            val idBrand: String,
            val pkUser: String,
            val identification: String,
            val email: String,
            val step: Int,
            val idUserRequest: String
        ) : UIEvent()

        data class OnCurrencySymbolValueChange(val currencySymbol: String) : UIEvent()
        data class OnUpdateScreenConfigData(val screenConfigData: List<CreditCatalog?>?) : UIEvent()
        object OnCallMutationSaveCreditFlowStep : UIEvent()
    }

    companion object {
        const val CREDIT_TOTAL_STEPS = 7
        const val CREDIT_INDICATOR_TOTAL_STEPS = 5
        const val BANNER_TIME = 3000L
    }
}
