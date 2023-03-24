package com.multimoney.multimoney.presentation.ui.login.forgotpassword.process

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.SavedStateHandle
import com.amplifyframework.core.Amplify
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.security.QueryValidationSecurityUseCase
import com.multimoney.domain.model.metrics.EmailDto
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.process.ProcessForgotPasswordViewModel.BaseEvent.OnResendOtpToastEvent
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.process.ProcessForgotPasswordViewModel.UIEvent.OnAlertButtonClick
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.process.ProcessForgotPasswordViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.process.ProcessForgotPasswordViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.process.ProcessForgotPasswordViewModel.UIEvent.OnNewPasswordConfirmationValueChange
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.process.ProcessForgotPasswordViewModel.UIEvent.OnNewPasswordValueChange
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.process.ProcessForgotPasswordViewModel.UIEvent.OnOtpValueChange
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.process.ProcessForgotPasswordViewModel.UIEvent.OnResendOtpClick
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.process.ProcessForgotPasswordViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.catalog.ValidationSecurityPassword.None
import com.multimoney.multimoney.presentation.util.catalog.ValidationSecurityPassword.OnlyValidate
import com.multimoney.multimoney.presentation.util.password.PasswordValidationHelper
import com.multimoney.multimoney.presentation.util.passwordHasALowercaseLetterValidation
import com.multimoney.multimoney.presentation.util.passwordHasANumberValidation
import com.multimoney.multimoney.presentation.util.passwordHasAUppercaseLetterValidation
import com.multimoney.multimoney.presentation.util.passwordHasMinimumCharacters
import com.multimoney.multimoney.presentation.util.passwordHasSpecialCharacterValidation
import com.multimoney.multimoney.presentation.util.toJson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class ProcessForgotPasswordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val queryValidationSecurityUseCase: QueryValidationSecurityUseCase,
    private val passwordValidationHelper: PasswordValidationHelper
) : BaseViewModel(false) {

    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var email: String = ""
    private var idBrand: Int = 0
    private var pkUser: String = ""
    private var previousScreen: String? = null
    private var otp: String = ""

    init {
        email = savedStateHandle[EMAIL] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        pkUser = savedStateHandle[PK_USER] ?: ""
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
    }

    private fun onStart() {
        uiState = when (idBrand) {
            Brand.CostaRica.id -> {
                uiState.copy(titleResource = R.string.process_forgot_password_title)
            }
            else -> {
                uiState.copy(titleResource = R.string.process_forgot_password_title_sv)
            }
        }
    }

    private fun onCloseClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        navigateBack(popTo = previousScreen ?: "", isRestart = false)
    }

    private fun validatePassword(isConfirmPassword: Boolean = false) {
        val password = if (isConfirmPassword) uiState.newPasswordConfirmation else uiState.newPassword

        uiState = uiState.copy(
            eightCharactersMinimumState = passwordHasMinimumCharacters(uiState.newPassword),
            oneUppercaseState = passwordHasAUppercaseLetterValidation(uiState.newPassword) && uiState.newPassword.isNotEmpty(),
            oneLowercaseState = passwordHasALowercaseLetterValidation(uiState.newPassword) && uiState.newPassword.isNotEmpty(),
            oneNumberState = passwordHasANumberValidation(uiState.newPassword) && uiState.newPassword.isNotEmpty(),
            oneCharacterState = passwordHasSpecialCharacterValidation(uiState.newPassword) && uiState.newPassword.isNotEmpty(),
            newPasswordConfirmationError = validatePasswordAreTheSame()
        )

        uiState = if (!isConfirmPassword) {
            uiState.copy(
                newPasswordError = passwordValidationHelper.validateConsecutiveCharacter(
                    value = password,
                    password = uiState.newPassword,
                    confirmPassword = uiState.newPasswordConfirmation
                )
            )
        } else {
            val errorMessage = passwordValidationHelper.validateConsecutiveCharacter(
                value = password,
                password = uiState.newPassword,
                confirmPassword = uiState.newPasswordConfirmation
            )
            uiState.copy(
                newPasswordConfirmationError = Triple(errorMessage.first, errorMessage.second, "")
            )
        }

        resetValidationLabel(uiState.newPassword)
    }

    private fun validatePasswordAreTheSame() =
        if (uiState.newPassword.isNotEmpty() && uiState.newPasswordConfirmation.isNotEmpty() && uiState.newPasswordConfirmation != uiState.newPassword) {
            Triple(true, R.string.process_forgot_password_new_password_confirmation_error, "")
        } else {
            Triple(false, string.error_empty, "")
        }

    private fun resetValidationLabel(password: String) {
        if (password.isEmpty()) {
            uiState = uiState.copy(
                eightCharactersMinimumState = null,
                oneUppercaseState = null,
                oneLowercaseState = null,
                oneNumberState = null,
                oneCharacterState = null
            )
        }
    }

    private fun isFormValid(): Boolean {
        return uiState.oneLowercaseState ?: false && uiState.oneUppercaseState ?: false && uiState.oneNumberState ?: false &&
                uiState.oneCharacterState ?: false && passwordHasMinimumCharacters(uiState.newPassword) &&
                (uiState.newPasswordConfirmation == uiState.newPassword) && !uiState.newPasswordConfirmationError.first && otp.trim()
            .isNotEmpty() && otp.trim().length == OTP_TOTAL_DIGITS
    }

    private fun onOtpValueChange(value: String) {
        otp = value
        uiState = uiState.copy(
            otp = value,
            isFormValid = isFormValid()
        )
    }

    private fun onNewPasswordValueChange(password: String?) {
        uiState = uiState.copy(newPassword = password.toString())
        validatePassword()
        uiState = uiState.copy(isFormValid = isFormValid())
    }

    private fun onNewPasswordConfirmationValueChange(password: String?) {
        uiState = uiState.copy(newPasswordConfirmation = password.toString())
        validatePassword(isConfirmPassword = true)
        uiState = uiState.copy(isFormValid = isFormValid())
    }

    private fun cleanErrors() {
        uiState = uiState.copy(
            newPasswordConfirmationError = Triple(false, string.empty, ""),
            newPasswordError = Pair(false, string.empty)
        )
    }

    private fun onCallQueryValidationSecurityUseCase() = executeUseCase {
        cleanErrors()
        queryValidationSecurityUseCase.invoke(
            idBrand = idBrand,
            pkUser = pkUser,
            user = email,
            password = uiState.newPassword,
            actionSecurity = OnlyValidate.actionSecurity
        ).collectLatest { result ->
            result.onSuccess {
                onConfirmResetPassword()
            }.onMessage {
                onPasswordSameAsPrevious(it?.messageError?.message)
            }.onFailure {
                onAlertFailure()
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun callSavePassword() = executeUseCase {
        queryValidationSecurityUseCase.invoke(
            idBrand = idBrand,
            pkUser = pkUser,
            user = email,
            password = uiState.newPassword,
            actionSecurity = None.actionSecurity
        ).collectLatest { result ->
            result.onSuccess {
                successResetPassword()
            }.onMessage {
                successResetPassword()
            }.onFailure {
                successResetPassword()
            }
        }
    }

    private fun onConfirmResetPassword() = Amplify.Auth.confirmResetPassword(uiState.newPassword, uiState.otp, {
        callSavePassword()
    }, {
        onAlertFailure(it.message == OTP_ERROR_MESSAGE)
    })

    private fun successResetPassword() {
        registerAdjustEvent(
            AdjustEventType.FORGOT_CONFIRM_CORRECT_PASSWORD_4001,
            isLoggedIn = false,
            applyAdjust = false,
            data = EmailDto(email).toJson()
        )
        registerAdjustEvent(
            AdjustEventType.FORGOT_SUCCESS_4004,
            isLoggedIn = false,
            applyAdjust = false,
            data = EmailDto(email).toJson()
        )
        onAlertSuccess()
    }

    private fun onAlertSuccess() {
        uiState = uiState.copy(
            isAlertResultVisible = true,
            alertResultIconResource = R.drawable.ic_success_symbol,
            alertResultTitleResource = R.string.process_forgot_password_alert_success_title,
            alertResultDescriptionResource = R.string.process_forgot_password_alert_success_description,
            alertResultButtonTextResource = R.string.common_go_home,
            isLoading = false
        )
    }

    private fun onAlertFailure(isOtpFailure: Boolean = false) {
        uiState = uiState.copy(
            isAlertResultVisible = true,
            alertResultIconResource = R.drawable.ic_error_symbol,
            alertResultTitleResource = R.string.process_forgot_password_alert_failure_title,
            alertResultDescriptionResource = if (idBrand == Brand.CostaRica.id) {
                R.string.process_forgot_password_alert_failure_description
            } else {
                R.string.process_forgot_password_alert_failure_description_sv
            },
            alertResultButtonTextResource = if (isOtpFailure) {
                R.string.process_forgot_password_alert_failure_button_try_again
            } else {
                R.string.common_go_home
            },
            isAlertResultOtpFailure = isOtpFailure,
            isLoading = false
        )
    }

    private fun onPasswordSameAsPrevious(errorMessage: String?) {
        uiState = uiState.copy(
            isLoading = false,
            newPasswordConfirmationError = Triple(
                true,
                string.profile_settings_error_password_must_not_be_the_same,
                errorMessage.orEmpty()
            )
        )
    }

    private fun onContinueClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        if (idBrand != Brand.Default.id) {
            onCallQueryValidationSecurityUseCase()
        } else {
            onAlertFailure()
        }
    }

    private fun onResendOtpClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        registerAdjustEvent(
            AdjustEventType.FORGOT_RESEND_OTP_4002,
            isLoggedIn = false,
            applyAdjust = false,
            data = EmailDto(email).toJson()
        )
        if (idBrand == Brand.Default.id) {
            emitBaseEvent(OnResendOtpToastEvent)
        } else {
            Amplify.Auth.resetPassword(
                email,
                {
                    emitBaseEvent(OnResendOtpToastEvent)
                },
                {
                    emitBaseEvent(OnResendOtpToastEvent)
                }
            )
        }
    }

    private fun onAlertButtonClick(focusManager: FocusManager) {
        if (uiState.isAlertResultOtpFailure) {
            otp = ""
            uiState = uiState.copy(
                isAlertResultVisible = false,
                isAlertResultOtpFailure = false,
                otp = "",
                isFormValid = isFormValid()
            )
        } else {
            onCloseClick(focusManager)
        }
    }

    private fun onValidatePasswordStructure() = executeUseCase {
        val userPk: Int = if (pkUser.isNotBlank()) {
            pkUser.toInt()
        } else {
            0
        }
        passwordValidationHelper.getValidatePasswordStructure(idBrand, userPk, email)
    }

    fun getForbiddenWords(value: String): String = passwordValidationHelper.getForbiddenWords(value)

    data class UIState(
        // Interactions
        val titleResource: Int = R.string.empty,
        val otp: String = "",
        val newPassword: String = "",
        val newPasswordConfirmation: String = "",
        val newPasswordError: Pair<Boolean, Int> = Pair(false, R.string.sign_up_otp_code_not_valid),
        val newPasswordConfirmationError: Triple<Boolean, Int, String> = Triple(
            false,
            R.string.sign_up_otp_code_not_valid,
            ""
        ),
        var eightCharactersMinimumState: Boolean? = null,
        var oneUppercaseState: Boolean? = null,
        var oneLowercaseState: Boolean? = null,
        var oneNumberState: Boolean? = null,
        var oneCharacterState: Boolean? = null,
        val alertResultIconResource: Int = R.drawable.ic_success_symbol,
        val alertResultTitleResource: Int = R.string.empty,
        val alertResultDescriptionResource: Int = R.string.empty,
        val alertResultButtonTextResource: Int = R.string.empty,
        val isFormValid: Boolean = false,
        val isLoading: Boolean = false,
        val isAlertResultVisible: Boolean = false,
        val isAlertResultOtpFailure: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnCloseClick -> onCloseClick(uiEvent.focusManager)
            is OnAlertButtonClick -> onAlertButtonClick(uiEvent.focusManager)
            is OnContinueClick -> onContinueClick(uiEvent.focusManager)
            is OnOtpValueChange -> onOtpValueChange(uiEvent.value)
            is OnResendOtpClick -> onResendOtpClick(uiEvent.focusManager)
            is OnNewPasswordValueChange -> onNewPasswordValueChange(uiEvent.value)
            is OnNewPasswordConfirmationValueChange -> onNewPasswordConfirmationValueChange(uiEvent.value)
            is OnStart -> onStart()
            is UIEvent.OnValidatePasswordStructure -> onValidatePasswordStructure()
        }
    }

    sealed class UIEvent {
        data class OnCloseClick(val focusManager: FocusManager) : UIEvent()
        data class OnAlertButtonClick(val focusManager: FocusManager) : UIEvent()
        data class OnContinueClick(val focusManager: FocusManager) : UIEvent()
        data class OnOtpValueChange(val value: String) : UIEvent()
        data class OnResendOtpClick(val focusManager: FocusManager) : UIEvent()
        data class OnNewPasswordValueChange(val value: String) : UIEvent()
        data class OnNewPasswordConfirmationValueChange(val value: String) : UIEvent()
        object OnStart : UIEvent()
        object OnValidatePasswordStructure : UIEvent()
    }

    sealed class BaseEvent {
        object OnResendOtpToastEvent : BaseEvent()
    }

    companion object {
        const val OTP_TOTAL_DIGITS = 6
        const val OTP_ERROR_MESSAGE = "Confirmation code entered is not correct."
    }
}
