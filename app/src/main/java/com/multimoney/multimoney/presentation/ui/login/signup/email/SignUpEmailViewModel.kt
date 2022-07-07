package com.multimoney.multimoney.presentation.ui.login.signup.email

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.data.util.catalog.UserStatus
import com.multimoney.domain.interaction.security.MutationUserValidationUseCase
import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.email.SignUpEmailViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.login.signup.email.SignUpEmailViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.login.signup.email.SignUpEmailViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.login.signup.email.SignUpEmailViewModel.UIEvent.OnUserDataValidationSuccess
import com.multimoney.multimoney.presentation.ui.login.signup.email.SignUpEmailViewModel.UIEvent.OnUserEmailValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.email.SignUpEmailViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.ui.login.signup.email.SignUpEmailViewModel.UIEvent.OnValidateUserEmail
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.multimoney.multimoney.presentation.util.isEmailValid
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SignUpEmailViewModel @Inject constructor(
    private val mutationUserValidationUseCase: MutationUserValidationUseCase
) : BaseViewModel() {

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
    val onUserDataValidationEvent = MutableSharedFlow<MultimoneyResult<UserData?>>()

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

    private fun callMutationUserValidationUseCase(email: String, currentStep: String, idBrand: Int) =
        executeUseCase {
            mutationUserValidationUseCase(
                email = email,
                currentStep = currentStep,
                idBrand = idBrand
            ).collectLatest { result ->
                onUserDataValidationEvent.emit(result)
            }
        }

    private fun onNextActionClick(nextStepAction: () -> Unit) {
        if (isDataChanged() || isUserStatusIncomplete.not()) {
            callMutationUserValidationUseCase(
                uiState.userEmail,
                SignUpStep.One.name,
                Brand.Revamp.id
            )
        } else {
            nextStepAction.invoke()
        }
    }

    private fun onUserDataValidationSuccess(
        context: Context,
        currentStep: Int,
        userData: UserData?,
        onUseDataValueChange: () -> Unit,
        nextStepAction: () -> Unit,
        openSignUpSplashComeBack: () -> Unit,
        previousStepAction: () -> Unit,
        onLoadingValueChange: () -> Unit,
        onOpenDialog: (DialogParameters) -> Unit
    ) {
        previousUserEmail = userData?.email ?: ""
        onLoadingValueChange()
        onUseDataValueChange()
        if (userData?.userStatus == UserStatus.Incomplete.name) {
            isUserStatusIncomplete = true
            if (SignUpStep.Search.getIdByName(userData.currentStep) == currentStep) {
                nextStepAction()
            } else {
                openSignUpSplashComeBack()
            }
        } else if (userData?.userStatus == UserStatus.Active.name) {
            isUserStatusIncomplete = false
            onOpenDialog(
                DialogParameters(
                    title = string.sign_up_email_user_completed_dialog_title,
                    description = userCompletedDialogDescription,
                    positiveText = string.sign_up_email_user_completed_dialog_positive,
                    positiveAction = { previousStepAction() },
                    isActive = mutableStateOf(true)
                )
            )
        } else if (userData?.userStatus == UserStatus.Blocked.name) {
            isUserStatusIncomplete = false
            onOpenDialog(
                DialogParameters(
                    title = string.sign_up_email_blocked_dialog_title,
                    description = blockedMessage,
                    isActive = mutableStateOf(true),
                    positiveText = string.contact,
                    negativeText = string.cancel,
                    positiveAction = {
                        context.openWhatsAppDeepLink(linkWhatsapp)
                    }
                )
            )
        }
    }

    private fun onUserEmailValueChange(value: String) {
        uiState = uiState.copy(userEmail = value)
        clearUserEmailError()
        isFormValid()
    }

    data class UIState(
        // Fields
        val userEmail: String = "",
        val userEmailError: Pair<Boolean, Int> = Pair(false, R.string.sign_up_email_required),
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnStart -> onStart(event.userCompletedDialogDescription, event.linkWhatsapp, event.blockedMessage)
            is OnValidateForm -> isFormValid()
            is OnNextActionClick -> onNextActionClick(event.nextStepAction)
            is OnUserDataValidationSuccess -> onUserDataValidationSuccess(
                event.context,
                event.currentStep,
                event.userData,
                event.onUseDataValueChange,
                event.nextStepAction,
                event.openSignUpSplashComeBack,
                event.previousStepAction,
                event.onLoadingValueChange,
                event.onOpenDialog
            )
            is OnValidateUserEmail -> isUserEmailValid()
            is OnUserEmailValueChange -> onUserEmailValueChange(event.value)
        }
    }

    sealed class UIEvent {
        data class OnStart(
            val userCompletedDialogDescription: String,
            val linkWhatsapp: String,
            val blockedMessage: String
        ) : UIEvent()

        data class OnNextActionClick(val nextStepAction: () -> Unit) : UIEvent()
        data class OnUserDataValidationSuccess(
            val context: Context,
            val currentStep: Int,
            val userData: UserData?,
            val onUseDataValueChange: () -> Unit,
            val nextStepAction: () -> Unit,
            val openSignUpSplashComeBack: () -> Unit,
            val previousStepAction: () -> Unit,
            val onLoadingValueChange: () -> Unit,
            val onOpenDialog: (DialogParameters) -> Unit
        ) : UIEvent()

        data class OnUserEmailValueChange(val value: String) : UIEvent()

        object OnValidateForm : UIEvent()
        object OnValidateUserEmail : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }
}