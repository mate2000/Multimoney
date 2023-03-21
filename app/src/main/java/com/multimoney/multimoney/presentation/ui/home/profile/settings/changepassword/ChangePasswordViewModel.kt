package com.multimoney.multimoney.presentation.ui.home.profile.settings.changepassword

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.AuthException.InvalidPasswordException
import com.amplifyframework.auth.AuthException.NotAuthorizedException
import com.amplifyframework.core.Amplify
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.security.QueryValidationSecurityUseCase
import com.multimoney.domain.model.metrics.BaseEventDataDto
import com.multimoney.domain.model.security.ValidateSecurity
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.EMAIL
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.USER_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.ui.home.profile.settings.changepassword.ChangePasswordViewModel.UIEvent.OnNavigateToForgotPassword
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.getNavParam
import com.multimoney.multimoney.presentation.util.password.PasswordValidationHelper
import com.multimoney.multimoney.presentation.util.passwordHasALowercaseLetterValidation
import com.multimoney.multimoney.presentation.util.passwordHasANumberValidation
import com.multimoney.multimoney.presentation.util.passwordHasAUppercaseLetterValidation
import com.multimoney.multimoney.presentation.util.passwordHasMinimumCharacters
import com.multimoney.multimoney.presentation.util.passwordHasSpecialCharacterValidation
import com.multimoney.multimoney.presentation.util.toJson
import com.multimoney.multimoney.util.CognitoHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val savedStateHandle: SavedStateHandle,
    private val queryValidationSecurityUseCase: QueryValidationSecurityUseCase,
    private val cognitoHelper: CognitoHelper,
    private val mmCountDownTimer: MMCountDownTimer,
    private val passwordValidationHelper: PasswordValidationHelper
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set
    var onPasswordSaveEvents = MutableSharedFlow<MultimoneyResult<ValidateSecurity?>>()
    var onCognitoPasswordUpdateEvents = MutableSharedFlow<Pair<Boolean, Int>>()

    // Stateless
    private var previousScreen: String

    init {
        uiState = uiState.copy(
            idBrand = savedStateHandle[ID_BRAND] ?: 0,
            pkUser = savedStateHandle[PK_USER] ?: "",
            userName = savedStateHandle[USER_NAME] ?: "",
            email = savedStateHandle[EMAIL] ?: ""
        )
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
    }

    private fun validatePassword(isConfirmPassword: Boolean = false) {
        val password = if (isConfirmPassword) uiState.newPasswordConfirmation else uiState.newPassword

        uiState = uiState.copy(
            eightCharactersMinimumState = passwordHasMinimumCharacters(uiState.newPassword),
            oneUppercaseState = passwordHasAUppercaseLetterValidation(uiState.newPassword) && uiState.newPassword.isNotEmpty(),
            oneLowercaseState = passwordHasALowercaseLetterValidation(uiState.newPassword) && uiState.newPassword.isNotEmpty(),
            oneNumberState = passwordHasANumberValidation(uiState.newPassword) && uiState.newPassword.isNotEmpty(),
            oneCharacterState = passwordHasSpecialCharacterValidation(uiState.newPassword) && uiState.newPassword.isNotEmpty()
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
            uiState.copy(
                newPasswordConfirmationError = passwordValidationHelper.validateConsecutiveCharacter(
                    value = password,
                    password = uiState.newPassword,
                    confirmPassword = uiState.newPasswordConfirmation
                )
            )
        }

        resetValidationLabel(uiState.newPassword)
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
            (uiState.newPasswordConfirmation == uiState.newPassword) && !uiState.newPasswordConfirmationError.first && uiState.currentPassword.isNotEmpty()
    }

    private fun onNewPasswordValueChange(password: String?) {
        uiState = uiState.copy(newPassword = password.toString())
        validatePassword()
        uiState = uiState.copy(isButtonEnabled = isFormValid())
    }

    private fun onNewPasswordConfirmationValueChange(password: String?) {
        uiState = uiState.copy(newPasswordConfirmation = password.toString())
        validatePassword(isConfirmPassword = true)
        uiState = uiState.copy(isButtonEnabled = isFormValid())
    }

    private fun onCurrentPasswordValueChange(password: String?) {
        uiState = uiState.copy(
            currentPassword = password.toString(),
            currentPasswordError = Pair(false, R.string.empty)
        )
        uiState = uiState.copy(isButtonEnabled = isFormValid())
    }

    private fun cleanErrors() {
        uiState = uiState.copy(
            newPasswordConfirmationError = Pair(false, R.string.empty),
            newPasswordError = Pair(false, R.string.empty),
            currentPasswordError = Pair(false, R.string.empty)
        )
    }

    private fun onUpdatePassword() = executeUseCase {
        cleanErrors()
        onUpdateLoadingState(true)
        queryValidationSecurityUseCase.invoke(
            idBrand = uiState.idBrand,
            pkUser = uiState.pkUser,
            user = uiState.userName,
            password = uiState.newPassword
        ).collectLatest { result ->
            onPasswordSaveEvents.emit(result)
        }
    }

    private fun updateCognitoStatus(message: Int) = executeUseCase {
        onCognitoPasswordUpdateEvents.emit(Pair(true, message))
    }

    private fun onCallCognitoUpdatePassword() = executeUseCase {
        Amplify.Auth.updatePassword(
            uiState.currentPassword,
            uiState.newPassword,
            {
                registerAdjustEvent(AdjustEventType.SETTINGS_CHANGE_PASSWORD_SUCCESS_8002, applyAdjust = false, data = BaseEventDataDto(user = uiState.userName, idBrand = uiState.idBrand).toJson())
                updateCognitoStatus(
                    R.string.profile_settings_password_modified
                )
                uiState = uiState.copy(isLoading = false)
            },
            {
                uiState = uiState.copy(isLoading = false)
                uiState = when (it) {
                    is NotAuthorizedException -> {
                        uiState.copy(
                            currentPasswordError = Pair(
                                true,
                                R.string.profile_settings_error_wrong_current_password
                            )
                        )
                    }
                    is InvalidPasswordException -> {
                        uiState.copy(
                            newPasswordConfirmationError = Pair(
                                true,
                                R.string.profile_settings_error_new_password_invalid
                            ),
                            newPasswordError = Pair(true, R.string.empty)
                        )
                    }
                    else -> {
                        uiState.copy(
                            isAlertResultVisible = true,
                            alertResultTitle = R.string.profile_settings_error_we_could_not_change_your_password,
                            alertResultDescription = R.string.profile_settings_error_we_are_sorry_try_again_later
                        )
                    }
                }
            }
        )
    }

    private fun onPasswordSameAsPrevious() {
        uiState = uiState.copy(
            newPasswordConfirmationError = Pair(
                true,
                R.string.profile_settings_error_password_must_not_be_the_same
            )
        )
    }

    private fun onUpdateLocallyStoredPassword() {
        viewModelScope.launch {
            dataStorePreferences.isBiometricsEnabled(false)
        }
    }

    private fun onUpdateLoadingState(state: Boolean) {
        uiState = uiState.copy(isLoading = state)
    }

    private fun onShowAlertDialog() {
        uiState = uiState.copy(isAlertResultVisible = true, isLoading = false)
    }

    private fun onNavigateToForgotPassword() = navigateTo(
        route = Screen.RequestForgotPassword.baseRoute
            .plus(
                getNavParam(PREVIOUS_SCREEN, Screen.ProfileChangePasswordScreen.route)
            )
    )

    private fun onAlertButtonClick() = when (previousScreen) {
        Screen.ProfileSettingsScreen.baseRoute -> navigateBack(Screen.HomeScreen.route, isRestart = true)
        else -> signOut()
    }

    private fun onNavigateBack() = when (previousScreen) {
        Screen.ProfileSettingsScreen.baseRoute -> navigateBack(Screen.ProfileSettingsScreen.route, false)
        else -> signOut()
    }

    private fun signOut() {
        cognitoHelper.signOut(signOutError = {
            Timber.d("SignOut Error")
        })
        viewModelScope.launch {
            dataStorePreferences.setAuthToken("")
        }
        mmCountDownTimer.discardTimer()
        navigateBack(Screen.SignInScreen.route, isRestart = false)
    }

    private fun onValidatePasswordStructure() = executeUseCase {
        passwordValidationHelper.getValidatePasswordStructure(
            pkUser = uiState.pkUser.toInt(),
            user = uiState.email,
            idBrand = uiState.idBrand
        )
    }

    fun getForbiddenWords(value: String): String = passwordValidationHelper.getForbiddenWords(value)

    data class UIState(
        // Fields
        val currentPassword: String = "",
        val newPassword: String = "",
        val newPasswordConfirmation: String = "",
        val currentPasswordError: Pair<Boolean, Int> = Pair(
            false,
            R.string.sign_up_otp_code_not_valid
        ),
        val newPasswordError: Pair<Boolean, Int> = Pair(false, R.string.sign_up_otp_code_not_valid),
        val newPasswordConfirmationError: Pair<Boolean, Int> = Pair(
            false,
            R.string.sign_up_otp_code_not_valid
        ),
        val isButtonEnabled: Boolean = false,
        val idBrand: Int = 0,
        val pkUser: String = "",
        val userName: String = "",
        val email: String = "",
        val isLoading: Boolean = false,

        var eightCharactersMinimumState: Boolean? = null,
        var oneUppercaseState: Boolean? = null,
        var oneLowercaseState: Boolean? = null,
        var oneNumberState: Boolean? = null,
        var oneCharacterState: Boolean? = null,
        var isAlertResultVisible: Boolean = false,
        var alertResultTitle: Int = R.string.empty,
        var alertResultDescription: Int = R.string.empty
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnNewPasswordValueChange -> onNewPasswordValueChange(event.password)
            is UIEvent.OnNewPasswordConfirmationValueChange -> onNewPasswordConfirmationValueChange(
                event.password
            )
            is UIEvent.OnCurrentPasswordValueChange -> onCurrentPasswordValueChange(event.password)
            is UIEvent.OnUpdatePassword -> onUpdatePassword()
            is UIEvent.OnCallCognitoUpdatePassword -> onCallCognitoUpdatePassword()
            is UIEvent.OnPasswordSameAsPrevious -> onPasswordSameAsPrevious()
            is UIEvent.OnUpdateLocallyStoredPassword -> onUpdateLocallyStoredPassword()
            is UIEvent.OnUpdateLoadingState -> onUpdateLoadingState(event.state)
            is UIEvent.OnShowAlertDialog -> onShowAlertDialog()
            is UIEvent.OnAlertButtonClick -> onAlertButtonClick()
            is OnNavigateToForgotPassword -> onNavigateToForgotPassword()
            is UIEvent.OnValidatePasswordStructure -> onValidatePasswordStructure()
        }
    }

    sealed class UIEvent {
        data class OnNewPasswordValueChange(
            val password: String?
        ) : UIEvent()

        data class OnNewPasswordConfirmationValueChange(
            val password: String?
        ) : UIEvent()

        data class OnCurrentPasswordValueChange(
            val password: String?
        ) : UIEvent()

        data class OnUpdateLoadingState(
            val state: Boolean
        ) : UIEvent()

        object OnNavigateBack : UIEvent()
        object OnAlertButtonClick : UIEvent()
        object OnPasswordSameAsPrevious : UIEvent()
        object OnShowAlertDialog : UIEvent()
        object OnCallCognitoUpdatePassword : UIEvent()
        object OnUpdateLocallyStoredPassword : UIEvent()
        object OnUpdatePassword : UIEvent()
        object OnNavigateToForgotPassword : UIEvent()
        object OnValidatePasswordStructure: UIEvent()
    }

    companion object {
        const val VALID_PASSWORD = 0
    }
}
