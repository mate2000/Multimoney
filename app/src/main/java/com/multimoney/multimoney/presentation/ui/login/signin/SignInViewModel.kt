package com.multimoney.multimoney.presentation.ui.login.signin

import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnCallCognitoSignIn
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnFingerprintCheckedChanged
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnInitializeBiometricPrompt
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnShowBiometricPromptForDecryption
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnShowBiometricPromptForEncryption
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnShowBiometricSignInChanged
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnUserEmailValueChange
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnUserPasswordValueChange
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnValidateUserEmail
import com.multimoney.multimoney.presentation.util.isEmailValid
import com.multimoney.multimoney.util.BiometricHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    val biometricHelper: BiometricHelper,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel() {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var biometricPromptTitle = ""
    private var biometricPromptDescription = ""
    private var biometricPromptNegative = ""

    private fun onStart() {
        viewModelScope.launch {
            val isBiometricActive = dataStorePreferences.isBiometricsEnabled().first()
            uiState = uiState.copy(
                userEmail = dataStorePreferences.getUserEmail().first(),
                isBiometricActive = isBiometricActive,
                showBiometricSignIn = isBiometricActive
            )
        }
    }

    private fun callCognitoSignIn() {
        uiState = uiState.copy(isLoading = true)
        clearUserEmailError()
        Amplify.Auth.signIn(uiState.userEmail, uiState.userPassword, {
            if (it.isSignInComplete) {
                Amplify.Auth.fetchAuthSession({ authSessionSuccess ->
                    val session = authSessionSuccess as AWSCognitoAuthSession
                    when (session.identityId.type) {
                        AuthSessionResult.Type.SUCCESS -> {
                            uiState = uiState.copy(isLoading = false)
                            if (uiState.isFingerprintChecked) {
                                uiState = uiState.copy(configureBiometric = true)
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

    private fun isFormValid() {
        uiState = uiState.copy(
            isSignInEnabled = when {
                uiState.userEmail.isBlank() -> false
                isEmailValid(uiState.userEmail).not() -> false
                uiState.userPassword.isBlank() -> false
                else -> true
            }
        )
    }

    private fun isUserEmailValid() {
        if (isEmailValid(uiState.userEmail).not()) {
            uiState = uiState.copy(userEmailError = Pair(true, R.string.sign_in_email_not_valid))
        }
    }

    private fun clearUserEmailError() {
        uiState = uiState.copy(
            userEmailError = Pair(false, R.string.error_empty),
            userPasswordError = if (uiState.userPasswordError.second == R.string.sign_in_validation) {
                Pair(false, R.string.error_empty)
            } else {
                uiState.userPasswordError
            }
        )
    }

    private fun onUserEmailValueChange(value: String) {
        uiState = uiState.copy(userEmail = value)
        clearUserEmailError()
        isFormValid()
    }

    private fun clearUserPasswordError() {
        uiState = uiState.copy(
            userEmailError = if (uiState.userPasswordError.second == R.string.sign_in_validation) {
                Pair(false, R.string.error_empty)
            } else {
                uiState.userEmailError
            },
            userPasswordError = if (uiState.userPasswordError.second == R.string.sign_in_validation) {
                Pair(false, R.string.error_empty)
            } else {
                uiState.userPasswordError
            }
        )
    }

    private fun onUserPasswordValueChange(value: String) {
        uiState = uiState.copy(userPassword = value)
        clearUserPasswordError()
        isFormValid()
    }

    private fun cognitoError() {
        uiState = uiState.copy(
            userEmailError = Pair(true, R.string.error_empty),
            userPasswordError = Pair(true, R.string.sign_in_validation),
            isLoading = false
        )
    }

    private fun biometricPromptError(errorCode: Int, errString: CharSequence) {
        if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
            uiState = uiState.copy(
                biometricError = true,
                biometricErrorDialog = Pair(mutableStateOf(true), errString.toString())
            )
        }
    }

    private fun biometricPromptConfigurationError(errorCode: Int, errString: CharSequence) {
        if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
            navigateToHome()
        } else {
            // TODO: Pending logic by business
        }
    }

    private fun onShowBiometricPromptForEncryption(fragmentActivity: FragmentActivity) {
        biometricHelper.showBiometricPrompt(
            title = biometricPromptTitle,
            description = biometricPromptDescription,
            negative = biometricPromptNegative,
            activity = fragmentActivity,
            processSuccess = { result ->
                biometricPromptForEncryptionSuccess(result)
            },
            processError = { errorCode, errString ->
                biometricPromptConfigurationError(errorCode, errString)
            }
        )
    }

    private fun biometricPromptForEncryptionSuccess(result: BiometricPrompt.AuthenticationResult) {
        result.cryptoObject?.cipher?.apply {
            viewModelScope.launch {
                uiState = uiState.copy(configureBiometric = false)
                dataStorePreferences.setUserEmail(uiState.userEmail)
                dataStorePreferences.setUserName(uiState.userEmail)
                dataStorePreferences.setUserPassword(uiState.userPassword, this@apply)
                dataStorePreferences.isBiometricsEnabled(true)
                navigateToHome()
            }
        }
    }

    private fun onShowBiometricPromptForDecryption(fragmentActivity: FragmentActivity) {
        viewModelScope.launch {
            biometricHelper.showBiometricPrompt(
                title = biometricPromptTitle,
                description = biometricPromptDescription,
                negative = biometricPromptNegative,
                activity = fragmentActivity,
                processSuccess = { result ->
                    biometricPromptForDecryptionSuccess(result)
                },
                processError = { errorCode, errString ->
                    biometricPromptError(errorCode, errString)
                },
                initializationVector = dataStorePreferences.getUserPasswordVector()
                    .first()
            )
        }
    }

    private fun biometricPromptForDecryptionSuccess(result: BiometricPrompt.AuthenticationResult) {
        result.cryptoObject?.cipher?.apply {
            viewModelScope.launch {
                uiState = uiState.copy(
                    userPassword = dataStorePreferences.getUserPassword(this@apply).first()
                )
                callCognitoSignIn()
            }
        }
    }

    private fun navigateToHome() = popAndNavigateTo(
        route = Screen.HomeScreen.route,
        popTo = Screen.SignInScreen.route
    )

    private fun initializeBiometricPrompt(
        biometricPromptTitle: String,
        biometricPromptDescription: String,
        biometricPromptNegative: String
    ) {
        this.biometricPromptTitle = biometricPromptTitle
        this.biometricPromptDescription = biometricPromptDescription
        this.biometricPromptNegative = biometricPromptNegative
    }

    private fun onFingerprintCheckedChanged(value: Boolean, showDialog: Boolean) {
        uiState = uiState.copy(
            isFingerprintChecked = value,
            openDialogCustom = mutableStateOf(showDialog)
        )
    }

    data class UIState(
        // Fields
        val userEmail: String = "",
        val userEmailError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
        val userPassword: String = "",
        val userPasswordError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
        val userName: String? = null,
        val isFingerprintChecked: Boolean = false,

        // Interactions
        val isSignInEnabled: Boolean = false,
        val biometricErrorDialog: Pair<MutableState<Boolean>, String> = Pair(
            mutableStateOf(false),
            ""
        ),
        val configureBiometric: Boolean = false,
        val biometricError: Boolean = false,
        val openDialogCustom: MutableState<Boolean> = mutableStateOf(false),
        val isBiometricActive: Boolean = false,
        val showBiometricSignIn: Boolean = false,
        val isLoading: Boolean = false
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnUserPasswordValueChange -> onUserPasswordValueChange(event.value)
            is OnUserEmailValueChange -> onUserEmailValueChange(event.value)
            is OnInitializeBiometricPrompt -> initializeBiometricPrompt(
                event.biometricPromptTitle,
                event.biometricPromptDescription,
                event.biometricPromptNegative
            )
            is OnShowBiometricPromptForEncryption -> onShowBiometricPromptForEncryption(event.fragmentActivity)
            is OnShowBiometricPromptForDecryption -> onShowBiometricPromptForDecryption(event.fragmentActivity)
            is OnShowBiometricSignInChanged -> uiState =
                uiState.copy(showBiometricSignIn = event.value)
            is OnFingerprintCheckedChanged -> onFingerprintCheckedChanged(
                event.value,
                event.showDialog
            )

            is OnStart -> onStart()
            is OnValidateUserEmail -> isUserEmailValid()
            is OnCallCognitoSignIn -> callCognitoSignIn()
        }
    }

    sealed class UIEvent {

        data class OnUserPasswordValueChange(val value: String) : UIEvent()
        data class OnUserEmailValueChange(val value: String) : UIEvent()
        data class OnInitializeBiometricPrompt(
            val biometricPromptTitle: String,
            val biometricPromptDescription: String,
            val biometricPromptNegative: String
        ) : UIEvent()

        data class OnShowBiometricPromptForEncryption(val fragmentActivity: FragmentActivity) :
            UIEvent()

        data class OnShowBiometricPromptForDecryption(val fragmentActivity: FragmentActivity) :
            UIEvent()

        data class OnShowBiometricSignInChanged(val value: Boolean) :
            UIEvent()

        data class OnFingerprintCheckedChanged(val value: Boolean, val showDialog: Boolean) :
            UIEvent()


        object OnStart : UIEvent()
        object OnValidateUserEmail : UIEvent()
        object OnCallCognitoSignIn : UIEvent()
    }
}