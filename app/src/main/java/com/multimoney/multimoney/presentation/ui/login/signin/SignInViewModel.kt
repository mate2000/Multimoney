package com.multimoney.multimoney.presentation.ui.login.signin

import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.util.isEmailValid
import com.multimoney.multimoney.util.BiometricHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    val biometricHelper: BiometricHelper,
    val dataStorePreferences: DataStorePreferences
) : BaseViewModel() {

    // Interactions
    var isSignInEnabled by mutableStateOf(false)
    var biometricPromptTitle = ""
    var biometricPromptDescription = ""
    var biometricPromptNegative = ""

    // Fields
    var userEmail by mutableStateOf("")
    var userEmailError by mutableStateOf(Pair(false, R.string.error_empty))
    var userPassword by mutableStateOf("")
    var userPasswordError by mutableStateOf(Pair(false, R.string.error_empty))
    var userName by mutableStateOf<String?>(null)
    var isFingerprintChecked by mutableStateOf(false)
    var successMessage by mutableStateOf("")

    var biometricErrorDialog by mutableStateOf(Pair(mutableStateOf(false), ""))
    var configureBiometric by mutableStateOf(false)
    var biometricError by mutableStateOf(false)

    fun signIn() {
        isLoading = true
        clearUserEmailError()
        Amplify.Auth.signIn(userEmail, userPassword, {
            if (it.isSignInComplete) {
                Amplify.Auth.fetchAuthSession({ authSessionSuccess ->
                    val session = authSessionSuccess as AWSCognitoAuthSession
                    when (session.identityId.type) {
                        AuthSessionResult.Type.SUCCESS -> {
                            successMessage = session.identityId.value ?: ""
                            isLoading = false
                            if (isFingerprintChecked) {
                                configureBiometric = true
                            } else {
                                navigateToHome()
                            }
                        }
                        AuthSessionResult.Type.FAILURE -> cognitoError()
                    }
                }, {
                    cognitoError()
                })
            } else {
                cognitoError()
            }
        }, {
            cognitoError()
        })
    }

    fun isFormValid() {
        isSignInEnabled = when {
            userEmail.isBlank() -> false
            isEmailValid(userEmail).not() -> false
            userPassword.isBlank() -> false
            else -> true
        }
    }

    fun isUserEmailValid() {
        if (isEmailValid(userEmail).not()) {
            userEmailError = Pair(true, R.string.sign_in_email_not_valid)
        }
    }

    fun clearUserEmailError() {
        userEmailError = Pair(false, R.string.error_empty)
        if (userPasswordError.second == R.string.sign_in_validation) {
            userPasswordError = Pair(false, R.string.error_empty)
        }
    }

    fun clearUserPasswordError() {
        if (userPasswordError.second == R.string.sign_in_validation) {
            userEmailError = Pair(false, R.string.error_empty)
            userPasswordError = Pair(false, R.string.error_empty)
        }
    }

    private fun cognitoError() {
        userEmailError = Pair(true, R.string.error_empty)
        userPasswordError = Pair(true, R.string.sign_in_validation)
        isLoading = false
    }

    fun biometricPromptError(errorCode: Int, errString: CharSequence) {
        if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
            biometricError = true
            biometricErrorDialog = Pair(mutableStateOf(true), errString.toString())
        }
    }

    fun biometricPromptConfigurationError(errorCode: Int, errString: CharSequence) {
        if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
            navigateToHome()
        } else {
            // TODO: Pending logic by business
        }
    }

    fun biometricPromptForEncryptionSuccess(result: BiometricPrompt.AuthenticationResult) {
        result.cryptoObject?.cipher?.apply {
            viewModelScope.launch {
                dataStorePreferences.setUserEmail(userEmail)
                dataStorePreferences.setUserPassword(userPassword, this@apply)
                dataStorePreferences.isBiometricsEnabled(true)
                navigateToHome()
            }
        }
    }

    fun biometricPromptForDecryptionSuccess(result: BiometricPrompt.AuthenticationResult) {
        result.cryptoObject?.cipher?.apply {
            viewModelScope.launch {
                userPassword = dataStorePreferences.getUserPassword(this@apply).first()
                signIn()
            }
        }
    }

    private fun navigateToHome() = popAndNavigateTo(
        route = Screen.HomeScreen.route,
        popTo = Screen.SignInScreen.route
    )
}