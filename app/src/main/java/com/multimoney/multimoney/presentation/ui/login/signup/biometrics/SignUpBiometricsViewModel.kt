package com.multimoney.multimoney.presentation.ui.login.signup.biometrics

import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.util.BiometricHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpBiometricsViewModel @Inject constructor(
    val biometricHelper: BiometricHelper,
    private val dataStorePreferences: DataStorePreferences
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
            viewModelScope.launch {
                dataStorePreferences.setUserEmail(userEmail)
                dataStorePreferences.setUserPassword(userPassword, this@apply)
                dataStorePreferences.isBiometricsEnabled(true)
                onNavigateBack()
            }
        }
    }

    fun onNavigateBack() {
        popAndNavigateTo(
            route = Screen.SignInScreen.route,
            popTo = Screen.SignUpBiometricsScreen.route
        )
    }
}