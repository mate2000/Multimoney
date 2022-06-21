package com.multimoney.multimoney.presentation.ui.login.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.GsonHelper
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.domain.interaction.security.MutationUpdateUserRegisterUseCase
import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnIsBiometricAvailable
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnOpenDialogValueChange
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.multimoney.multimoney.util.BiometricHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    val biometricHelper: BiometricHelper,
    val gsonHelper: GsonHelper,
    private val mutationUpdateUserRegisterUseCase: MutationUpdateUserRegisterUseCase
) : BaseViewModel() {
    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var userData: UserData? = null
    var countryCode = ""
    var userPassword = ""
    var isBiometricAvailable = false
    var nextAction: () -> Unit = {}

    fun nextStep() {
        if (uiState.currentStep <= SIGN_UP_TOTAL_STEPS) {
            uiState = uiState.copy(
                currentStep = uiState.currentStep + 1,
                isCloseVisible = uiState.currentStep > SignUpStep.One.id
            )
        } else {
            completedProcessAction()
        }
    }

    fun previousStep() {
        if (uiState.currentStep > SignUpStep.One.id) {
            uiState = uiState.copy(
                currentStep = uiState.currentStep - 1,
                isCloseVisible = uiState.currentStep > SignUpStep.One.id
            )
        } else {
            popAndNavigateTo(
                route = Screen.SignInScreen.route,
                popTo = Screen.SignUpScreen.route
            )
        }
    }

    fun moveToStep(step: Int) {
        if (uiState.currentStep <= SIGN_UP_TOTAL_STEPS && step <= SIGN_UP_TOTAL_STEPS) {
            uiState = uiState.copy(
                currentStep = step,
                isCloseVisible = uiState.currentStep > SignUpStep.One.id
            )
        }
    }

    private fun completedProcessAction() = popAndNavigateTo(
        route = if (isBiometricAvailable) {
            "${Screen.SignUpBiometricsScreen.baseRoute}/${userData?.email}/$userPassword"
        } else {
            Screen.SignUpCompleted.route
        },
        popTo = Screen.SignUpScreen.route
    )


    fun callMutationUpdateUserRegisterUseCase() {
        viewModelScope.launch {
            mutationUpdateUserRegisterUseCase.invoke(
                pkUser = userData?.pkUser ?: "",
                user = userData?.userName ?: "",
                email = userData?.email ?: "",
                phoneNumber = userData?.phoneNumber,
                fullName = userData?.fullName,
                firstName = userData?.firstName,
                secondName = userData?.secondName,
                lastName = userData?.lastName,
                secondLastName = userData?.secondLastName,
                contactMeans = userData?.contactMeans,
                nationality = userData?.nationality,
                identification = userData?.identification,
                countryCode = userData?.countryCode,
                currentStep = userData?.currentStep ?: "",
                idBrand = Brand.Revamp.id
            ).collectLatest { result ->
                result.onSuccess {
                    isLoading = false
                    userData = it
                    nextStep()
                }
                result.onFailure {
                    isLoading = false
                    openDialog = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                }
                result.onLoading {
                    isLoading = true
                }
            }
        }
    }

    private fun onBackClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        previousStep()
    }

    private fun onCloseClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        popAndNavigateTo(
            route = Screen.SignInScreen.route,
            popTo = Screen.SignUpScreen.route
        )
    }

    private fun onContinueClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        nextAction.invoke()
    }

    data class UIState(
        // Interactions
        val currentStep: Int = SignUpStep.Five.id,
        val isCloseVisible: Boolean = false,
        val isContinueEnabled: Boolean = false,
        val isLoading: Boolean = true,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnBackClick -> onBackClick(event.focusManager)
            is OnCloseClick -> onCloseClick(event.focusManager)
            is OnContinueClick -> onContinueClick(event.focusManager)
            is OnContinueEnable -> uiState = uiState.copy(isContinueEnabled = event.enable)
            is OnIsBiometricAvailable -> isBiometricAvailable = event.value
            is OnLoadingValueChange -> uiState = uiState.copy(isLoading = event.isLoading)
            is OnOpenDialogValueChange -> uiState = uiState.copy(openDialog = event.openDialog)
            is OnFailureWithDialog -> uiState =
                uiState.copy(isLoading = event.isLoading, openDialog = event.openDialog)
        }
    }

    sealed class UIEvent {
        data class OnBackClick(val focusManager: FocusManager) : UIEvent()
        data class OnCloseClick(val focusManager: FocusManager) : UIEvent()
        data class OnContinueClick(val focusManager: FocusManager) : UIEvent()
        data class OnContinueEnable(val enable: Boolean) : UIEvent()
        data class OnIsBiometricAvailable(val value: Boolean) : UIEvent()
        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()
        data class OnOpenDialogValueChange(val openDialog: DialogParameters) : UIEvent()
        data class OnFailureWithDialog(
            val isLoading: Boolean,
            val openDialog: DialogParameters
        ) : UIEvent()
    }

    companion object {
        const val SIGN_UP_TOTAL_STEPS = 5
        const val PHONE_HARDCODED = "50371680915"
    }
}