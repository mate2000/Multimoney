package com.multimoney.multimoney.presentation.ui.login.registereduser.password

import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.security.QueryValidationSecurityUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.Screen.SignInScreen
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.LAST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.STATUS
import com.multimoney.multimoney.presentation.ui.login.registereduser.password.RegisteredUserPasswordViewModel.UIEvent.OnCallCognitoSignUp
import com.multimoney.multimoney.presentation.ui.login.registereduser.password.RegisteredUserPasswordViewModel.UIEvent.OnCallPasswordSave
import com.multimoney.multimoney.presentation.ui.login.registereduser.password.RegisteredUserPasswordViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.login.registereduser.password.RegisteredUserPasswordViewModel.UIEvent.OnConfirmPasswordValueChange
import com.multimoney.multimoney.presentation.ui.login.registereduser.password.RegisteredUserPasswordViewModel.UIEvent.OnFingerprintCheckedChanged
import com.multimoney.multimoney.presentation.ui.login.registereduser.password.RegisteredUserPasswordViewModel.UIEvent.OnInitializeDialogTexts
import com.multimoney.multimoney.presentation.ui.login.registereduser.password.RegisteredUserPasswordViewModel.UIEvent.OnIsBiometricAvailable
import com.multimoney.multimoney.presentation.ui.login.registereduser.password.RegisteredUserPasswordViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.login.registereduser.password.RegisteredUserPasswordViewModel.UIEvent.OnPasswordValueChange
import com.multimoney.multimoney.presentation.ui.login.registereduser.password.RegisteredUserPasswordViewModel.UIEvent.OnShowBiometricPromptForEncryption
import com.multimoney.multimoney.presentation.ui.login.registereduser.password.RegisteredUserPasswordViewModel.UIEvent.OnValidForm
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisteredUserPasswordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
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
    private var biometricDialogDescription = ""
    private var biometricDialogSuccessDescription = ""
    private var biometricDialogFailureDescription = ""
    private var isBiometricAvailable = false
    var email: String = ""
    private var pkUser: Long = 0
    private var identification: String = ""
    private var status: String = ""
    private var idBrand: Int = 0
    var firstName: String = ""
    var lastName: String = ""
    private var phone: String = ""

    init {
        email = savedStateHandle[EMAIL] ?: ""
        pkUser = savedStateHandle[PK_USER] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        status = savedStateHandle[STATUS] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        firstName = savedStateHandle[FIRST_NAME] ?: ""
        lastName = savedStateHandle[LAST_NAME] ?: ""
        phone = savedStateHandle[PHONE_NUMBER] ?: ""
    }

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
            uiState.oneCharacterState ?: false && passwordHasMinimumCharacters(uiState.password) &&
            (uiState.confirmPassword == uiState.password) && !uiState.confirmPasswordError.first
    }

    private fun onPasswordValueChange(password: String) {
        uiState = uiState.copy(password = password)
        validatePassword()
        uiState = uiState.copy(isContinueEnabled = isFormValid())
    }

    private fun onConfirmPasswordValueChange(confirmPassword: String) {
        uiState = uiState.copy(confirmPassword = confirmPassword)
        validatePassword()
        uiState = uiState.copy(isContinueEnabled = isFormValid())
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

    private fun onFingerprintCheckedChanged(value: Boolean, showDialog: Boolean) {
        uiState = uiState.copy(
            isFingerprintChecked = value,
            openDialogCustom = DialogParameters(
                titleResource = string.active_biometric_title,
                description = biometricDialogDescription,
                isActive = mutableStateOf(showDialog),
                positiveResource = string.active_biometric_positive_button_label,
                negativeResource = string.active_biometric_negative_button_label,
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
            result.onSuccess {
                onUIEvent(
                    OnCallCognitoSignUp(
                        email = email,
                        firstName = firstName,
                        secondName = "",
                        lastName = lastName,
                        phone = phone,
                        identification = identification,
                        pkUser = pkUser,
                        status = status,
                        idBrand = idBrand,
                        onFailureWithDialog = { dialog ->
                            uiState = uiState.copy(isLoading = false, openDialogCustom = dialog)
                        }
                    )
                )
            }.onMessage {
                uiState = uiState.copy(
                    isLoading = false,
                    openDialogCustom = DialogParameters(
                        description = it?.messageError?.message ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    openDialogCustom = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
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
            AuthUserAttributeKey.custom(COGNITO_CUSTOM_ID_BRAND) to idBrand.toString()
        )
        val options = AuthSignUpOptions.builder()
            .userAttributes(attrs.map { AuthUserAttribute(it.key, it.value) })
            .build()
        Amplify.Auth.signUp(email, uiState.password, options, {
            uiState = uiState.copy(isLoading = false)
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

    private fun showBiometricSuccess() {
        uiState = uiState.copy(
            openDialogCustom = DialogParameters(
                titleResource = string.dialog_success_biometric_title,
                description = biometricDialogSuccessDescription,
                positiveResource = string.dialog_success_biometric_positive_text,
                isActive = mutableStateOf(true),
                positiveAction = {
                    completedProcessAction()
                },
                dismissAction = {
                    completedProcessAction()
                }
            )
        )
    }

    private fun showBiometricsFailed() {
        uiState = uiState.copy(
            openDialogCustom = DialogParameters(
                titleResource = string.dialog_failure_biometric_title,
                description = biometricDialogFailureDescription,
                positiveResource = string.dialog_failure_biometric_positive_text,
                isActive = mutableStateOf(true),
                positiveAction = {
                    completedProcessAction()
                },
                dismissAction = {
                    completedProcessAction()
                }
            )
        )
    }

    private fun biometricPromptForEncryptionSuccess(
        result: BiometricPrompt.AuthenticationResult,
        userEmail: String,
        userPassword: String,
        userName: String
    ) {
        result.cryptoObject?.cipher?.apply {
            viewModelScope.launch {
                dataStorePreferences.setUserEmail(userEmail)
                dataStorePreferences.setUserName(userName)
                dataStorePreferences.setUserPassword(userPassword, this@apply)
                dataStorePreferences.isBiometricsEnabled(true)
                showBiometricSuccess()
            }
        }
    }

    private fun onShowBiometricPromptForEncryption(
        fragmentActivity: FragmentActivity,
        userEmail: String,
        userName: String
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
                        userName
                    )
                },
                processError = { _, _ ->
                    showBiometricsFailed()
                }
            )
        } else {
            completedProcessAction()
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

    private fun completedProcessAction() = popAndNavigateTo(
        route = Screen.SignUpCompleted.route,
        popTo = Screen.RegisteredUserPassword.route
    )

    private fun onCloseClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        uiState = uiState.copy(
            openDialogCustom = DialogParameters(
                titleResource = string.general_close_dialog_title,
                descriptionResource = string.sign_up_close_dialog_description,
                positiveResource = string.sign_up_close_dialog_positive_button_text,
                negativeResource = string.sign_up_close_dialog_negative_button_text,
                positiveAction = {
                    popAndNavigateTo(
                        route = SignInScreen.route,
                        popTo = Screen.RegisteredUserPassword.route
                    )
                },
                isActive = mutableStateOf(true)
            )
        )
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
        var isLoading: Boolean = false,
        var isFingerprintChecked: Boolean = false,
        var isContinueEnabled: Boolean = false,
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
                uiEvent.biometricDialogFailureDescription
            )
            is OnNextActionClick -> uiEvent.nextStepAction.invoke()
            is OnPasswordValueChange -> onPasswordValueChange(uiEvent.password)
            is OnConfirmPasswordValueChange -> onConfirmPasswordValueChange(uiEvent.confirmPassword)
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
                uiEvent.onFailureWithDialog
            )
            is OnValidForm -> uiEvent.onContinueEnable(isFormValid())
            is OnCallPasswordSave -> callQuerySavePassword(uiEvent.pkUser, uiEvent.user, uiEvent.idBrant)
            is OnFingerprintCheckedChanged -> onFingerprintCheckedChanged(uiEvent.value, uiEvent.showDialog)
            is OnShowBiometricPromptForEncryption -> onShowBiometricPromptForEncryption(
                uiEvent.fragmentActivity,
                uiEvent.userEmail,
                uiEvent.userName
            )
            is OnIsBiometricAvailable -> isBiometricAvailable = uiEvent.value
            is OnCloseClick -> onCloseClick(focusManager = uiEvent.focusManager)
        }
    }

    sealed class UIEvent {
        data class OnPasswordValueChange(
            val password: String
        ) : UIEvent()

        data class OnConfirmPasswordValueChange(val confirmPassword: String) : UIEvent()

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
            val userName: String
        ) : UIEvent()

        data class OnIsBiometricAvailable(val value: Boolean) : UIEvent()
        data class OnCloseClick(val focusManager: FocusManager) : UIEvent()
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
