package com.multimoney.multimoney.presentation.ui.login.signup.email

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.data.util.catalog.UserStatus
import com.multimoney.domain.interaction.security.QueryValidateUserExistsUseCase
import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.email.SignUpEmailViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.login.signup.email.SignUpEmailViewModel.UIEvent.*
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.isEmailValid
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class SignUpEmailViewModel @Inject constructor(
    private val queryValidateUserExistsUseCase: QueryValidateUserExistsUseCase,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var previousUserEmail = ""
    private var isUserStatusIncomplete = true
    private var userCompletedDialogDescription = ""
    private var linkWhatsapp = ""
    private var blockedMessage = ""

    // Events
    val onValidateUserExistsEvent = MutableSharedFlow<MultimoneyResult<UserData?>>()

    private fun onStart(userCompletedDialogDescription: String, linkWhatsapp: String, blockedMessage: String) {
        this.userCompletedDialogDescription = userCompletedDialogDescription
        this.linkWhatsapp = linkWhatsapp
        this.blockedMessage = blockedMessage
    }

    private fun isFormValid() = emitBaseEvent(
        OnFormValidateCompleted(
            when {
                uiState.userEmail.isBlank() -> false
                isEmailValid(uiState.userEmail).not() -> false
                else -> true
            }
        )
    )

    private fun isUserEmailValid() {
        if (isEmailValid(uiState.userEmail).not()) {
            uiState = uiState.copy(userEmailError = Pair(true, R.string.sign_up_email_not_valid))
        }
    }

    private fun clearUserEmailError() {
        uiState = uiState.copy(userEmailError = Pair(false, R.string.error_empty))
    }

    private fun isDataChanged() = previousUserEmail != uiState.userEmail

    private fun callQueryValidationUserExistsUseCase(email: String) =
        executeUseCase {
            queryValidateUserExistsUseCase(
                email = email,
                dataStorePreferences.getDeviceId().first()
            ).collectLatest { result ->
                onValidateUserExistsEvent.emit(result)
            }
        }

    private fun onNextActionClick(nextStepAction: () -> Unit) {
        if (isDataChanged() || isUserStatusIncomplete.not()) {
            callQueryValidationUserExistsUseCase(
                uiState.userEmail
            )
        } else {
            nextStepAction.invoke()
        }
    }

    private fun onValidationUserExistsSuccess(
        context: Context,
        currentStep: Int,
        userData: UserData?,
        onUseDataValueChange: () -> Unit,
        nextStepAction: () -> Unit,
        openSignUpSplashComeBack: () -> Unit,
        onOpenDialog: (DialogParameters) -> Unit
    ) {
        onUseDataValueChange()
        if (userData?.status == VALID_EMAIL || userData?.status == ANOTHER_DEVICE_ALREADY_REGISTERED) {
            val step = SignUpStep.Search.getIdByName(userData.currentStep)
            if (step == currentStep && step < STEP_TO_SHOW_SPLASH) {
                isUserStatusIncomplete = true
                nextStepAction()
            } else {
                openSignUpSplashComeBack()
            }
        } else if (userData?.userStatus == UserStatus.Blocked.status) {
            onOpenDialog(
                DialogParameters(
                    titleResource = string.sign_up_email_blocked_dialog_title,
                    description = blockedMessage,
                    isActive = mutableStateOf(true),
                    positiveResource = string.contact,
                    negativeResource = string.cancel,
                    positiveAction = {
                        context.openWhatsAppDeepLink(linkWhatsapp)
                    }
                )
            )
        }
    }

    private fun setPreviousEmail() {
        previousUserEmail = uiState.userEmail
    }

    private fun onHandleUserState(
        previousStepAction: () -> Unit,
        onLoadingValueChange: () -> Unit,
        onOpenDialog: (DialogParameters) -> Unit
    ) {
        onLoadingValueChange()
        isUserStatusIncomplete = false
        onOpenDialog(
            DialogParameters(
                titleResource = string.sign_up_email_user_completed_dialog_title,
                description = userCompletedDialogDescription,
                positiveResource = string.sign_up_email_user_completed_dialog_positive,
                positiveAction = { previousStepAction() },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onUserEmailValueChange(value: String) {
        uiState = uiState.copy(userEmail = value)
        clearUserEmailError()
        isFormValid()
    }

    fun onSuccessValidation(
        context: Context,
        sharedViewModel: SignUpViewModel,
        userData: UserData?
    ) {
        onValidationUserExistsSuccess(
            context,
            currentStep = sharedViewModel.uiState.currentStep,
            userData = userData,
            onUseDataValueChange = {
                sharedViewModel.onUIEvent(
                    SignUpViewModel.UIEvent.OnUseDataValueChange(
                        userData?.copy(email = uiState.userEmail),
                        userData?.idBrand
                    )
                )
            },
            nextStepAction = { sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnNextStep) },
            openSignUpSplashComeBack = {
                sharedViewModel.onUIEvent(
                    SignUpViewModel.UIEvent.OnOpenSplashComeBack(
                        SignUpStep.Search.getIdByName(
                            userData?.currentStep
                        )
                    )
                )
            },
            onOpenDialog = {
                sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnOpenDialogValueChange(it))
            }
        )
    }

    private fun onShowAnotherDeviceAlreadyRegisteredDialog(onPositiveClick: () -> Unit) {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = R.string.sign_up_email_another_device_registered_dialog_title,
                descriptionResource = R.string.sign_up_email_another_device_registered_dialog_description,
                positiveResource = R.string.button_continue,
                negativeResource = R.string.cancel,
                isActive = mutableStateOf(true),
                positiveAction = onPositiveClick
            )
        )
    }

    data class UIState(
        // Fields
        val userEmail: String = "",
        val userEmailError: Pair<Boolean, Int> = Pair(false, R.string.sign_up_email_required),
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnStart -> onStart(event.userCompletedDialogDescription, event.linkWhatsapp, event.blockedMessage)
            is OnValidateForm -> isFormValid()
            is OnNextActionClick -> onNextActionClick(event.nextStepAction)
            is OnHandleUserStatus -> onHandleUserState(
                event.previousStepAction,
                event.onLoadingValueChange,
                event.onOpenDialog
            )
            is OnValidateUserEmail -> isUserEmailValid()
            is OnUserEmailValueChange -> onUserEmailValueChange(event.value)
            is OnShowAnotherDeviceAlreadyRegisteredDialog -> onShowAnotherDeviceAlreadyRegisteredDialog(event.onPositiveClick)
            is OnSetPreviousEmail -> setPreviousEmail()
        }
    }

    sealed class UIEvent {
        data class OnStart(
            val userCompletedDialogDescription: String,
            val linkWhatsapp: String,
            val blockedMessage: String
        ) : UIEvent()

        data class OnNextActionClick(val nextStepAction: () -> Unit) : UIEvent()

        data class OnHandleUserStatus(
            val context: Context,
            val userData: UserData?,
            val previousStepAction: () -> Unit,
            val onLoadingValueChange: () -> Unit,
            val onOpenDialog: (DialogParameters) -> Unit
        ) : UIEvent()

        data class OnUserEmailValueChange(val value: String) : UIEvent()
        object OnValidateForm : UIEvent()
        object OnValidateUserEmail : UIEvent()
        data class OnShowAnotherDeviceAlreadyRegisteredDialog(val onPositiveClick: () -> Unit) : UIEvent()
        object OnSetPreviousEmail : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    companion object {
        const val ANOTHER_DEVICE_ALREADY_REGISTERED = 3102
        private const val VALID_EMAIL = 0
        private const val STEP_TO_SHOW_SPLASH = 3
    }
}
