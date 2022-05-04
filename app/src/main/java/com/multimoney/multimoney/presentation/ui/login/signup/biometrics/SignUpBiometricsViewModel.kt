package com.multimoney.multimoney.presentation.ui.login.signup.biometrics

import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.util.BiometricHelper
import com.multimoney.multimoney.util.cryptography.CryptographyManagerImpl
import com.multimoney.multimoney.util.cryptography.UserCredentials
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpBiometricsViewModel @Inject constructor(
    val biometricHelper: BiometricHelper,
    private val cryptographyManagerImpl: CryptographyManagerImpl
) : BaseViewModel() {

    var userEmail by mutableStateOf("")
    var userPassword by mutableStateOf("")
    var biometricPromptTitle = ""
    var biometricPromptSubtitle = ""
    var biometricPromptDescription = ""
    var biometricPromptNegative = ""

    fun biometricPromptError(errorCode: Int, errString: CharSequence) {
        onNavigateBack()
    }

    fun biometricPromptForEncryptionSuccess(result: BiometricPrompt.AuthenticationResult) {
        result.cryptoObject?.cipher?.apply {
            cryptographyManagerImpl.persistCiphertextWrapperToPreferences(
                cryptographyManagerImpl.encryptData(
                    UserCredentials(userEmail, userPassword), this
                )
            )
        }
        onNavigateBack()
    }

    fun onNavigateBack() {
        popAndNavigateTo(
            route = Screen.SignInScreen.route,
            popTo = Screen.SignUpBiometricsScreen.route
        )
    }
}