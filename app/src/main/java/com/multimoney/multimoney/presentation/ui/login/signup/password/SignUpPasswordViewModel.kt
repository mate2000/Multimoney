package com.multimoney.multimoney.presentation.ui.login.signup.password

import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.security.QueryValidationSecurityUseCase
import com.multimoney.domain.model.security.ValidateSecurity
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
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
import com.multimoney.multimoney.presentation.util.DialogParameters
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpPasswordViewModel @Inject constructor(
    val biometricHelper: BiometricHelper,
    private val dataStorePreferences: DataStorePreferences,
    private val queryValidationSecurityUseCase: QueryValidationSecurityUseCase
) : BaseViewModel() {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var biometricPromptTitle = ""
    private var biometricPromptDescription = ""
    private var biometricPromptNegative = ""
    private var biometricDialogDescription = ""
    private var biometricDialogSuccessDescription = ""
    private var biometricDialogFailureDescription = ""
    private var isBiometricAvailable = false

    // Events
    var onPasswordSaveEvents = MutableSharedFlow<MultimoneyResult<ValidateSecurity?>>()

    private fun onInitializeDialogTexts(
        biometricPromptTitle: String,
        biometricPromptDescription: String,
        biometricPromptNegative: String,
        biometricDialogDescription: String,
        biometricDialogSuccessDescription: String,
        biometricDialogFailureDescription: String
    ) {
        this.biometricPromptTitle = biometricPromptTitle
        this.biometricPromptDescription = biometricPromptDescription
        this.biometricPromptNegative = biometricPromptNegative
        this.biometricDialogDescription = biometricDialogDescription
        this.biometricDialogSuccessDescription = biometricDialogSuccessDescription
        this.biometricDialogFailureDescription = biometricDialogFailureDescription
    }

    private fun isFormValid(): Boolean {
        return uiState.oneLowercaseState ?: false && uiState.oneUppercaseState ?: false && uiState.oneNumberState ?: false &&
                uiState.oneCharacterState ?: false && passwordHasMinimumCharacters(uiState.password)
                && (uiState.confirmPassword == uiState.password) && !uiState.confirmPasswordError.first
    }

    private fun onPasswordValueChange(password: String, onContinueEnable: (isEnable: Boolean) -> Unit) {
        uiState = uiState.copy(password = password)
        validatePassword()
        onContinueEnable(isFormValid())
    }

    private fun onConfirmPasswordValueChange(confirmPassword: String, onContinueEnable: (isEnable: Boolean) -> Unit) {
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
                    R.string.sign_up_password_requirement_max_three_characters_or_number_consecutive
                )
            }
            noMoreThanThreeConsecutiveLetterOrNumber(uiState.password) -> {
                Pair(
                    true,
                    R.string.sign_up_password_requirement_max_three_characters_or_number_consecutive
                )
            }
            noMoreThanThreeLettersOrNumbers(uiState.password) -> {
                Pair(
                    true,
                    R.string.sign_up_password_requirement_max_three_characters_or_number_consecutive
                )
            }
            (uiState.password.isNotEmpty() && uiState.confirmPassword.isNotEmpty() && uiState.confirmPassword != uiState.password) -> {
                Pair(true, R.string.sign_up_password_confirm_password_error)
            }
            else -> {
                Pair(false, R.string.error_empty)
            }
        }
    }

    private fun onFingerprintCheckedChanged(value: Boolean, showDialog: Boolean) {
        uiState = uiState.copy(
            isFingerprintChecked = value,
            openDialogCustom = DialogParameters(
                title = R.string.active_biometric_title,
                description = biometricDialogDescription,
                isActive = mutableStateOf(showDialog),
                positiveText = R.string.active_biometric_positive_button_label,
                negativeText = R.string.active_biometric_negative_button_label,
                positiveAction = {
                    onFingerprintCheckedChanged(value = true, showDialog = false)
                },
                negativeAction = {
                    onFingerprintCheckedChanged(value = false, showDialog = false)
                },
                dismissAction = {
                    onFingerprintCheckedChanged(value = false, showDialog = false)
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
        lastName: String,
        phone: String,
        identification: String,
        pkUser: String,
        status: String,
        onSuccess: () -> Unit,
        onFailureWithDialog: (DialogParameters) -> Unit
    ) {
        val attrs = mapOf(
            AuthUserAttributeKey.email() to email,
            AuthUserAttributeKey.name() to firstName,
            AuthUserAttributeKey.middleName() to lastName,
            AuthUserAttributeKey.phoneNumber() to phone,
            AuthUserAttributeKey.custom(COGNITO_CUSTOM_IDENTIFICATION) to identification,
            AuthUserAttributeKey.custom(COGNITO_CUSTOM_PK_USER) to pkUser,
            AuthUserAttributeKey.custom(COGNITO_CUSTOM_STATUS) to status,
            AuthUserAttributeKey.custom(COGNITO_CUSTOM_ID_BRAND) to Brand.Revamp.id.toString()
        )
        val options = AuthSignUpOptions.builder()
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
                title = R.string.dialog_success_biometric_title,
                description = biometricDialogSuccessDescription,
                positiveText = R.string.dialog_success_biometric_positive_text,
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
                title = R.string.dialog_failure_biometric_title,
                description = biometricDialogFailureDescription,
                positiveText = R.string.dialog_failure_biometric_positive_text,
                isActive = mutableStateOf(true),
                positiveAction = {
                    onNextStep()
                },
                dismissAction = {
                    onNextStep()
                }
            ))
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
        //Fields
        var password: String = "",
        var passwordError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
        var confirmPassword: String = "",
        var confirmPasswordError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
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
            is OnInitializeDialogTexts -> onInitializeDialogTexts(
                uiEvent.biometricPromptTitle,
                uiEvent.biometricPromptDescription,
                uiEvent.biometricPromptNegative,
                uiEvent.biometricDialogDescription,
                uiEvent.biometricDialogSuccessDescription,
                uiEvent.biometricDialogFailureDescription,
            )
            is OnNextActionClick -> uiEvent.nextStepAction.invoke()
            is OnPasswordValueChange -> onPasswordValueChange(uiEvent.password, uiEvent.onContinueEnable)
            is OnConfirmPasswordValueChange -> onConfirmPasswordValueChange(
                uiEvent.confirmPassword,
                uiEvent.onContinueEnable
            )
            is OnCallCognitoSignUp -> signUp(
                uiEvent.email,
                uiEvent.firstName,
                uiEvent.lastName,
                uiEvent.phone,
                uiEvent.identification,
                uiEvent.pkUser,
                uiEvent.status,
                uiEvent.onSuccess,
                uiEvent.onFailureWithDialog
            )
            is OnValidForm -> uiEvent.onContinueEnable(isFormValid())
            is OnCallPasswordSave -> callQuerySavePassword(uiEvent.pkUser, uiEvent.user, uiEvent.idBrant)
            is OnFingerprintCheckedChanged -> onFingerprintCheckedChanged(uiEvent.value, uiEvent.showDialog)
            is OnShowBiometricPromptForEncryption -> onShowBiometricPromptForEncryption(
                uiEvent.fragmentActivity,
                uiEvent.userEmail,
                uiEvent.userName,
                uiEvent.onNextStep
            )
            is OnIsBiometricAvailable -> isBiometricAvailable = uiEvent.value
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
            val lastName: String,
            val phone: String,
            val identification: String,
            val pkUser: String,
            val status: String,
            val onSuccess: () -> Unit,
            val onFailureWithDialog: (DialogParameters) -> Unit
        ) : UIEvent()

        data class OnCallPasswordSave(
            val pkUser: String,
            val user: String,
            val idBrant: Int
        ) : UIEvent()

        data class OnValidForm(val onContinueEnable: (isEnable: Boolean) -> Unit) : UIEvent()

        data class OnFingerprintCheckedChanged(
            val value: Boolean,
            val showDialog: Boolean
        ) : UIEvent()

        data class OnInitializeDialogTexts(
            val biometricPromptTitle: String,
            val biometricPromptDescription: String,
            val biometricPromptNegative: String,
            val biometricDialogDescription: String,
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
    }
}