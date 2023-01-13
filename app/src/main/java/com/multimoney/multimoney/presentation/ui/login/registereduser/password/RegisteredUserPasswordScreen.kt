package com.multimoney.multimoney.presentation.ui.login.registereduser.password

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.registereduser.password.RegisteredUserPasswordViewModel.BaseEvent.OnOpenBiometricDialog
import com.multimoney.multimoney.presentation.ui.login.registereduser.password.RegisteredUserPasswordViewModel.UIEvent.OnCallPasswordSave
import com.multimoney.multimoney.presentation.ui.login.registereduser.password.RegisteredUserPasswordViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.login.registereduser.password.RegisteredUserPasswordViewModel.UIEvent.OnConfirmPasswordValueChange
import com.multimoney.multimoney.presentation.ui.login.registereduser.password.RegisteredUserPasswordViewModel.UIEvent.OnFingerprintCheckedChanged
import com.multimoney.multimoney.presentation.ui.login.registereduser.password.RegisteredUserPasswordViewModel.UIEvent.OnIsBiometricAvailable
import com.multimoney.multimoney.presentation.ui.login.registereduser.password.RegisteredUserPasswordViewModel.UIEvent.OnPasswordValueChange
import com.multimoney.multimoney.presentation.ui.login.registereduser.password.RegisteredUserPasswordViewModel.UIEvent.OnShowBiometricPromptForEncryption
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomCheckBox
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.CustomPasswordRequirementLabel
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun RegisteredUserPasswordScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: RegisteredUserPasswordViewModel = hiltViewModel()
) {
    // Properties
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val fragmentActivity = LocalContext.current as FragmentActivity

    viewModel.onUIEvent(
        RegisteredUserPasswordViewModel.UIEvent.OnInitializeDialogTexts(
            biometricPromptTitle = stringResource(id = string.biometric_dialog_title),
            biometricPromptDescription = stringResource(id = string.biometric_dialog_description),
            biometricPromptNegative = stringResource(id = string.cancel),
            biometricDialogDescription = stringResource(id = string.active_biometric_message),
            biometricDialogSuccessDescription = stringResource(id = string.dialog_success_biometric_description),
            biometricDialogFailureDescription = stringResource(id = string.dialog_failure_biometric_description)
        )
    )

    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate)
        viewModel.apply {
            onUIEvent(
                OnIsBiometricAvailable(biometricHelper.isBiometricAvailable(context))
            )
        }
        viewModel.baseEvent.collect { event ->
            when (event) {
                is OnOpenBiometricDialog -> viewModel.onUIEvent(
                    OnShowBiometricPromptForEncryption(
                        fragmentActivity = fragmentActivity,
                        userEmail = viewModel.userData?.email ?: "",
                        userName = "${viewModel.userData?.firstName} ${viewModel.userData?.firstLastName}"
                    )
                )
            }
        }
    }

    // Content
    RegisteredUserPasswordContent(
        onCloseClick = { viewModel.onUIEvent(OnCloseClick(focusManager = focusManager)) },
        viewModel.uiState.password,
        viewModel.uiState.passwordError,
        onPasswordChange = { viewModel.onUIEvent(OnPasswordValueChange(it)) },
        viewModel.uiState.confirmPassword,
        viewModel.uiState.confirmPasswordError,
        onConfirmPasswordChange = { viewModel.onUIEvent(OnConfirmPasswordValueChange(it)) },
        viewModel.uiState.eightCharactersMinimumState,
        viewModel.uiState.oneUppercaseState,
        viewModel.uiState.oneLowercaseState,
        viewModel.uiState.oneNumberState,
        viewModel.uiState.oneCharacterState,
        viewModel.biometricHelper.isBiometricAvailable(context),
        viewModel.uiState.isFingerprintChecked,
        onFingerprintCheckedChanged = { value, showDialog ->
            viewModel.onUIEvent(OnFingerprintCheckedChanged(value, showDialog))
        },
        viewModel.uiState.isContinueEnabled,
        onContinueClick = {
            viewModel.onUIEvent(OnCallPasswordSave)
        }
    )

    LoadingIndicator(viewModel.uiState.isLoading)

    BackHandler {
        // empty to block system back
    }

    // Dialog
    if (viewModel.uiState.openDialogCustom.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialogCustom.titleResource),
            message = viewModel.uiState.openDialogCustom.description,
            positiveButtonText = stringResource(id = viewModel.uiState.openDialogCustom.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialogCustom.negativeResource),
            onPositiveAction = viewModel.uiState.openDialogCustom.positiveAction,
            onNegativeAction = viewModel.uiState.openDialogCustom.negativeAction,
            onDismissAction = viewModel.uiState.openDialogCustom.dismissAction,
            openDialogCustom = viewModel.uiState.openDialogCustom.isActive
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
fun RegisteredUserPasswordContent(
    onCloseClick: () -> Unit = {},
    passwordValue: String = "",
    passwordError: Pair<Boolean, Int> = Pair(false, string.empty),
    onPasswordChange: (String) -> Unit = {},
    confirmPasswordValue: String = "",
    confirmPasswordError: Pair<Boolean, Int> = Pair(false, string.empty),
    onConfirmPasswordChange: (String) -> Unit = {},
    eightCharactersMinimumState: Boolean? = null,
    oneUppercaseState: Boolean? = null,
    oneLowercaseState: Boolean? = null,
    oneNumberState: Boolean? = null,
    oneCharacterState: Boolean? = null,
    isBiometricAvailable: Boolean = false,
    isFingerprintChecked: Boolean = false,
    onFingerprintCheckedChanged: (Boolean, Boolean) -> Unit = { _, _ -> },
    isContinueEnabled: Boolean = false,
    onContinueClick: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.fillMaxSize().background(MultimoneyTheme.colors.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            TopNavBar(
                isLeftButtonVisible = false,
                onRightButtonClick = onCloseClick
            )
            Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp)) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = Typography.h6.toSpanStyle().copy(
                                color = MultimoneyTheme.colors.labelText,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(stringResource(id = string.sign_up_password_title))
                        }
                    },
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 24.sp
                )
                CustomOutlinedTextField(
                    value = passwordValue,
                    onValueChange = {
                        onPasswordChange(it)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.clearFocus()
                    }),
                    labelText = stringResource(id = string.sign_up_label_password),
                    isPassword = true,
                    modifier = Modifier.padding(top = 24.dp),
                    isRequired = true,
                    isRequiredMessage = stringResource(id = string.sign_up_password_required),
                    isError = passwordError.first,
                    errorMessage = if (passwordError.first) {
                        stringResource(id = passwordError.second)
                    } else {
                        null
                    }
                )
                CustomOutlinedTextField(
                    value = confirmPasswordValue,
                    onValueChange = {
                        onConfirmPasswordChange(
                            it
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    labelText = stringResource(id = string.sign_up_label_confirm_password),
                    isPassword = true,
                    modifier = Modifier.padding(top = 16.dp),
                    isRequired = true,
                    isRequiredMessage = stringResource(id = string.sign_up_password_required),
                    isError = confirmPasswordError.first,
                    errorMessage = if (confirmPasswordError.first) {
                        stringResource(id = confirmPasswordError.second)
                    } else {
                        null
                    }
                )
                FlowRow(
                    Modifier.padding(top = 8.dp)
                ) {
                    PasswordRequirementLabels(
                        text = stringResource(id = string.sign_up_password_requirement_eight_characters_minimum),
                        state = eightCharactersMinimumState
                    )
                    PasswordRequirementLabels(
                        text = stringResource(id = string.sign_up_password_requirement_one_uppercase),
                        state = oneUppercaseState
                    )
                    PasswordRequirementLabels(
                        text = stringResource(id = string.sign_up_password_requirement_one_lowercase),
                        state = oneLowercaseState
                    )
                    PasswordRequirementLabels(
                        text = stringResource(id = string.sign_up_password_requirement_one_number),
                        state = oneNumberState
                    )
                    PasswordRequirementLabels(
                        text = stringResource(id = string.sign_up_password_requirement_one_characer),
                        state = oneCharacterState
                    )
                }
                if (isBiometricAvailable) {
                    CustomCheckBox(
                        checked = isFingerprintChecked,
                        onCheckedChange = {
                            onFingerprintCheckedChanged(it, it)
                        },
                        text = stringResource(id = string.sign_in_activate_fingerprint),
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }

        CustomButton(
            onClick = onContinueClick,
            text = stringResource(id = string.button_continue),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 32.dp, top = 16.dp).fillMaxWidth()
                .height(48.dp),
            buttonType = PrimaryPrimary,
            enable = isContinueEnabled
        )
    }
}

@Composable
fun PasswordRequirementLabels(modifier: Modifier = Modifier, text: String, state: Boolean?) {
    CustomPasswordRequirementLabel(
        modifier,
        text = text,
        successIcon = drawable.ic_check,
        errorIcon = drawable.ic_error_password,
        state = state
    )
}
