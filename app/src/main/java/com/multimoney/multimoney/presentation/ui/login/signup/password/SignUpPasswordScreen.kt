package com.multimoney.multimoney.presentation.ui.login.signup.password

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
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
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.uielement.CustomCheckBox
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.CustomPasswordRequirementLabel
import com.multimoney.multimoney.presentation.util.DialogParameters

@Composable
@Preview
fun SignUpPasswordScreen(
    viewModel: SignUpPasswordViewModel = hiltViewModel(),
    sharedViewModel: SignUpViewModel = hiltViewModel()
) {

    // Properties
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val fragmentActivity = LocalContext.current as FragmentActivity
    //BackPressedHandler(onBackPressed = { sharedViewModel.onUIEvent(OnCloseClick(focusManager)) })

    BackHandler {
        sharedViewModel.onUIEvent(OnCloseClick(focusManager))
    }

    viewModel.onUIEvent(
        SignUpPasswordViewModel.UIEvent.OnInitializeDialogTexts(
            biometricPromptTitle = stringResource(id = R.string.biometric_dialog_title),
            biometricPromptDescription = stringResource(id = R.string.biometric_dialog_description),
            biometricPromptNegative = stringResource(id = R.string.cancel),
            biometricDialogDescription = stringResource(id = R.string.active_biometric_message),
            biometricDialogSuccessDescription = stringResource(id = R.string.dialog_success_biometric_description),
            biometricDialogFailureDescription = stringResource(id = R.string.dialog_failure_biometric_description),
        )
    )

    LaunchedEffect(context) {
        viewModel.apply {
            onUIEvent(
                SignUpPasswordViewModel.UIEvent.OnIsBiometricAvailable(
                    biometricHelper.isBiometricAvailable(
                        context
                    )
                )
            )
        }
    }

    LaunchedEffect(true) {
        viewModel.onUIEvent(SignUpPasswordViewModel.UIEvent.OnValidForm(
            onContinueEnable = { isEnabled ->
                sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnContinueEnable(isEnabled))
            }
        ))
        sharedViewModel.apply {
            onUIEvent(SignUpViewModel.UIEvent.OnSetNavigation(nextAction = {
                viewModel.onUIEvent(
                    SignUpPasswordViewModel.UIEvent.OnCallPasswordSave(
                        pkUser = userData?.pkUser ?: "0",
                        user = userData?.email ?: "",
                        Brand.Revamp.id
                    )
                )
            }, nextStep = SignUpStep.Seven.id, previousStep = SignUpStep.Three.id))
        }
    }

    LaunchedEffect(true) {
        viewModel.onPasswordSaveEvents.collect { event ->
            event.onSuccess {
                sharedViewModel.apply {
                    userData?.currentStep = SignUpStep.Five.name
                    viewModel.onUIEvent(SignUpPasswordViewModel.UIEvent.OnCallCognitoSignUp(
                        email = userData?.email ?: "",
                        firstName = userData?.firstName ?: "",
                        lastName = userData?.firstLastName ?: "",
                        phone = "${userData?.countryCode ?: ""}${userData?.phoneNumber ?: ""}",
                        identification = userData?.identification ?: "",
                        pkUser = userData?.pkUser ?: "",
                        status = userData?.userStatus ?: "",
                        onSuccess = {
                            onUIEvent(SignUpViewModel.UIEvent.OnLoadingValueChange(false))
                            viewModel.onUIEvent(
                                SignUpPasswordViewModel.UIEvent.OnShowBiometricPromptForEncryption(
                                    fragmentActivity = fragmentActivity,
                                    userEmail = userData?.email ?: "",
                                    userName = "${userData?.firstName ?: ""} ${userData?.firstLastName ?: ""}",
                                    onNextStep = { onUIEvent(SignUpViewModel.UIEvent.OnNextStep) }
                                )
                            )
                        },
                        onFailureWithDialog = { dialog ->
                            onUIEvent(
                                SignUpViewModel.UIEvent.OnFailureWithDialog(
                                    false,
                                    dialog
                                )
                            )
                        }
                    ))
                }
            }.onMessage {
                sharedViewModel.onUIEvent(
                    SignUpViewModel.UIEvent.OnFailureWithDialog(
                        false, DialogParameters(
                            description = it?.messageError?.message ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                )
            }.onFailure {
                sharedViewModel.onUIEvent(
                    SignUpViewModel.UIEvent.OnFailureWithDialog(
                        false, DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                )
            }.onLoading {
                sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnLoadingValueChange(true))
            }
        }
    }

    Column(Modifier.padding(16.dp)) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = Typography.h5.toSpanStyle()
                        .copy(
                            color = MultimoneyTheme.colors.labelText,
                            fontWeight = FontWeight.SemiBold
                        )
                ) {
                    append(stringResource(id = R.string.sign_up_password_title))
                }
            },
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
        CustomOutlinedTextField(
            value = viewModel.uiState.password,
            onValueChange = {
                viewModel.onUIEvent(
                    SignUpPasswordViewModel.UIEvent.OnPasswordValueChange(
                        it,
                        onContinueEnable = { isEnable ->
                            sharedViewModel.onUIEvent(
                                SignUpViewModel.UIEvent.OnContinueEnable(
                                    isEnable
                                )
                            )
                        })
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.clearFocus()
            }),
            labelText = stringResource(id = R.string.sign_up_label_password),
            isPassword = true,
            modifier = Modifier
                .padding(top = 24.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_password_required),
            isError = viewModel.uiState.passwordError.first,
            errorMessage = if (viewModel.uiState.passwordError.first) {
                stringResource(id = viewModel.uiState.passwordError.second)
            } else {
                null
            }
        )
        CustomOutlinedTextField(
            value = viewModel.uiState.confirmPassword,
            onValueChange = {
                viewModel.onUIEvent(
                    SignUpPasswordViewModel.UIEvent.OnConfirmPasswordValueChange(
                        it,
                        onContinueEnable = { isEnable ->
                            sharedViewModel.onUIEvent(
                                SignUpViewModel.UIEvent.OnContinueEnable(
                                    isEnable
                                )
                            )
                        })
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            labelText = stringResource(id = R.string.sign_up_label_confirm_password),
            isPassword = true,
            modifier = Modifier
                .padding(top = 16.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_password_required),
            isError = viewModel.uiState.confirmPasswordError.first,
            errorMessage = if (viewModel.uiState.confirmPasswordError.first) {
                stringResource(id = viewModel.uiState.confirmPasswordError.second)
            } else {
                null
            },
        )
        FlowRow(
            Modifier
                .padding(top = 8.dp)
        ) {
            PasswordRequirementLabels(
                text = stringResource(id = R.string.sign_up_password_requirement_eight_characters_minimum),
                state = viewModel.uiState.eightCharactersMinimumState
            )
            PasswordRequirementLabels(
                text = stringResource(id = R.string.sign_up_password_requirement_one_uppercase),
                state = viewModel.uiState.oneUppercaseState
            )
            PasswordRequirementLabels(
                text = stringResource(id = R.string.sign_up_password_requirement_one_lowercase),
                state = viewModel.uiState.oneLowercaseState
            )
            PasswordRequirementLabels(
                text = stringResource(id = R.string.sign_up_password_requirement_one_number),
                state = viewModel.uiState.oneNumberState
            )
            PasswordRequirementLabels(
                text = stringResource(id = R.string.sign_up_password_requirement_one_characer),
                state = viewModel.uiState.oneCharacterState
            )
        }
        CustomCheckBox(
            checked = viewModel.uiState.isFingerprintChecked,
            onCheckedChange = {
                viewModel.onUIEvent(
                    SignUpPasswordViewModel.UIEvent.OnFingerprintCheckedChanged(
                        it,
                        it
                    )
                )
            },
            text = stringResource(id = R.string.sign_in_activate_fingerprint),
            modifier = Modifier.padding(top = 24.dp)
        )
    }

    // Dialog
    if (viewModel.uiState.openDialogCustom.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialogCustom.title),
            message = viewModel.uiState.openDialogCustom.description,
            positiveButtonText = stringResource(id = viewModel.uiState.openDialogCustom.positiveText),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialogCustom.negativeText),
            onPositiveAction = viewModel.uiState.openDialogCustom.positiveAction,
            onNegativeAction = viewModel.uiState.openDialogCustom.negativeAction,
            onDismissAction = viewModel.uiState.openDialogCustom.dismissAction,
            openDialogCustom = viewModel.uiState.openDialogCustom.isActive
        )
    }
}

@Composable
fun PasswordRequirementLabels(modifier: Modifier = Modifier, text: String, state: Boolean?) {
    CustomPasswordRequirementLabel(
        modifier,
        text = text,
        successIcon = R.drawable.ic_check,
        errorIcon = R.drawable.ic_error_password,
        state = state
    )
}