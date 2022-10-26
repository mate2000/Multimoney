package com.multimoney.multimoney.presentation.ui.smart

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.SmartSteps
import com.multimoney.domain.interaction.accountsmart.MutationGlobalRequestUseCase
import com.multimoney.domain.interaction.accountsmart.QueryStepByStepUseCase
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnBirthDateValueChange
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCivilStateValueChange
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnExpirationDateValueChange
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnGenderValueChange
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnMoveToStep
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnNextStep
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnOpenDialogValueChange
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnPreviousStep
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnProfessionValueChange
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnSetNavigation
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.SetUserData
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class SmartViewModel @Inject constructor(
    private val queryStepByStepUseCase: QueryStepByStepUseCase,
    private val mutationGlobalRequestUseCase: MutationGlobalRequestUseCase,
    private val dataStorePreferences: DataStorePreferences,
) : BaseViewModel(true) {

    // Stateless
    var nextAction: () -> Unit = {}
    var closeDialogDescription: String = ""
    private var nextStep: Int = SmartSteps.One.id
    private var previousStep: Int = SmartSteps.One.id
    var lastStep: Int = 1

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun callQueryStepByStepUseCase() = executeUseCase {
        queryStepByStepUseCase.invoke(
            user = uiState.user,
            idBrand = uiState.idBrand.toInt(),
            idRequest = 264
        ).collectLatest { result ->
            dataStorePreferences.getIdBrand().first()
            result.onSuccess {
                onUIEvent(OnLoadingValueChange(
                    false))
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
                onUIEvent(OnLoadingValueChange(
                    true))
            }
        }
    }

    private fun callMutationGlobalRequestUseCase() = executeUseCase {
        mutationGlobalRequestUseCase.invoke(
            pkUser = uiState.pkUser.toInt(),
            status = uiState.civilStatusId,
            idProfessionType = uiState.professionId,
            idAddressLevel1 = 7,
            idAddressLevel2 = 65,
            idAddressLevel3 = 557,
            idEconomicActivity = 7,
            income = 10000f,
            addressDetail = "del super 100 norte",
            isPEP = true,
            user = uiState.user,
            idBrand = uiState.idBrand.toInt(),
            currentStep = SmartSteps.Search.getNameById(uiState.currentStep)
        ).collectLatest { result ->
            result.onSuccess {
                onUIEvent(OnLoadingValueChange(
                    false))
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
                onUIEvent(OnLoadingValueChange(
                    true))
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
                titleResource = R.string.general_close_dialog_title,
                description = closeDialogDescription,
                positiveResource = R.string.sign_up_close_dialog_positive_button_text,
                negativeResource = R.string.sign_up_close_dialog_negative_button_text,
                positiveAction = {
                    popAndNavigateTo(
                        route = Screen.SignInScreen.route,
                        popTo = Screen.SignUpScreen.route
                    )
                },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onContinueClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        callMutationGlobalRequestUseCase()
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

    data class UIState(
        // Interactions
        val currentStep: Int = SmartSteps.One.id,
        val isCloseVisible: Boolean = false,
        val isContinueEnabled: Boolean = false,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val documentExpirationDate: String = "",
        val documentBirthDate: String = "",
        val gender: String = "",
        val genderId: Int = 1,
        val civilState: String = "",
        val civilStatusId: Int = 0,
        val profession: String = "",
        val professionId: Int = 0,
        val idBrand: String = "",
        val pkUser: String = "",
        val user: String = "",
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
            is OnFailureWithDialog -> uiState =
                uiState.copy(isLoading = event.isLoading, openDialog = event.openDialog)
            is OnNextStep -> nextStep()
            is OnMoveToStep -> moveToStep(event.step)
            is OnPreviousStep -> previousStep()
            is OnExpirationDateValueChange -> uiState =
                uiState.copy(documentExpirationDate = event.date)
            is OnBirthDateValueChange -> uiState = uiState.copy(documentBirthDate = event.date)
            is OnGenderValueChange -> uiState =
                uiState.copy(gender = event.gender, genderId = event.genderId)
            is OnCivilStateValueChange -> uiState =
                uiState.copy(civilState = event.civilState, civilStatusId = event.civilStatusId)
            is OnProfessionValueChange -> uiState =
                uiState.copy(profession = event.profession, professionId = event.professionId)
            is SetUserData -> uiState =
                uiState.copy(pkUser = event.pkUser, idBrand = event.idBrand, user = event.user)
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

        data class SetUserData(val idBrand: String, val pkUser: String, val user: String) :
            UIEvent()

        data class OnExpirationDateValueChange(val date: String) : UIEvent()
        data class OnBirthDateValueChange(val date: String) : UIEvent()
        data class OnGenderValueChange(val gender: String, val genderId: Int) : UIEvent()
        data class OnCivilStateValueChange(val civilState: String, val civilStatusId: Int) :
            UIEvent()

        data class OnProfessionValueChange(val profession: String, val professionId: Int) :
            UIEvent()

        data class OnSetNavigation(
            val nextAction: () -> Unit = {},
            val nextStep: Int,
            val previousStep: Int,
        ) : UIEvent()

        data class OnMoveToStep(val step: Int) : UIEvent()
        object OnNextStep : UIEvent()
        object OnPreviousStep : UIEvent()
    }

    companion object {
        const val SMART_TOTAL_STEPS = 6
        const val SMART_INDICATOR_TOTAL_STEPS = 5
    }
}