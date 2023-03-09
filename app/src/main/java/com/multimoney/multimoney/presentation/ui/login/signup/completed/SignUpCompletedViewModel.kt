package com.multimoney.multimoney.presentation.ui.login.signup.completed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.cognitoidentityprovider.util.CognitoJWTParser
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.cognito.options.AWSCognitoAuthSignInOptions
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.PASSWORD
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.completed.SignUpCompletedViewModel.UIEvent.OnSetupDeviceInfo
import com.multimoney.multimoney.presentation.ui.login.signup.completed.SignUpCompletedViewModel.UIEvent.OnSignIn
import com.multimoney.multimoney.presentation.ui.login.signup.password.SignUpPasswordViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.password.SignUpPasswordViewModel.UIEvent
import com.multimoney.multimoney.presentation.util.checkIfEmulator
import com.multimoney.multimoney.presentation.util.getAppVersion
import com.multimoney.multimoney.presentation.util.getDeviceBrand
import com.multimoney.multimoney.presentation.util.getDeviceModel
import com.multimoney.multimoney.presentation.util.getIPAddress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class SignUpCompletedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(false) {

    private var email = ""
    private var password = ""
    private var deviceId = ""
    private var uniqueId = ""
    private var ipAddress = ""
    private var deviceType = ""
    private var deviceName = ""
    private var appVersion = getAppVersion()
    private var deviceBrand = getDeviceBrand()
    private var deviceModel = getDeviceModel()
    private var isEmulator = checkIfEmulator()
    private var forceDeviceChange = false

    init {
        email = savedStateHandle[EMAIL] ?: ""
        password = savedStateHandle[PASSWORD] ?: ""
    }

    private fun onSetupDeviceInfo(
        deviceName: String,
        deviceType: String
    ) {
        viewModelScope.launch {
            deviceId = dataStorePreferences.getDeviceId().first()
            uniqueId = dataStorePreferences.getUniqueId().first()
        }
        viewModelScope.launch(Dispatchers.IO) {
            ipAddress = getIPAddress() ?: ""
        }
        this.deviceName = deviceName
        this.deviceType = deviceType
    }

    private fun callCognitoSignIn() {
        val attrs = mapOf(
            SignInViewModel.DEVICE_ID to deviceId,
            SignInViewModel.BRAND to deviceBrand,
            SignInViewModel.UNIQUE_ID to uniqueId,
            SignInViewModel.MODEL to deviceModel,
            SignInViewModel.DEVICE_NAME to deviceName,
            SignInViewModel.APP_VERSION to appVersion,
            SignInViewModel.IS_EMULATOR to isEmulator.toString(),
            SignInViewModel.IP_ADDRESS to ipAddress,
            SignInViewModel.FORCE to forceDeviceChange.toString()
        )

        val options = AWSCognitoAuthSignInOptions.builder().metadata(attrs).build()

        Amplify.Auth.signIn(
            email,
            password,
            options,
            { authSignInResult ->
                if (authSignInResult.isSignInComplete) {
                    Amplify.Auth.fetchAuthSession({ authSessionSuccess ->
                        val session = authSessionSuccess as AWSCognitoAuthSession
                        when (session.identityId.type) {
                            AuthSessionResult.Type.SUCCESS -> {
                                // Get user attributes in order to save user name for welcome message
                                Amplify.Auth.fetchUserAttributes({ authUserAttribute ->
                                    viewModelScope.launch {
                                        // If isBiometricActive false that means the userName has to be saved
                                        val payload =
                                            CognitoJWTParser.getPayload(session.userPoolTokens.value?.idToken)
                                        saveUserData(
                                            payload,
                                            session.userPoolTokens.value?.idToken.orEmpty(),
                                            authUserAttribute
                                        )
                                        navigateToHome()
                                    }
                                }, {
                                    onNavigateToSignIn()
                                })
                            }
                            AuthSessionResult.Type.FAILURE -> onNavigateToSignIn()
                        }
                    }, {
                        onNavigateToSignIn()
                    })
                } else {
                    onNavigateToSignIn()
                }
            },
            {
                onNavigateToSignIn()
            }
        )
    }

    private suspend fun saveUserData(
        payload: JSONObject,
        idToken: String,
        authUserAttribute: List<AuthUserAttribute>
    ) {
        dataStorePreferences.setUserName("${authUserAttribute.firstOrNull { it.key == AuthUserAttributeKey.name() }?.value.orEmpty()} ${authUserAttribute.firstOrNull { it.key == AuthUserAttributeKey.familyName() }?.value.orEmpty()}")
        dataStorePreferences.setAuthToken(idToken)
        dataStorePreferences.setIdBrand(payload.getString(SignUpPasswordViewModel.COGNITO_CUSTOM_ID_BRAND))
        dataStorePreferences.setPkUser(payload.getString(SignUpPasswordViewModel.COGNITO_CUSTOM_PK_USER))
        dataStorePreferences.setIdentification(payload.getString(SignUpPasswordViewModel.COGNITO_CUSTOM_IDENTIFICATION))
        dataStorePreferences.setUserEmail(email)
    }

    private fun navigateToHome() = popAndNavigateTo(
        route = Screen.HomeScreen.route,
        popTo = Screen.SignUpCompleted.route
    )

    private fun onNavigateToSignIn() {
        popAndNavigateTo(
            route = Screen.SignInScreen.route,
            popTo = Screen.SignUpCompleted.route
        )
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnSignIn -> callCognitoSignIn()
            is OnSetupDeviceInfo -> onSetupDeviceInfo(
                event.deviceName,
                event.deviceType
            )
        }
    }

    sealed class UIEvent {
        object OnSignIn : UIEvent()
        data class OnSetupDeviceInfo(
            val deviceName: String,
            val deviceType: String
        ) : UIEvent()
    }
}
