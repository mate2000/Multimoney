package com.multimoney.multimoney.presentation.ui.home.profile.settings.changepassword

import android.provider.Contacts.Intents.UI
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.amplifyframework.auth.AuthException.InvalidPasswordException
import com.amplifyframework.auth.AuthException.NotAuthorizedException
import com.amplifyframework.auth.AuthException.SignedOutException
import com.amplifyframework.core.Amplify
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.security.QueryValidationSecurityUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.USER_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.ui.login.signup.password.SignUpPasswordViewModel
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.noMoreThanThreeConsecutiveLetterOrNumber
import com.multimoney.multimoney.presentation.util.noMoreThanThreeEqualConsecutiveLetterOrNumber
import com.multimoney.multimoney.presentation.util.noMoreThanThreeLettersOrNumbers
import com.multimoney.multimoney.presentation.util.passwordHasALowercaseLetterValidation
import com.multimoney.multimoney.presentation.util.passwordHasANumberValidation
import com.multimoney.multimoney.presentation.util.passwordHasAUppercaseLetterValidation
import com.multimoney.multimoney.presentation.util.passwordHasMinimumCharacters
import com.multimoney.multimoney.presentation.util.passwordHasSpecialCharacterValidation
import com.multimoney.multimoney.util.CognitoHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val savedStateHandle: SavedStateHandle,
    private val cognitoHelper: CognitoHelper,
    private val countDownTimer: MMCountDownTimer,
    private val queryValidationSecurityUseCase: QueryValidationSecurityUseCase
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    init {
        uiState = uiState.copy(
            idBrand = savedStateHandle[ID_BRAND] ?: 0,
            pkUser = savedStateHandle[PK_USER] ?: "",
            userName = savedStateHandle[USER_NAME] ?: ""
        )
    }


    private fun validatePassword() {
        uiState = uiState.copy(
            eightCharactersMinimumState = passwordHasMinimumCharacters(uiState.newPassword),
            oneUppercaseState = passwordHasAUppercaseLetterValidation(uiState.newPassword) && uiState.newPassword.isNotEmpty(),
            oneLowercaseState = passwordHasALowercaseLetterValidation(uiState.newPassword) && uiState.newPassword.isNotEmpty(),
            oneNumberState = passwordHasANumberValidation(uiState.newPassword) && uiState.newPassword.isNotEmpty(),
            oneCharacterState = passwordHasSpecialCharacterValidation(uiState.newPassword) && uiState.newPassword.isNotEmpty(),
            newPasswordConfirmationError = validateHasTheSameConsecutiveCharacter()
        )
        resetValidationLabel(uiState.newPassword)
    }

    private fun validateHasTheSameConsecutiveCharacter(): Pair<Boolean, Int> {
        return when {
            noMoreThanThreeEqualConsecutiveLetterOrNumber(uiState.newPassword) -> {
                Pair(
                    true,
                    R.string.sign_up_password_requirement_max_three_characters_or_number_consecutive
                )
            }
            noMoreThanThreeConsecutiveLetterOrNumber(uiState.newPassword) -> {
                Pair(
                    true,
                    R.string.sign_up_password_requirement_max_three_characters_or_number_consecutive
                )
            }
            noMoreThanThreeLettersOrNumbers(uiState.newPassword) -> {
                Pair(
                    true,
                    R.string.sign_up_password_requirement_max_three_characters_or_number_consecutive
                )
            }
            (uiState.newPassword.isNotEmpty() && uiState.newPasswordConfirmation.isNotEmpty() && uiState.newPasswordConfirmation != uiState.newPassword) -> {
                Pair(true, R.string.sign_up_password_confirm_password_error)
            }
            else -> {
                Pair(false, R.string.error_empty)
            }
        }
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
                uiState.oneCharacterState ?: false && passwordHasMinimumCharacters(uiState.newPassword)
                && (uiState.newPasswordConfirmation == uiState.newPassword) && !uiState.newPasswordConfirmationError.first
    }

    private fun onNewPasswordValueChange(password: String?) {
        uiState = uiState.copy(newPassword = password.toString())
        validatePassword()
        uiState = uiState.copy(isButtonEnabled = isFormValid())
    }

    private fun onNewPasswordConfirmationValueChange(password: String?) {
        uiState = uiState.copy(newPasswordConfirmation = password.toString())
        validatePassword()
        uiState = uiState.copy(isButtonEnabled = isFormValid())
    }

    private fun onCurrentPasswordValueChange(password: String?) {
        uiState = uiState.copy(currentPassword = password.toString())
    }

    private fun onUpdatePassword() = executeUseCase {

        queryValidationSecurityUseCase.invoke(
            idBrand = uiState.idBrand,
            pkUser = uiState.pkUser,
            user = uiState.userName,
            password = uiState.newPassword
        ).collectLatest { result ->
            result.onSuccess {
                when (it?.messageError?.status) {
                    VALID_PASSWORD -> {
                        Log.e("TAG", "contrasena cambiada")
                        updatePasswordAmplify()
                    }
                    else -> {
                        uiState = uiState.copy(isLoading = false)
                        Log.e("TAG", "ya usada")
                    }
                }

            }.onFailure {
                uiState = uiState.copy(isLoading = false)
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }.onMessage {
                uiState = uiState.copy(isLoading = false)
            }
        }

    }

    private fun updatePasswordAmplify() {
        Amplify.Auth.updatePassword(uiState.currentPassword, uiState.newPassword,
            {
                Log.e("TAG", "clave actualizada")
                uiState = uiState.copy(isLoading = false)
            }, {
                uiState = uiState.copy(isLoading = false)
                when (it) {
                    is NotAuthorizedException -> {
                        Log.e("tag", "no autorizado")
                    }
                    is InvalidPasswordException -> {
                        Log.e("tag", "clave invalida")
                    }
                    else -> {
                        Log.e("tag", "something went wrong ${it}")
                    }
                }
            })
    }

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
        val isLoading : Boolean = false,

        var eightCharactersMinimumState: Boolean? = null,
        var oneUppercaseState: Boolean? = null,
        var oneLowercaseState: Boolean? = null,
        var oneNumberState: Boolean? = null,
        var oneCharacterState: Boolean? = null,
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is UIEvent.OnNewPasswordValueChange -> onNewPasswordValueChange(event.password)
            is UIEvent.OnNewPasswordConfirmationValueChange -> onNewPasswordConfirmationValueChange(
                event.password
            )
            is UIEvent.OnCurrentPasswordValueChange -> onCurrentPasswordValueChange(event.password)
            is UIEvent.OnUpdatePassword -> onUpdatePassword()
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

        object OnNavigateBack : UIEvent()
        object OnUpdatePassword : UIEvent()
    }

    companion object {
        const val VALID_PASSWORD = 0
    }

}