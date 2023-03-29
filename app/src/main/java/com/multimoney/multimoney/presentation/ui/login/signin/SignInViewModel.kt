package com.multimoney.multimoney.presentation.ui.login.signin

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.cognitoidentityprovider.util.CognitoJWTParser
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.cognito.options.AWSCognitoAuthSignInOptions
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.profile.QueryCountryContactUseCase
import com.multimoney.domain.interaction.security.QueryValidateUserExistsUseCase
import com.multimoney.domain.model.metrics.EmailDto
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.password.SignUpPasswordViewModel
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.catalog.CognitoErrorCode
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.checkIfEmulator
import com.multimoney.multimoney.presentation.util.getAppVersion
import com.multimoney.multimoney.presentation.util.getDeviceBrand
import com.multimoney.multimoney.presentation.util.getDeviceModel
import com.multimoney.multimoney.presentation.util.getIPAddress
import com.multimoney.multimoney.presentation.util.getNavParam
import com.multimoney.multimoney.presentation.util.isCognitoErrorCode
import com.multimoney.multimoney.presentation.util.isEmailValid
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink
import com.multimoney.multimoney.presentation.util.toJson
import com.multimoney.multimoney.util.BiometricHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val biometricHelper: BiometricHelper,
    private val dataStorePreferences: DataStorePreferences,
    private val queryValidateUserExistsUseCase: QueryValidateUserExistsUseCase,
    private val queryCountryContactUseCase: QueryCountryContactUseCase
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
    private var forceShowBiometricsPrompt = false

    private fun onStart(
        deviceName: String,
        deviceType: String,
        forceDeviceChange: Boolean
    ) {
        this.deviceName = deviceName
        this.deviceType = deviceType
        this.forceDeviceChange = forceDeviceChange
        onUserPasswordValueChange("")
        viewModelScope.launch(Dispatchers.IO) { ipAddress = getIPAddress() ?: "" }
        viewModelScope.launch {
            deviceId = dataStorePreferences.getDeviceId().first()
            uniqueId = dataStorePreferences.getUniqueId().first()
            val isBiometricActive = dataStorePreferences.isBiometricsEnabled().first()
            biometricUserEmail = dataStorePreferences.getUserEmail().first()
            forceShowBiometricsPrompt = dataStorePreferences.isForceShowBiometricPrompt().first()
            uiState = uiState.copy(
                userEmail = biometricUserEmail,
                userName = dataStorePreferences.getUserName().first(),
                isBiometricActive = isBiometricActive,
                showBiometricSignIn = isBiometricActive,
                userPassword = "",
                userPasswordError = Pair(false, string.error_empty),
                toastIsVisible = dataStorePreferences.isSignOutOnBackground().first()
            )
        }
    }

    private fun callCognitoSignIn(activity: FragmentActivity) {
        uiState = uiState.copy(isLoading = true)
        clearUserEmailError()

        // TODO: Implement logic to send metadata to cognito
        val attrs = mapOf(
            DEVICE_ID to deviceId,
            BRAND to deviceBrand,
            UNIQUE_ID to uniqueId,
            MODEL to deviceModel,
            DEVICE_NAME to deviceName,
            APP_VERSION to appVersion,
            IS_EMULATOR to isEmulator.toString(),
            IP_ADDRESS to ipAddress,
            FORCE to forceDeviceChange.toString()
        )

        val options = AWSCognitoAuthSignInOptions.builder().metadata(attrs).build()

        Amplify.Auth.signOut({
            Amplify.Auth.signIn(
                uiState.userEmail,
                uiState.userPassword,
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
                                            if (payload.getString(SignUpPasswordViewModel.COGNITO_CHANGE_PASSWORD_REQUIRED)
                                                .toBoolean()
                                            ) {
                                                uiState = uiState.copy(
                                                    openDialog = DialogParameters(
                                                        titleResource = string.sign_in_expired_password_dialog_title,
                                                        descriptionResource = string.sign_in_expired_password_dialog_description,
                                                        positiveResource = string.sign_in_expired_password_dialog_positive_button,
                                                        positiveAction = {
                                                            onNavigateToChangePassword(
                                                                idBrand = payload.getString(
                                                                    SignUpPasswordViewModel.COGNITO_CUSTOM_ID_BRAND
                                                                ).toIntOrNull() ?: 0,
                                                                pkUser = payload.getString(
                                                                    SignUpPasswordViewModel.COGNITO_CUSTOM_PK_USER
                                                                ),
                                                                userName = uiState.userEmail
                                                            )
                                                        },
                                                        isActive = mutableStateOf(true)
                                                    ),
                                                    isLoading = false
                                                )
                                            } else {
                                                uiState = uiState.copy(isLoading = false)
                                                if (uiState.isFingerprintChecked) {
                                                    uiState = uiState.copy(configureBiometric = true)
                                                } else {
                                                    navigateToHome()
                                                }
                                            }
                                        }
                                    }, {
                                        callQueryValidationUserExistsUseCase()
                                    })
                                }
                                AuthSessionResult.Type.FAILURE -> callQueryValidationUserExistsUseCase()
                            }
                        }, {
                            callQueryValidationUserExistsUseCase()
                        })
                    } else {
                        callQueryValidationUserExistsUseCase()
                    }
                },
                { checkSessionState(it) }
            )
        }, {
            callQueryValidationUserExistsUseCase()
        })
    }

    private fun onNavigateToChangePassword(idBrand: Int, pkUser: String, userName: String) =
        navigateTo("${Screen.ProfileChangePasswordScreen.baseRoute}/$idBrand/$pkUser/$userName/${uiState.userEmail}/${Screen.SignInScreen.baseRoute}")

    private fun checkSessionState(authException: AuthException) = when {
        authException.cause?.message?.isCognitoErrorCode(CognitoErrorCode.SessionActive.code) == true ->
            uiState = uiState.copy(
                errorCode = CognitoErrorCode.SessionActive,
                openDialog = DialogParameters(
                    titleResource = string.sign_in_session_active_on_another_device_title,
                    descriptionResource = string.sign_in_session_open_here_close_another,
                    positiveResource = string.sign_in_dialog_sign_in_here_button,
                    negativeResource = string.sign_in_dialog_exit_button,
                    positiveAction = { onUIEvent(UIEvent.OnNavigateToOTPScreen) },
                    negativeAction = { onUIEvent(UIEvent.OnCloseDialog) },
                    dismissAction = { onUIEvent(UIEvent.OnCloseDialog) },
                    isActive = mutableStateOf(true)
                ),
                isLoading = false
            )
        authException.cause?.message?.isCognitoErrorCode(CognitoErrorCode.SessionBlocked.code) == true ->
            uiState = uiState.copy(
                errorCode = CognitoErrorCode.SessionBlocked,
                openDialog = DialogParameters(
                    titleResource = string.sign_in_session_blocked_title,
                    descriptionResource = string.sign_in_session_blacklisted_message_sv,
                    isActive = mutableStateOf(true)
                ),
                isLoading = false
            )
        authException.cause?.message?.isCognitoErrorCode(CognitoErrorCode.BlacklistedDevice.code) == true ||
            authException.cause?.message?.isCognitoErrorCode(CognitoErrorCode.BlacklistedDeviceTooManyAccounts.code) == true ->
            uiState = uiState.copy(
                errorCode = CognitoErrorCode.BlacklistedDevice,
                openDialog = DialogParameters(
                    titleResource = if (uiState.isO3Country == SignUpViewModel.ISO3_COSTA_RICA) string.sign_in_session_blacklisted_title_cr else string.sign_in_session_blacklisted_title,
                    descriptionResource = string.sign_in_session_blacklisted_message_sv,
                    positiveResource = string.sign_in_session_blacklisted_contact_support,
                    isActive = mutableStateOf(true)
                ),
                isLoading = false
            )
        else -> callQueryValidationUserExistsUseCase()
    }

    private fun callQueryValidationUserExistsUseCase() = executeUseCase {
        queryValidateUserExistsUseCase(
            email = uiState.userEmail,
            deviceId = dataStorePreferences.getDeviceId().first()
        ).collectLatest { result ->
            result.onSuccess { userData ->
                if (userData?.isNewUser == false) {
                    uiState = uiState.copy(
                        openDialog = DialogParameters(
                            titleResource = string.sign_in_dialog_user_exists_title,
                            descriptionResource = string.sign_in_dialog_user_exists_description,
                            positiveResource = string.button_continue,
                            negativeResource = string.common_return,
                            positiveAction = { onNavigateToSignUp() },
                            negativeAction = { onUIEvent(UIEvent.OnCloseDialog) },
                            dismissAction = { onUIEvent(UIEvent.OnCloseDialog) },
                            isActive = mutableStateOf(true)
                        ),
                        isLoading = false
                    )
                } else {
                    cognitoError()
                }
            }.onMessage {
                cognitoError()
            }.onFailure {
                cognitoError()
            }
        }
    }

    private fun onCloseDialog() {
        uiState = uiState.copy(
            openDialog = DialogParameters(isActive = mutableStateOf(false)),
            isLoading = false
        )
    }

    private suspend fun saveUserData(
        payload: JSONObject,
        idToken: String,
        authUserAttribute: List<AuthUserAttribute>
    ) {
        if (uiState.userEmail != biometricUserEmail) {
            dataStorePreferences.isBiometricsEnabled(false)
        }
        dataStorePreferences.setUserName("${authUserAttribute.firstOrNull { it.key == AuthUserAttributeKey.name() }?.value.orEmpty()} ${authUserAttribute.firstOrNull { it.key == AuthUserAttributeKey.familyName() }?.value.orEmpty()}")
        dataStorePreferences.setAuthToken(idToken)
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
                Pair(true, string.sign_in_email_not_valid)
            } else {
                Pair(false, string.error_empty)
            },
            showBiometricSignIn = uiState.isBiometricActive && uiState.userEmail == biometricUserEmail && isForcePassword.not()
        )
    }

    private fun clearUserEmailError() {
        uiState = uiState.copy(
            userEmailError = Pair(false, string.error_empty),
            userPasswordError = if (uiState.userPasswordError.second == string.sign_in_validation) {
                Pair(false, string.error_empty)
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
            userEmailError = if (uiState.userPasswordError.second == string.sign_in_validation) {
                Pair(false, string.error_empty)
            } else {
                uiState.userEmailError
            },
            userPasswordError = if (uiState.userPasswordError.second == string.sign_in_validation) {
                Pair(false, string.error_empty)
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
            userEmailError = Pair(true, string.error_empty),
            userPasswordError = Pair(true, string.sign_in_validation),
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
                    biometricPromptForDecryptionSuccess(fragmentActivity, result)
                },
                processError = { errorCode, errString ->
                    biometricPromptError(errorCode, errString)
                },
                initializationVector = dataStorePreferences.getUserPasswordVector().first()
            )
        }
    }

    private fun biometricPromptForDecryptionSuccess(
        activity: FragmentActivity,
        result: BiometricPrompt.AuthenticationResult
    ) {
        result.cryptoObject?.cipher?.apply {
            viewModelScope.launch {
                uiState = uiState.copy(
                    userPassword = dataStorePreferences.getUserPassword(this@apply).first()
                )
                callCognitoSignIn(activity)
            }
        }
    }

    private fun navigateToHome() {
        viewModelScope.launch {
            if (dataStorePreferences.isAdjustFirstSingInEventRegister().first()) {
                registerAdjustEvent(
                    AdjustEventType.FIRST_LOGIN_3000,
                    isLoggedIn = false,
                    data = EmailDto(uiState.userEmail).toJson()
                )
                dataStorePreferences.isAdjustFirstSingInEventRegister(false)
            } else {
                registerAdjustEvent(
                    AdjustEventType.LOGIN_3001,
                    isLoggedIn = false,
                    applyAdjust = false,
                    data = EmailDto(uiState.userEmail).toJson()
                )
            }
        }
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.SignInScreen.route
        )
    }

    private fun onNavigateToSignUp() =
        navigateTo(route = "${Screen.SignUpScreen.baseRoute}/".plus(0))

    private fun initializeBiometricPrompt(
        biometricPromptTitle: String,
        biometricPromptDescription: String,
        biometricPromptNegative: String,
        fragmentActivity: FragmentActivity
    ) {
        this.biometricPromptTitle = biometricPromptTitle
        this.biometricPromptDescription = biometricPromptDescription
        this.biometricPromptNegative = biometricPromptNegative
        if (uiState.isBiometricActive && forceShowBiometricsPrompt) {
            onShowBiometricPromptForDecryption(fragmentActivity = fragmentActivity)
            deactivateForceBiometricPrompt()
        } else {
            deactivateForceBiometricPrompt()
        }
    }

    private fun deactivateForceBiometricPrompt() {
        viewModelScope.launch {
            dataStorePreferences.isForceShowBiometricPrompt(false)
            forceShowBiometricsPrompt = false
        }
    }

    private fun onFingerprintCheckedChanged(
        value: Boolean,
        showDialog: Boolean,
        is03Country: String
    ) {
        uiState = uiState.copy(
            isFingerprintChecked = value,
            openDialog = getBiometricsDialogParameters(is03Country, showDialog)
        )
    }

    private fun getBiometricsDialogParameters(
        is03Country: String,
        showDialog: Boolean
    ): DialogParameters {
        return when (is03Country) {
            ISO3_COSTA_RICA -> {
                DialogParameters(
                    titleResource = string.active_biometric_title_cr,
                    descriptionResource = string.active_biometric_message_cr,
                    positiveResource = string.active_biometric_positive_button_label,
                    negativeResource = string.active_biometric_negative_button_label,
                    positiveAction = {
                        onUIEvent(
                            UIEvent.OnFingerprintCheckedChanged(
                                value = true,
                                showDialog = false,
                                is03Country
                            )
                        )
                    },
                    negativeAction = {
                        onUIEvent(
                            UIEvent.OnFingerprintCheckedChanged(
                                value = false,
                                showDialog = false,
                                is03Country
                            )
                        )
                    },
                    dismissAction = {
                        onUIEvent(
                            UIEvent.OnFingerprintCheckedChanged(
                                value = false,
                                showDialog = false,
                                is03Country
                            )
                        )
                    },
                    isActive = mutableStateOf(showDialog)
                )
            }
            else -> {
                DialogParameters(
                    titleResource = string.active_biometric_title,
                    descriptionResource = string.active_biometric_message,
                    positiveResource = string.active_biometric_positive_button_label,
                    negativeResource = string.active_biometric_negative_button_label,
                    positiveAction = {
                        onUIEvent(
                            UIEvent.OnFingerprintCheckedChanged(
                                value = true,
                                showDialog = false,
                                is03Country
                            )
                        )
                    },
                    negativeAction = {
                        onUIEvent(
                            UIEvent.OnFingerprintCheckedChanged(
                                value = false,
                                showDialog = false,
                                is03Country
                            )
                        )
                    },
                    dismissAction = {
                        onUIEvent(
                            UIEvent.OnFingerprintCheckedChanged(
                                value = false,
                                showDialog = false,
                                is03Country
                            )
                        )
                    },
                    isActive = mutableStateOf(showDialog)
                )
            }
        }
    }

    private fun onNavigateToForgotPassword() = navigateTo(
        route = Screen.RequestForgotPassword.baseRoute.plus(
            getNavParam(PREVIOUS_SCREEN, Screen.SignInScreen.route)
        )
    )

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

    fun isAccessWithBiometrics() =
        uiState.isBiometricActive && uiState.userEmail == biometricUserEmail

    fun isWelcomeWithName() =
        uiState.userName.isNotEmpty() && uiState.userEmail == biometricUserEmail

    private fun onNavigateToOTPScreen() {
        registerAdjustEvent(
            AdjustEventType.SECURITY_LOGIN_CHANGE_DEVICE_9002,
            isLoggedIn = false,
            applyAdjust = false,
            data = EmailDto(uiState.userEmail).toJson()
        )
        popAndNavigateTo(
            "${Screen.SignInOTPScreen.baseRoute}/${uiState.userEmail}/${uiState.userPassword}/$deviceId/$uniqueId/$ipAddress/$deviceType/$deviceName/$appVersion/$deviceBrand/$deviceModel/$isEmulator",
            Screen.SignInOTPScreen.baseRoute
        )
    }

    private fun onUpdateToastVisibility(value: Boolean) {
        viewModelScope.launch {
            uiState = uiState.copy(toastIsVisible = value)
            dataStorePreferences.isSignOutOnBackground(value)
        }
    }

    private fun openWhatsAppLink(context: Context) {
        context.openWhatsAppDeepLink(uiState.linkWhatsapp)
        onNavigateBack()
    }

    private fun onNavigateBack() {
        navigateBack(Screen.HomeScreen.route, isRestart = true)
    }

    private fun getContactInfo(idBrand: Int) = executeUseCase {
        queryCountryContactUseCase.invoke(
            user = GUEST_USER,
            idBrand = idBrand
        ).collectLatest { result ->
            result.onSuccess { contactInfo ->
                uiState = uiState.copy(linkWhatsapp = contactInfo?.whatsappLink ?: "")
                dataStorePreferences.setWhatsAppLink(contactInfo?.whatsappLink ?: "")
            }
        }
    }

    private fun setCountryCode(countryCode: String) {
        executeUseCase {
            getContactInfo(Brand.Search.getIdBrandByCountryCode(countryCode))
        }
    }

    data class UIState(
        // Fields
        val userEmail: String = "",
        val userEmailError: Pair<Boolean, Int> = Pair(false, string.error_empty),
        val userPassword: String = "",
        val userPasswordError: Pair<Boolean, Int> = Pair(false, string.error_empty),
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
        val isBiometricActive: Boolean = false,
        val showBiometricSignIn: Boolean = false,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val toastIsVisible: Boolean = false,
        val isO3Country: String = "",
        val linkWhatsapp: String = "",
        val errorCode: CognitoErrorCode? = null
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnUserPasswordValueChange -> onUserPasswordValueChange(event.value)
            is UIEvent.OnUserEmailValueChange -> onUserEmailValueChange(event.value)
            is UIEvent.OnInitializeBiometricPrompt -> initializeBiometricPrompt(
                event.biometricPromptTitle,
                event.biometricPromptDescription,
                event.biometricPromptNegative,
                event.fragmentActivity
            )
            is UIEvent.OnShowBiometricPromptForEncryption -> onShowBiometricPromptForEncryption(
                event.fragmentActivity
            )
            is UIEvent.OnShowBiometricPromptForDecryption -> onShowBiometricPromptForDecryption(
                event.fragmentActivity
            )
            is UIEvent.OnShowBiometricSignInChanged -> onShowBiometricSignInChanged(event.value)
            is UIEvent.OnFingerprintCheckedChanged -> onFingerprintCheckedChanged(
                event.value,
                event.showDialog,
                event.is03Country
            )

            is UIEvent.OnStart -> onStart(
                event.deviceName,
                event.deviceType,
                event.forceDeviceChange
            )
            is UIEvent.OnValidateUserEmail -> isUserEmailValid()
            is UIEvent.OnCallCognitoSignIn -> callCognitoSignIn(event.activity)
            is UIEvent.OnNavigateToForgotPassword -> onNavigateToForgotPassword()
            is UIEvent.OnCloseDialog -> onCloseDialog()
            is UIEvent.OnNavigateToOTPScreen -> onNavigateToOTPScreen()
            is UIEvent.OnNavigateToSignUp -> onNavigateToSignUp()
            is UIEvent.OnUpdateToastVisibility -> onUpdateToastVisibility(event.value)
            is UIEvent.OnUpdateIso3Country ->
                uiState =
                    uiState.copy(isO3Country = event.iso3Country)
            is UIEvent.OnOpenWhatsappLink -> openWhatsAppLink(event.context)
            is UIEvent.OnSetCountryCode -> setCountryCode(event.countryCode)
        }
    }

    sealed class UIEvent {
        object OnCloseDialog : UIEvent()

        object OnNavigateToOTPScreen : UIEvent()

        data class OnUserPasswordValueChange(val value: String) : UIEvent()
        data class OnUserEmailValueChange(val value: String) : UIEvent()
        data class OnInitializeBiometricPrompt(
            val biometricPromptTitle: String,
            val biometricPromptDescription: String,
            val biometricPromptNegative: String,
            val fragmentActivity: FragmentActivity
        ) : UIEvent()

        data class OnShowBiometricPromptForEncryption(val fragmentActivity: FragmentActivity) :
            UIEvent()

        data class OnShowBiometricPromptForDecryption(val fragmentActivity: FragmentActivity) :
            UIEvent()

        data class OnShowBiometricSignInChanged(val value: Boolean) : UIEvent()

        data class OnFingerprintCheckedChanged(
            val value: Boolean,
            val showDialog: Boolean,
            val is03Country: String
        ) : UIEvent()

        data class OnStart(
            val deviceName: String,
            val deviceType: String,
            val forceDeviceChange: Boolean
        ) : UIEvent()

        object OnValidateUserEmail : UIEvent()
        data class OnCallCognitoSignIn(val activity: FragmentActivity) : UIEvent()
        object OnNavigateToForgotPassword : UIEvent()
        object OnNavigateToSignUp : UIEvent()
        data class OnUpdateToastVisibility(val value: Boolean) : UIEvent()
        data class OnUpdateIso3Country(val iso3Country: String) : UIEvent()
        data class OnOpenWhatsappLink(val context: Context) : UIEvent()
        data class OnSetCountryCode(val countryCode: String) : UIEvent()
    }

    companion object {
        const val DEVICE_ID = "DeviceId"
        const val UNIQUE_ID = "UniqueId"
        const val BRAND = "Brand"
        const val MODEL = "Model"
        const val APP_VERSION = "AppVersion"
        const val IS_EMULATOR = "IsEmulator"
        const val DEVICE_NAME = "DeviceName"
        const val IP_ADDRESS = "IpAddress"
        const val FORCE = "Force"
        const val ISO3_COSTA_RICA = "CRI"
        const val GUEST_USER = "guest_user"
    }
}
