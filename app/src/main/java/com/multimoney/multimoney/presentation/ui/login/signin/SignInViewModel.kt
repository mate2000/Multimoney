package com.multimoney.multimoney.presentation.ui.login.signin

import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.cognitoidentityprovider.util.CognitoJWTParser
import com.amplifyframework.auth.AuthUserAttributeKey
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
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnNavigateToForgotPassword
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnShowBiometricPromptForDecryption
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnShowBiometricPromptForEncryption
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnShowBiometricSignInChanged
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnUserEmailValueChange
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnUserPasswordValueChange
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnValidateUserEmail
import com.multimoney.multimoney.presentation.ui.login.signup.password.SignUpPasswordViewModel
import com.multimoney.multimoney.presentation.util.isEmailValid
import com.multimoney.multimoney.util.BiometricHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val biometricHelper: BiometricHelper,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var isForcePassword = false
    private var biometricUserEmail = ""
    private var biometricPromptTitle = ""
    private var biometricPromptDescription = ""
    private var biometricPromptNegative = ""

    private fun onStart() {
        viewModelScope.launch {
            val isBiometricActive = dataStorePreferences.isBiometricsEnabled().first()
            biometricUserEmail = dataStorePreferences.getUserEmail().first()
            uiState = uiState.copy(
                userEmail = biometricUserEmail,
                userName = dataStorePreferences.getUserName().first(),
                isBiometricActive = isBiometricActive,
                showBiometricSignIn = isBiometricActive
            )
        }
    }

    private fun callCognitoSignIn() {
        uiState = uiState.copy(isLoading = true)
        clearUserEmailError()

        // TODO: Implement logic to send metadata to cognito
//        val attrs = mapOf(
//            DEVICE_ID to "Android 1"
//        )
//
//        val options = AWSCognitoAuthSignInOptions.builder()
//            .metadata(attrs)
//            .build()

        Amplify.Auth.signOut({
            // TODO: This line must be uncommented when logic to send metadata to cognito is implemented
//        Amplify.Auth.signIn(uiState.userEmail, uiState.userPassword, options, { authSignInResult ->
            Amplify.Auth.signIn(uiState.userEmail, uiState.userPassword, { authSignInResult ->
                if (authSignInResult.isSignInComplete) {
                    Amplify.Auth.fetchAuthSession({ authSessionSuccess ->
                        val session = authSessionSuccess as AWSCognitoAuthSession
                        when (session.identityId.type) {
                            AuthSessionResult.Type.SUCCESS -> {
                                // Get user attributes in order to save user name for welcome message
                                Amplify.Auth.fetchUserAttributes({ authUserAttribute ->
                                    viewModelScope.launch {
                                        // If isBiometricActive false that means the userName has to be saved
                                        if (uiState.isBiometricActive.not()) {
                                            dataStorePreferences.setUserName("${authUserAttribute.firstOrNull { it.key == AuthUserAttributeKey.name() }?.value.orEmpty()} ${authUserAttribute.firstOrNull { it.key == AuthUserAttributeKey.familyName() }?.value.orEmpty()}")
                                        }
                                        val payload = CognitoJWTParser.getPayload(session.userPoolTokens.value?.idToken)
                                        dataStorePreferences.setAuthToken(session.userPoolTokens.value?.idToken ?: "")
                                        saveUserData(payload)
                                        uiState = uiState.copy(isLoading = false)
                                        if (uiState.isFingerprintChecked) {
                                            uiState = uiState.copy(configureBiometric = true)
                                        } else {
                                            navigateToHome()
                                        }
                                    }
                                }, {
                                    cognitoError()
                                })
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
        }, {
            cognitoError()
        })
    }

    private suspend fun saveUserData(payload: JSONObject) {
        dataStorePreferences.setIdBrand(payload.getString(SignUpPasswordViewModel.COGNITO_CUSTOM_ID_BRAND))
        dataStorePreferences.setPkUser(payload.getString(SignUpPasswordViewModel.COGNITO_CUSTOM_PK_USER))
        dataStorePreferences.setIdentification(payload.getString(SignUpPasswordViewModel.COGNITO_CUSTOM_IDENTIFICATION))
        dataStorePreferences.setUserEmail(uiState.userEmail)
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
        uiState = uiState.copy(
            userEmailError = if (isEmailValid(uiState.userEmail).not()) {
                Pair(true, R.string.sign_in_email_not_valid)
            } else {
                Pair(false, R.string.error_empty)
            },
            showBiometricSignIn = uiState.isBiometricActive && uiState.userEmail == biometricUserEmail && isForcePassword.not()
        )
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
                isBiometricError = true,
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
            Amplify.Auth.fetchUserAttributes({ authUserAttribute ->
                uiState = uiState.copy(configureBiometric = false)
                viewModelScope.launch {
                    dataStorePreferences.setUserEmail(uiState.userEmail)
                    dataStorePreferences.setUserPassword(uiState.userPassword, this@apply)
                    dataStorePreferences.setUserName("${authUserAttribute.firstOrNull { it.key == AuthUserAttributeKey.name() }?.value.orEmpty()} ${authUserAttribute.firstOrNull { it.key == AuthUserAttributeKey.middleName() }?.value.orEmpty()}")
                    dataStorePreferences.isBiometricsEnabled(true)
                    navigateToHome()
                }
            }, {
                cognitoError()
            })
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

    private fun onNavigateToForgotPassword() {
        // navigate to forgot screen
    }

    private fun onShowBiometricSignInChanged(value: Boolean) {
        uiState = uiState.copy(
            showBiometricSignIn = value,
            userEmail = if (value) {
                biometricUserEmail
            } else {
                uiState.userEmail
            }
        )
        isForcePassword = value.not()
    }

    fun isAccessWithBiometrics() = uiState.isBiometricActive && uiState.userEmail == biometricUserEmail

    fun isWelcomeWithName() = uiState.userName.isNotEmpty() && uiState.userEmail == biometricUserEmail

    data class UIState(
        // Fields
        val userEmail: String = "",
        val userEmailError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
        val userPassword: String = "",
        val userPasswordError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
        val userName: String = "",
        val isFingerprintChecked: Boolean = false,

        // Interactions
        val isSignInEnabled: Boolean = false,
        val biometricErrorDialog: Pair<MutableState<Boolean>, String> = Pair(
            mutableStateOf(false),
            ""
        ),
        val configureBiometric: Boolean = false,
        val isBiometricError: Boolean = false,
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
            is OnShowBiometricSignInChanged -> onShowBiometricSignInChanged(event.value)
            is OnFingerprintCheckedChanged -> onFingerprintCheckedChanged(
                event.value,
                event.showDialog
            )

            is OnStart -> onStart()
            is OnValidateUserEmail -> isUserEmailValid()
            is OnCallCognitoSignIn -> callCognitoSignIn()
            is OnNavigateToForgotPassword -> onNavigateToForgotPassword()
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
        object OnNavigateToForgotPassword : UIEvent()
    }

    companion object {
        const val DEVICE_ID = "DeviceId"
    }
}
