package com.multimoney.multimoney.presentation.ui.login.signup.password

import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.options.AWSCognitoAuthSignUpOptions
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.security.QueryValidationSecurityUseCase
import com.multimoney.domain.model.security.ValidateSecurity
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.password.SignUpPasswordViewModel.UIEvent.OnCallCognitoSignUp
import com.multimoney.multimoney.presentation.ui.login.signup.password.SignUpPasswordViewModel.UIEvent.OnCallPasswordSave
import com.multimoney.multimoney.presentation.ui.login.signup.password.SignUpPasswordViewModel.UIEvent.OnConfirmPasswordValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.password.SignUpPasswordViewModel.UIEvent.OnFingerprintCheckedChanged
import com.multimoney.multimoney.presentation.ui.login.signup.password.SignUpPasswordViewModel.UIEvent.OnInitializeDialogTexts
import com.multimoney.multimoney.presentation.ui.login.signup.password.SignUpPasswordViewModel.UIEvent.OnIsBiometricAvailable
import com.multimoney.multimoney.presentation.ui.login.signup.password.SignUpPasswordViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.login.signup.password.SignUpPasswordViewModel.UIEvent.OnPasswordValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.password.SignUpPasswordViewModel.UIEvent.OnShowBiometricPromptForEncryption
import com.multimoney.multimoney.presentation.ui.login.signup.password.SignUpPasswordViewModel.UIEvent.OnValidForm
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.checkIfEmulator
import com.multimoney.multimoney.presentation.util.getAppVersion
import com.multimoney.multimoney.presentation.util.getDeviceBrand
import com.multimoney.multimoney.presentation.util.getDeviceModel
import com.multimoney.multimoney.presentation.util.noMoreThanThreeConsecutiveLetterOrNumber
import com.multimoney.multimoney.presentation.util.noMoreThanThreeEqualConsecutiveLetterOrNumber
import com.multimoney.multimoney.presentation.util.noMoreThanThreeLettersOrNumbers
import com.multimoney.multimoney.presentation.util.passwordHasALowercaseLetterValidation
import com.multimoney.multimoney.presentation.util.passwordHasANumberValidation
import com.multimoney.multimoney.presentation.util.passwordHasAUppercaseLetterValidation
import com.multimoney.multimoney.presentation.util.passwordHasMinimumCharacters
import com.multimoney.multimoney.presentation.util.passwordHasSpecialCharacterValidation
import com.multimoney.multimoney.util.BiometricHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpPasswordViewModel @Inject constructor(
    val biometricHelper: BiometricHelper,
    private val dataStorePreferences: DataStorePreferences,
    private val queryValidationSecurityUseCase: QueryValidationSecurityUseCase
) : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var biometricPromptTitle = ""
    private var biometricPromptDescription = ""
    private var biometricPromptNegative = ""
    private var biometricDialogSuccessDescription = ""
    private var biometricDialogFailureDescription = ""
    private var isBiometricAvailable = false
    private var deviceId = ""
    private var uniqueId = ""
    private var ipAddress = ""
    private var deviceType = ""
    private var deviceName = ""
    private var appVersion = getAppVersion()
    private var deviceBrand = getDeviceBrand()
    private var deviceModel = getDeviceModel()
    private var isEmulator = checkIfEmulator()

    // Events
    var onPasswordSaveEvents = MutableSharedFlow<MultimoneyResult<ValidateSecurity?>>()

    private fun onInitializeDialogTexts(
        biometricPromptTitle: String,
        biometricPromptDescription: String,
        biometricPromptNegative: String,
        biometricDialogSuccessDescription: String,
        biometricDialogFailureDescription: String
    ) {
        this.biometricPromptTitle = biometricPromptTitle
        this.biometricPromptDescription = biometricPromptDescription
        this.biometricPromptNegative = biometricPromptNegative
        this.biometricDialogSuccessDescription = biometricDialogSuccessDescription
        this.biometricDialogFailureDescription = biometricDialogFailureDescription
    }

    private fun isFormValid(): Boolean {
        return uiState.oneLowercaseState ?: false && uiState.oneUppercaseState ?: false && uiState.oneNumberState ?: false &&
                uiState.oneCharacterState ?: false && passwordHasMinimumCharacters(uiState.password) &&
                (uiState.confirmPassword == uiState.password) && !uiState.confirmPasswordError.first
    }

    private fun onSetupDeviceInfo(
        ipAddress: String,
        deviceName: String,
        deviceType: String,
    ) {
        viewModelScope.launch {
            deviceId = dataStorePreferences.getDeviceId().first()
            uniqueId = dataStorePreferences.getUniqueId().first()
        }
        this.ipAddress = ipAddress
        this.deviceName = deviceName
        this.deviceType = deviceType
    }

    private fun onPasswordValueChange(
        password: String,
        onContinueEnable: (isEnable: Boolean) -> Unit
    ) {
        uiState = uiState.copy(password = password)
        validatePassword()
        onContinueEnable(isFormValid())
    }

    private fun onConfirmPasswordValueChange(
        confirmPassword: String,
        onContinueEnable: (isEnable: Boolean) -> Unit
    ) {
        uiState = uiState.copy(confirmPassword = confirmPassword)
        validatePassword()
        onContinueEnable(isFormValid())
    }

    private fun validatePassword() {
        uiState = uiState.copy(
            eightCharactersMinimumState = passwordHasMinimumCharacters(uiState.password),
            oneUppercaseState = passwordHasAUppercaseLetterValidation(uiState.password) && uiState.password.isNotEmpty(),
            oneLowercaseState = passwordHasALowercaseLetterValidation(uiState.password) && uiState.password.isNotEmpty(),
            oneNumberState = passwordHasANumberValidation(uiState.password) && uiState.password.isNotEmpty(),
            oneCharacterState = passwordHasSpecialCharacterValidation(uiState.password) && uiState.password.isNotEmpty(),
            confirmPasswordError = validateHasTheSameConsecutiveCharacter()
        )
        resetValidationLabel(uiState.password)
    }

    private fun validateHasTheSameConsecutiveCharacter(): Pair<Boolean, Int> {
        return when {
            noMoreThanThreeEqualConsecutiveLetterOrNumber(uiState.password) -> {
                Pair(
                    true,
                    string.sign_up_password_requirement_max_three_characters_or_number_consecutive
                )
            }
            noMoreThanThreeConsecutiveLetterOrNumber(uiState.password) -> {
                Pair(
                    true,
                    string.sign_up_password_requirement_max_three_characters_or_number_consecutive
                )
            }
            noMoreThanThreeLettersOrNumbers(uiState.password) -> {
                Pair(
                    true,
                    string.sign_up_password_requirement_max_three_characters_or_number_consecutive
                )
            }
            (uiState.password.isNotEmpty() && uiState.confirmPassword.isNotEmpty() && uiState.confirmPassword != uiState.password) -> {
                Pair(true, string.sign_up_password_confirm_password_error)
            }
            else -> {
                Pair(false, string.error_empty)
            }
        }
    }

    private fun onFingerprintCheckedChanged(value: Boolean, showDialog: Boolean, idBrand: Int) {
        uiState = uiState.copy(
            isFingerprintChecked = value,
            openDialogCustom = DialogParameters(
                titleResource = if (idBrand == Brand.CostaRica.id) string.active_biometric_title_cr else string.active_biometric_title,
                descriptionResource = if (idBrand == Brand.CostaRica.id) string.active_biometric_message_cr else string.active_biometric_message,
                isActive = mutableStateOf(showDialog),
                positiveResource = string.active_biometric_positive_button_label,
                negativeResource = string.active_biometric_negative_button_label,
                positiveAction = {
                    onFingerprintCheckedChanged(value = true, showDialog = false, idBrand)
                },
                negativeAction = {
                    onFingerprintCheckedChanged(value = false, showDialog = false, idBrand)
                },
                dismissAction = {
                    onFingerprintCheckedChanged(value = false, showDialog = false, idBrand)
                }
            )
        )
    }

    private fun callQuerySavePassword(pkUser: String, user: String, idBrant: Int) = executeUseCase {
        queryValidationSecurityUseCase.invoke(
            pkUser = pkUser,
            password = uiState.password,
            user = user,
            idBrand = idBrant
        ).collectLatest { result ->
            onPasswordSaveEvents.emit(result)
        }
    }

    private fun signUp(
        email: String,
        firstName: String,
        secondName: String,
        lastName: String,
        phone: String,
        identification: String,
        pkUser: String,
        status: String,
        idBrand: Int,
        onSuccess: () -> Unit,
        onFailureWithDialog: (DialogParameters) -> Unit
    ) {
        val attrs = mapOf(
            AuthUserAttributeKey.email() to email,
            AuthUserAttributeKey.name() to firstName,
            AuthUserAttributeKey.middleName() to secondName,
            AuthUserAttributeKey.familyName() to lastName,
            AuthUserAttributeKey.phoneNumber() to phone,
            AuthUserAttributeKey.custom(COGNITO_CUSTOM_IDENTIFICATION) to identification,
            AuthUserAttributeKey.custom(COGNITO_CUSTOM_PK_USER) to pkUser,
            AuthUserAttributeKey.custom(COGNITO_CUSTOM_STATUS) to status,
            AuthUserAttributeKey.custom(COGNITO_CUSTOM_ID_BRAND) to idBrand.toString(),
        )
        val metaData = mapOf(
            COGNITO_DEVICE_ID to deviceId,
            COGNITO_BRAND to deviceBrand,
            COGNITO_UNIQUE_ID to uniqueId,
            COGNITO_MODEL to deviceModel,
            COGNITO_DEVICE_NAME to deviceName,
            COGNITO_APP_VERSION to appVersion,
            COGNITO_IS_EMULATOR to isEmulator.toString(),
            COGNITO_IP_ADDRESS to ipAddress,
        )

        val options = AWSCognitoAuthSignUpOptions.builder().validationData(metaData)
            .userAttributes(attrs.map { AuthUserAttribute(it.key, it.value) })
            .build()
        Amplify.Auth.signUp(email, uiState.password, options, {
            onSuccess()
            emitBaseEvent(BaseEvent.OnOpenBiometricDialog)
        }, {
            onFailureWithDialog(
                DialogParameters(
                    description = it.localizedMessage ?: "",
                    isActive = mutableStateOf(true)
                )
            )
        })
    }

    private fun showBiometricSuccess(onNextStep: () -> Unit) {
        uiState = uiState.copy(
            openDialogCustom = DialogParameters(
                titleResource = string.dialog_success_biometric_title,
                description = biometricDialogSuccessDescription,
                positiveResource = string.dialog_success_biometric_positive_text,
                isActive = mutableStateOf(true),
                positiveAction = {
                    onNextStep()
                },
                dismissAction = {
                    onNextStep()
                }
            )
        )
    }

    private fun showBiometricsFailed(onNextStep: () -> Unit) {
        uiState = uiState.copy(
            openDialogCustom = DialogParameters(
                titleResource = string.dialog_failure_biometric_title,
                description = biometricDialogFailureDescription,
                positiveResource = string.dialog_failure_biometric_positive_text,
                isActive = mutableStateOf(true),
                positiveAction = {
                    onNextStep()
                },
                dismissAction = {
                    onNextStep()
                }
            )
        )
    }

    private fun biometricPromptError(
        errorCode: Int,
        errString: CharSequence,
        onNextStep: () -> Unit
    ) {
        showBiometricsFailed(onNextStep)
    }

    private fun biometricPromptForEncryptionSuccess(
        result: BiometricPrompt.AuthenticationResult,
        userEmail: String,
        userPassword: String,
        userName: String,
        onNextStep: () -> Unit
    ) {
        result.cryptoObject?.cipher?.apply {
            viewModelScope.launch {
                dataStorePreferences.setUserEmail(userEmail)
                dataStorePreferences.setUserName(userName)
                dataStorePreferences.setUserPassword(userPassword, this@apply)
                dataStorePreferences.isBiometricsEnabled(true)
                showBiometricSuccess(onNextStep)
            }
        }
    }

    private fun onShowBiometricPromptForEncryption(
        fragmentActivity: FragmentActivity,
        userEmail: String,
        userName: String,
        onNextStep: () -> Unit
    ) {
        if (isBiometricAvailable && uiState.isFingerprintChecked) {
            biometricHelper.showBiometricPrompt(
                title = biometricPromptTitle,
                description = biometricPromptDescription,
                negative = biometricPromptNegative,
                activity = fragmentActivity,
                processSuccess = { result ->
                    biometricPromptForEncryptionSuccess(
                        result,
                        userEmail,
                        uiState.password,
                        userName,
                        onNextStep
                    )
                },
                processError = { errorCode, errString ->
                    biometricPromptError(
                        errorCode,
                        errString,
                        onNextStep
                    )
                }
            )
        } else {
            onNextStep()
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

    data class UIState(
        // Fields
        var password: String = "",
        var passwordError: Pair<Boolean, Int> = Pair(false, string.error_empty),
        var confirmPassword: String = "",
        var confirmPasswordError: Pair<Boolean, Int> = Pair(false, string.error_empty),
        var eightCharactersMinimumState: Boolean? = null,
        var oneUppercaseState: Boolean? = null,
        var oneLowercaseState: Boolean? = null,
        var oneNumberState: Boolean? = null,
        var oneCharacterState: Boolean? = null,
        var isFingerprintChecked: Boolean = false,
        val openDialogCustom: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnInitializeDialogTexts -> onInitializeDialogTexts(
                uiEvent.biometricPromptTitle,
                uiEvent.biometricPromptDescription,
                uiEvent.biometricPromptNegative,
                uiEvent.biometricDialogSuccessDescription,
                uiEvent.biometricDialogFailureDescription
            )
            is OnNextActionClick -> uiEvent.nextStepAction.invoke()
            is OnPasswordValueChange -> onPasswordValueChange(
                uiEvent.password,
                uiEvent.onContinueEnable
            )
            is OnConfirmPasswordValueChange -> onConfirmPasswordValueChange(
                uiEvent.confirmPassword,
                uiEvent.onContinueEnable
            )
            is OnCallCognitoSignUp -> signUp(
                uiEvent.email,
                uiEvent.firstName,
                uiEvent.secondName,
                uiEvent.lastName,
                uiEvent.phone,
                uiEvent.identification,
                uiEvent.pkUser,
                uiEvent.status,
                uiEvent.idBrand,
                uiEvent.onSuccess,
                uiEvent.onFailureWithDialog
            )
            is OnValidForm -> uiEvent.onContinueEnable(isFormValid())
            is OnCallPasswordSave -> callQuerySavePassword(
                uiEvent.pkUser,
                uiEvent.user,
                uiEvent.idBrant
            )
            is OnFingerprintCheckedChanged -> onFingerprintCheckedChanged(
                uiEvent.value,
                uiEvent.showDialog,
                uiEvent.idBrand
            )
            is OnShowBiometricPromptForEncryption -> onShowBiometricPromptForEncryption(
                uiEvent.fragmentActivity,
                uiEvent.userEmail,
                uiEvent.userName,
                uiEvent.onNextStep
            )
            is OnIsBiometricAvailable -> isBiometricAvailable = uiEvent.value
            is UIEvent.OnSetupDeviceInfo -> onSetupDeviceInfo(
                uiEvent.ipAddress,
                uiEvent.deviceName,
                uiEvent.deviceType
            )
        }
    }

    sealed class UIEvent {
        data class OnPasswordValueChange(
            val password: String,
            val onContinueEnable: (isEnable: Boolean) -> Unit
        ) : UIEvent()

        data class OnConfirmPasswordValueChange(
            val confirmPassword: String,
            val onContinueEnable: (isEnable: Boolean) -> Unit
        ) : UIEvent()

        data class OnNextActionClick(val nextStepAction: () -> Unit) : UIEvent()
        data class OnCallCognitoSignUp(
            val email: String,
            val firstName: String,
            val secondName: String,
            val lastName: String,
            val phone: String,
            val identification: String,
            val pkUser: String,
            val status: String,
            val idBrand: Int,
            val onSuccess: () -> Unit,
            val onFailureWithDialog: (DialogParameters) -> Unit
        ) : UIEvent()

        data class OnCallPasswordSave(
            val pkUser: String,
            val user: String,
            val idBrant: Int
        ) : UIEvent()

        data class OnSetupDeviceInfo(
            val ipAddress: String,
            val deviceName: String,
            val deviceType: String
        ) : UIEvent()

        data class OnValidForm(val onContinueEnable: (isEnable: Boolean) -> Unit) : UIEvent()

        data class OnFingerprintCheckedChanged(
            val value: Boolean,
            val showDialog: Boolean,
            val idBrand: Int
        ) : UIEvent()

        data class OnInitializeDialogTexts(
            val biometricPromptTitle: String,
            val biometricPromptDescription: String,
            val biometricPromptNegative: String,
            val biometricDialogSuccessDescription: String,
            val biometricDialogFailureDescription: String
        ) : UIEvent()

        data class OnShowBiometricPromptForEncryption(
            val fragmentActivity: FragmentActivity,
            val userEmail: String,
            val userName: String,
            val onNextStep: () -> Unit
        ) : UIEvent()

        data class OnIsBiometricAvailable(val value: Boolean) : UIEvent()
    }

    sealed class BaseEvent {
        object OnOpenBiometricDialog : BaseEvent()
    }

    companion object {
        const val COGNITO_CUSTOM_IDENTIFICATION = "custom:Identification"
        const val COGNITO_CUSTOM_PK_USER = "custom:PkUser"
        const val COGNITO_CUSTOM_STATUS = "custom:Status"
        const val COGNITO_CUSTOM_ID_BRAND = "custom:IdBrand"
        const val COGNITO_CHANGE_PASSWORD_REQUIRED = "passwordChangeRequired"
        const val COGNITO_DEVICE_ID = "DeviceId"
        const val COGNITO_UNIQUE_ID = "UniqueId"
        const val COGNITO_BRAND = "Brand"
        const val COGNITO_MODEL = "Model"
        const val COGNITO_APP_VERSION = "AppVersion"
        const val COGNITO_IS_EMULATOR = "IsEmulator"
        const val COGNITO_DEVICE_NAME = "DeviceName"
        const val COGNITO_IP_ADDRESS = "IpAddress"
    }
}
