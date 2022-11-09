package com.multimoney.multimoney.presentation.ui.smart

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.SmartSteps
import com.multimoney.domain.interaction.accountsmart.MutationGlobalRequestUseCase
import com.multimoney.domain.interaction.accountsmart.QueryStepByStepUseCase
import com.multimoney.domain.model.accountsmart.AccountSmartData
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.Screen.SignInScreen
import com.multimoney.multimoney.presentation.navigation.Screen.SignUpScreen
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCallMutationUpdateGlobalRequestUseCase
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnNextStep
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnOpenDialogValueChange
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnPreviousStep
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnSetNavigation
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class SmartViewModel @Inject constructor(
    private val queryStepByStepUseCase: QueryStepByStepUseCase,
    private val mutationGlobalRequestUseCase: MutationGlobalRequestUseCase,
    private val dataStorePreferences: DataStorePreferences,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // bundle parameters
    val pkUser = savedStateHandle[PK_USER] ?: ""
    val idBrand = savedStateHandle[ID_BRAND] ?: ""
    val user = savedStateHandle[USER] ?: ""
    val idBrandAsInt = idBrand.toIntOrNull() ?: DEFAULT_ID_BRAND_ERROR

    // Stateless
    var nextAction: () -> Unit = {}
    var closeDialogDescription: String = ""
    var accountSmartData: AccountSmartData? = null
    private var nextStep: Int = SmartSteps.One.id
    private var previousStep: Int = SmartSteps.One.id

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    init {
        accountSmartData = AccountSmartData(
            pkUser = pkUser,
            idBrand = idBrandAsInt,
            user = user
        )
    }

    // FIXME, this is the logic to list the data, it should be handled in another ticket
    private fun callQueryStepByStepUseCase() = executeUseCase {
        queryStepByStepUseCase.invoke(
            user = accountSmartData?.user ?: "",
            idBrand = accountSmartData?.idBrand ?: 0,
            idRequest = 264
        ).collectLatest { result ->
            dataStorePreferences.getIdBrand().first()
            result.onSuccess {
                onUIEvent(OnLoadingValueChange(false))
            }
            result.onFailure {
                onUIEvent(
                    OnFailureWithDialog(
                        isLoading = false,
                        openDialog = DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                )
            }
            result.onLoading {
                onUIEvent(OnLoadingValueChange(true))
            }
        }
    }

    private fun callMutationGlobalRequestUseCase() = executeUseCase {
        mutationGlobalRequestUseCase.invoke(
            pkUser = accountSmartData?.pkUser?.toInt() ?: 0,
            status = accountSmartData?.status ?: 0,
            idProfessionType = accountSmartData?.idProfessionType ?: 0,
            idAddressLevel1 = accountSmartData?.idAddressLevel1 ?: 0,
            idAddressLevel2 = accountSmartData?.idAddressLevel2 ?: 0,
            idAddressLevel3 = accountSmartData?.idAddressLevel3 ?: 0,
            idEconomicActivity = accountSmartData?.idEconomicActivity ?: 0,
            income = accountSmartData?.income?.toDouble() ?: 0.0,
            addressDetail = accountSmartData?.addressDetail ?: "",
            isPEP = accountSmartData?.isPEP ?: false,
            user = accountSmartData?.user ?: "",
            idBrand = accountSmartData?.idBrand ?: 0,
            currentStep = accountSmartData?.currentStep ?: "",
            idCivilStatusType = accountSmartData?.idCivilStatusType ?: 0,
            birthday = accountSmartData?.birthday ?: "",
            expirationDate = accountSmartData?.expirationDate ?: "",
            idGender = accountSmartData?.idGender ?: 0,
            companyName = accountSmartData?.companyName.orEmpty(),
            aboutCompany = accountSmartData?.aboutCompany.orEmpty(),
            institutionPension = accountSmartData?.institutionPension.orEmpty(),
            specifiesIncomeSource = accountSmartData?.specifiesIncomeSource ?: "",
            entrepreneurship = accountSmartData?.entrepreneurship ?: "",
            legalID = accountSmartData?.legalID ?: ""
        ).collectLatest { result ->
            result.onSuccess {
                onUIEvent(OnLoadingValueChange(false))
                onUIEvent(OnNextStep)
            }
            result.onFailure {
                onUIEvent(
                    OnFailureWithDialog(
                        isLoading = false,
                        openDialog = DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                )
            }
            result.onLoading {
                onUIEvent(OnLoadingValueChange(true))
            }
        }
    }

    private fun moveToStep(step: Int) {
        if (step <= SMART_TOTAL_STEPS) {
            uiState = uiState.copy(
                currentStep = step,
                isCloseVisible = step > SmartSteps.One.id
            )
        }
    }

    private fun onBackClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        previousStep()
    }

    private fun onCloseClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = string.general_close_dialog_title,
                description = closeDialogDescription,
                positiveResource = string.sign_up_close_dialog_positive_button_text,
                negativeResource = string.sign_up_close_dialog_negative_button_text,
                positiveAction = {
                    popAndNavigateTo(
                        route = SignInScreen.route,
                        popTo = SignUpScreen.route
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

    private fun previousStep() {
        if (previousStep > SmartSteps.One.id || uiState.currentStep == SmartSteps.Two.id) {
            uiState = uiState.copy(
                currentStep = previousStep,
                isCloseVisible = previousStep > SmartSteps.One.id
            )
        } else {
            popAndNavigateTo(
                route = Screen.SignInScreen.route,
                popTo = Screen.SignUpScreen.route
            )
        }
    }

    private fun nextStep() {
        if (nextStep <= SMART_TOTAL_STEPS) {
            uiState = uiState.copy(
                currentStep = nextStep,
                isCloseVisible = nextStep > SmartSteps.One.id
            )
        }
    }

    private fun onSetNavigation(nextAction: () -> Unit, nextStep: Int, previousStep: Int) {
        this.nextAction = nextAction
        this.nextStep = nextStep
        this.previousStep = previousStep
    }

    private fun onFailure(error: HttpError) {
        uiState = uiState.copy(
            isLoading = false,
            openDialog = DialogParameters(
                description = error.getError() ?: "",
                isActive = mutableStateOf(true)
            )
        )
    }

    /**
     * this function will update the accountSmartData object with the new data coming from
     * the child screen after tapping on the "continue" button, also it will trigger the
     * API call by calling the callMutationGlobalRequestUseCase() function.
     */
    private fun onUpdateAccountSmartData(accountData: AccountSmartData?) {
        accountSmartData = accountData
        callMutationGlobalRequestUseCase()
    }

    data class UIState(
        // Interactions
        val currentStep: Int = SmartSteps.One.id,
        val isCloseVisible: Boolean = false,
        val isContinueEnabled: Boolean = false,
        val buttonTextRes: Int = string.button_continue,
        val isLoading: Boolean = false,
        val isContinueVisible: Boolean = true,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
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
            is OnNextStep -> nextStep()
            is OnPreviousStep -> previousStep()
            is UIEvent.OnContinueVisible -> uiState =
                uiState.copy(isContinueVisible = event.visible, buttonTextRes = event.textResId)
            is OnCallMutationUpdateGlobalRequestUseCase -> onUpdateAccountSmartData(event.accountSmartData)
        }
    }

    sealed class UIEvent {

        data class OnBackClick(val focusManager: FocusManager) : UIEvent()
        data class OnCloseClick(val focusManager: FocusManager) : UIEvent()
        data class OnContinueClick(val focusManager: FocusManager) : UIEvent()
        data class OnContinueEnable(val enable: Boolean) : UIEvent()
        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()
        data class OnOpenDialogValueChange(val openDialog: DialogParameters) : UIEvent()
        data class OnFailureWithDialog(val isLoading: Boolean, val openDialog: DialogParameters) :
            UIEvent()

        data class OnSetNavigation(
            val nextAction: () -> Unit = {},
            val nextStep: Int,
            val previousStep: Int
        ) : UIEvent()

        object OnNextStep : UIEvent()
        object OnPreviousStep : UIEvent()
        data class OnContinueVisible(val visible: Boolean, val textResId: Int = string.button_continue) : UIEvent()
        data class OnCallMutationUpdateGlobalRequestUseCase(val accountSmartData: AccountSmartData?) :
            UIEvent()
    }

    companion object {
        const val SMART_TOTAL_STEPS = 6
        const val SMART_INDICATOR_TOTAL_STEPS = 5
        const val DEFAULT_ID_BRAND_ERROR = -1
    }
}
