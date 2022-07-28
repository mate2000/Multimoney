package com.multimoney.multimoney.presentation.ui.login.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.data.util.catalog.SignUpStep.Search
import com.multimoney.domain.interaction.security.MutationUpdateUserRegisterUseCase
import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnCallMutationUpdateUserRegisterUseCase
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnCountryCountryCodeValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnFirstLastNameValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnFirstNameValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnInitializeText
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnMoveToStep
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnNationalityValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnNextStep
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnOnFidoVerifiedChanged
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnOpenDialogValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnOpenSplashComeBack
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnPhoneNumberValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnPhoneVerifiedChanged
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnPreviousStep
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnSecondLastNameValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnSecondNameValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnSetNavigation
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnSharedIdentificationValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnUseDataValueChange
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val mutationUpdateUserRegisterUseCase: MutationUpdateUserRegisterUseCase
) : BaseViewModel() {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var isOnFidoVerified = false
    var isPhoneVerified = false
    var userData: UserData? = null
    var countryCode = ""
    var nextAction: () -> Unit = {}
    var closeDialogDescription: String = ""
    private var nextStep: Int = SignUpStep.One.id
    private var previousStep: Int = SignUpStep.One.id

    private fun onInitializeTexts(description: String) {
        closeDialogDescription = description
    }

    private fun nextStep() {
        if (nextStep <= SIGN_UP_TOTAL_STEPS) {
            uiState = uiState.copy(
                currentStep = nextStep,
                isCloseVisible = nextStep > SignUpStep.One.id
            )
        } else {
            completedProcessAction()
        }
    }

    private fun previousStep() {
        if (previousStep > SignUpStep.One.id || uiState.currentStep == SignUpStep.Two.id) {
            uiState = uiState.copy(
                currentStep = previousStep,
                isCloseVisible = previousStep > SignUpStep.One.id
            )
        } else {
            popAndNavigateTo(
                route = Screen.SignInScreen.route,
                popTo = Screen.SignUpScreen.route
            )
        }
    }

    private fun navigateToSplashComeBack(step: Int) {
        navigateTo("${Screen.SignUpSplashComeBackScreen.baseRoute}/".plus(step))
    }

    private fun moveToStep(step: Int) {
        if (step <= SIGN_UP_TOTAL_STEPS) {
            uiState = uiState.copy(
                currentStep = step,
                isCloseVisible = step > SignUpStep.One.id
            )
        }
    }

    private fun completedProcessAction() = popAndNavigateTo(
        route = Screen.SignUpCompleted.route,
        popTo = Screen.SignUpScreen.route
    )

    private fun onPhoneNumberChange(phoneNumber: String) {
        isPhoneVerified = phoneNumber == userData?.phoneNumber
        userData?.phoneNumber = phoneNumber
    }

    private fun onCountryCodeChange(
        countryCode: String,
        countryPhoneCode: String,
        isResetPhoneNumber: Boolean
    ) {
        userData?.let {
            it.countryCode = countryPhoneCode
            it.phoneNumber = if (isResetPhoneNumber) {
                null
            } else {
                it.phoneNumber
            }
        }
        this.countryCode = countryCode
    }

    private fun onNationalityChange(nationality: String) {
        userData?.nationality = nationality
        userData?.identificationValueType = ""
        userData?.identification = ""
        userData?.firstName = ""
        userData?.secondName = ""
        userData?.firstLastName = ""
        userData?.secondLastName = ""
        userData?.fullName = ""
    }

    private fun callMutationUpdateUserRegisterUseCase() = executeUseCase {
        mutationUpdateUserRegisterUseCase.invoke(
            pkUser = userData?.pkUser ?: "",
            user = userData?.userName ?: "",
            email = userData?.email ?: "",
            phoneNumber = userData?.phoneNumber,
            fullName = userData?.fullName,
            firstName = userData?.firstName,
            secondName = userData?.secondName,
            lastName = userData?.firstLastName,
            secondLastName = userData?.secondLastName,
            nationality = userData?.nationality,
            identification = userData?.identification,
            countryCode = userData?.countryCode,
            currentStep = userData?.currentStep ?: "",
            idBrand = Brand.Revamp.id
        ).collectLatest { result ->
            result.onSuccess {
                nextStep = Search.getIdByName(it?.currentStep)
                onUIEvent(OnLoadingValueChange(false))
                onUIEvent(OnUseDataValueChange(it))
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

    private fun onBackClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        previousStep()
    }

    private fun onCloseClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        uiState = uiState.copy(
            openDialog = DialogParameters(
                title = R.string.sign_up_close_dialog_title,
                description = closeDialogDescription,
                positiveText = R.string.sign_up_close_dialog_positive_button_text,
                negativeText = R.string.sign_up_close_dialog_negative_button_text,
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
        nextAction.invoke()
    }

    private fun onSetNavigation(nextAction: () -> Unit, nextStep: Int, previousStep: Int) {
        this.nextAction = nextAction
        this.nextStep = nextStep
        this.previousStep = previousStep
    }

    data class UIState(
        // Interactions
        val currentStep: Int = SignUpStep.One.id,
        val isCloseVisible: Boolean = false,
        val isContinueEnabled: Boolean = false,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnInitializeText -> onInitializeTexts(
                event.description,
            )
            is OnSetNavigation -> onSetNavigation(event.nextAction, event.nextStep, event.previousStep)
            is OnBackClick -> onBackClick(event.focusManager)
            is OnCloseClick -> onCloseClick(event.focusManager)
            is OnContinueClick -> onContinueClick(event.focusManager)
            is OnContinueEnable -> uiState = uiState.copy(isContinueEnabled = event.enable)
            is OnLoadingValueChange -> uiState = uiState.copy(isLoading = event.isLoading)
            is OnOpenDialogValueChange -> uiState = uiState.copy(openDialog = event.openDialog)
            is OnFailureWithDialog -> uiState =
                uiState.copy(isLoading = event.isLoading, openDialog = event.openDialog)
            is OnNextStep -> nextStep()
            is OnUseDataValueChange -> userData = event.userData
            is OnMoveToStep -> moveToStep(event.step)
            is OnPreviousStep -> previousStep()
            is OnPhoneNumberValueChange -> onPhoneNumberChange(event.phoneNumber)
            is OnSharedIdentificationValueChange -> userData =
                userData?.copy(identification = event.identificationValue)
            is OnNationalityValueChange -> onNationalityChange(event.nationality)
            is OnCountryCountryCodeValueChange -> onCountryCodeChange(
                event.countryCode,
                event.countryPhoneCode,
                event.isResetPhoneNumber
            )
            is OnCallMutationUpdateUserRegisterUseCase -> callMutationUpdateUserRegisterUseCase()
            is OnFirstNameValueChange -> userData?.firstName = event.firstName
            is OnSecondNameValueChange -> userData?.secondName = event.secondName
            is OnFirstLastNameValueChange -> userData?.firstLastName = event.firstLastName
            is OnSecondLastNameValueChange -> userData?.secondLastName = event.secondLastName
            is OnOpenSplashComeBack -> navigateToSplashComeBack(event.step)
            is OnPhoneVerifiedChanged -> isPhoneVerified = event.isPhoneVerified
            is OnOnFidoVerifiedChanged -> isOnFidoVerified = event.isOnFidoVerified
        }
    }

    sealed class UIEvent {
        data class OnInitializeText(
            val description: String
        ) : UIEvent()

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

        data class OnUseDataValueChange(val userData: UserData?) : UIEvent()

        data class OnMoveToStep(val step: Int) : UIEvent()
        data class OnSharedIdentificationValueChange(val identificationValue: String) : UIEvent()
        data class OnPhoneNumberValueChange(val phoneNumber: String) : UIEvent()
        data class OnNationalityValueChange(val nationality: String) : UIEvent()
        data class OnCountryCountryCodeValueChange(
            val countryCode: String,
            val countryPhoneCode: String,
            val isResetPhoneNumber: Boolean
        ) : UIEvent()

        data class OnFirstNameValueChange(val firstName: String) : UIEvent()
        data class OnSecondNameValueChange(val secondName: String) : UIEvent()
        data class OnFirstLastNameValueChange(val firstLastName: String) : UIEvent()
        data class OnSecondLastNameValueChange(val secondLastName: String) : UIEvent()
        data class OnOpenSplashComeBack(val step: Int) : UIEvent()
        data class OnPhoneVerifiedChanged(val isPhoneVerified: Boolean) : UIEvent()
        data class OnOnFidoVerifiedChanged(val isOnFidoVerified: Boolean) : UIEvent()


        object OnNextStep : UIEvent()
        object OnPreviousStep : UIEvent()

        object OnCallMutationUpdateUserRegisterUseCase : UIEvent()
    }

    companion object {
        const val SIGN_UP_TOTAL_STEPS = 6
        const val SIGN_UP_INDICATOR_TOTAL_STEPS = 5
        const val PHONE_HARDCODED = "50371680915"
    }
}