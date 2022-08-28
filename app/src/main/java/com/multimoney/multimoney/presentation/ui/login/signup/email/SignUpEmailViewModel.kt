package com.multimoney.multimoney.presentation.ui.login.signup.email

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.data.util.catalog.UserStatus
import com.multimoney.domain.interaction.security.QueryValidateUserExistsUseCase
import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.email.SignUpEmailViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.login.signup.email.SignUpEmailViewModel.UIEvent.OnHandleUserStatus
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
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class SignUpEmailViewModel @Inject constructor(
    private val queryValidateUserExistsUseCase: QueryValidateUserExistsUseCase,
    // private val mutationUserValidationUseCase: MutationUserValidationUseCase
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
    // val onUserDataValidationEvent = MutableSharedFlow<MultimoneyResult<UserData?>>()
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

//    private fun callMutationUserValidationUseCase(email: String, nextStep: String, idBrand: Int) =
//        executeUseCase {
//            mutationUserValidationUseCase(
//                email = email,
//                currentStep = nextStep,
//                idBrand = idBrand
//            ).collectLatest { result ->
//                onUserDataValidationEvent.emit(result)
//            }
//        }

    private fun callQueryValidationUserExistsUseCase(email: String, nextStep: String, idBrand: Int) =
        executeUseCase {
            queryValidateUserExistsUseCase(
                email = email,
                currentStep = nextStep,
                idBrand = idBrand
            ).collectLatest { result ->
                onValidateUserExistsEvent.emit(result)
            }
        }

    private fun onNextActionClick(nextStepAction: () -> Unit, idBrand: Int) {
        if (isDataChanged() || isUserStatusIncomplete.not()) {
            callQueryValidationUserExistsUseCase(
                uiState.userEmail,
                SignUpStep.Two.name,
                idBrand
            )
        } else {
            nextStepAction.invoke()
        }
    }

    private fun onValidationUserExistsSuccess(
        currentStep: Int,
        userData: UserData?,
        onUseDataValueChange: () -> Unit,
        nextStepAction: () -> Unit,
        openSignUpSplashComeBack: () -> Unit,
        onLoadingValueChange: () -> Unit,
    ) {
        previousUserEmail = userData?.email ?: ""
        onLoadingValueChange()
        onUseDataValueChange()
        if (userData?.status == UserStatus.Incomplete.status) {
            isUserStatusIncomplete = true
            val step = SignUpStep.Search.getIdByName(userData.currentStep)
            if (step == currentStep || step < STEP_TO_SHOW_SPLASH) {
                nextStepAction()
            } else {
                openSignUpSplashComeBack()
            }
        }
    }

    private fun onHandleUserState(
        context: Context,
        userData: UserData?,
        previousStepAction: () -> Unit,
        onLoadingValueChange: () -> Unit,
        onOpenDialog: (DialogParameters) -> Unit,
    ) {
        onLoadingValueChange()
        if (userData?.status == UserStatus.Active.status) {
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
        } else {
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
            is OnNextActionClick -> onNextActionClick(event.nextStepAction, event.idBrand)
            is OnUserDataValidationSuccess -> onValidationUserExistsSuccess(
                event.currentStep,
                event.userData,
                event.onUseDataValueChange,
                event.nextStepAction,
                event.openSignUpSplashComeBack,
                event.onLoadingValueChange
            )
            is OnHandleUserStatus -> onHandleUserState(
                event.context,
                event.userData,
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

        data class OnNextActionClick(val nextStepAction: () -> Unit, val idBrand: Int) : UIEvent()
        data class OnUserDataValidationSuccess(
            val currentStep: Int,
            val userData: UserData?,
            val onUseDataValueChange: () -> Unit,
            val nextStepAction: () -> Unit,
            val openSignUpSplashComeBack: () -> Unit,
            val onLoadingValueChange: () -> Unit,
        ) : UIEvent()

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
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    companion object {
        private const val STEP_TO_SHOW_SPLASH = 3
    }
}