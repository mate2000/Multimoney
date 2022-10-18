package com.multimoney.multimoney.presentation.ui.login.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
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
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnFingerprintCheckedChanged
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnInitializeBiometricPrompt
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnNavigateToForgotPassword
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnShowBiometricPromptForDecryption
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnShowBiometricPromptForEncryption
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnShowBiometricSignInChanged
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnUserEmailValueChange
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnValidateUserEmail
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryTertiaryUnderLined
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.util.firebase.FireBaseEvents

@Composable
@Preview
fun SignInScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SignInViewModel = hiltViewModel()
) {
    // Properties
    val focusManager = LocalFocusManager.current
    val fragmentActivity = LocalContext.current as FragmentActivity

    // Navigation
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onNavigate = onNavigate, onPopAndNavigate = onPopAndNavigate)
            onUIEvent(OnStart)
        }
    }

    viewModel.onUIEvent(
        OnInitializeBiometricPrompt(
            biometricPromptTitle = stringResource(id = R.string.biometric_dialog_title),
            biometricPromptDescription = stringResource(id = R.string.biometric_dialog_description),
            biometricPromptNegative = stringResource(id = R.string.cancel)
        )
    )

    // View
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Welcome section
        CustomImage(
            drawableResource = R.drawable.ic_logo_multimoney,
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.CenterHorizontally)
                .padding(top = 40.dp)
                .size(64.dp, 67.dp)
        )

        Text(
            text = if (viewModel.isWelcomeWithName()) {
                buildAnnotatedString {
                    withStyle(
                        style = Typography.h5.toSpanStyle()
                            .copy(
                                color = MultimoneyTheme.colors.text,
                                fontWeight = FontWeight.SemiBold
                            )
                    ) {
                        append(
                            stringResource(
                                id = R.string.sign_in_title_name,
                                viewModel.uiState.userName
                            )
                        )
                    }
                    withStyle(
                        style = Typography.subtitle1.toSpanStyle()
                            .copy(color = MultimoneyTheme.colors.text)
                    ) {
                        append(stringResource(id = R.string.sign_in_title_no_name))
                    }
                }
            } else {
                buildAnnotatedString {
                    withStyle(
                        style = Typography.h5.toSpanStyle()
                            .copy(
                                color = MultimoneyTheme.colors.text,
                                fontWeight = FontWeight.SemiBold
                            )
                    ) {
                        append(stringResource(id = R.string.sign_in_title))
                    }
                }
            },
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 24.dp)
        )

        // Fields
        CustomOutlinedTextField(
            value = viewModel.uiState.userEmail,
            onValueChange = {
                viewModel.onUIEvent(OnUserEmailValueChange(it))
            },
            onDebounceValidation = { viewModel.onUIEvent(OnValidateUserEmail) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            labelText = if (viewModel.uiState.isBiometricActive) {
                stringResource(id = R.string.sign_in_biometric_email_hint)
            } else {
                stringResource(
                    id = R.string.label_email
                )
            },
            modifier = Modifier
                .padding(top = 44.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_in_email_required),
            isError = viewModel.uiState.userEmailError.first,
            errorMessage = stringResource(id = viewModel.uiState.userEmailError.second)
        )
        if (viewModel.uiState.isBiometricActive && viewModel.uiState.showBiometricSignIn) {
            SignInWithBiometric(
                Modifier.padding(top = 32.dp),
                onSignInWithBiometricAction = {
                    viewModel.provideFireBaseEventHelper.logEvent(FireBaseEvents.LoginBiometrics)
                    viewModel.onUIEvent(
                        OnShowBiometricPromptForDecryption(
                            fragmentActivity
                        )
                    )
                },
                onLinkEnterWithPassword = { viewModel.onUIEvent(OnShowBiometricSignInChanged(false)) }
            )
        } else {
            SignInWithPassword(
                viewModel = viewModel,
                focusManager = focusManager,
                onForgotPasswordClick = {
                    viewModel.onUIEvent(OnNavigateToForgotPassword)
                },
                onSignInWithBiometricLink = { viewModel.onUIEvent(OnShowBiometricSignInChanged(true)) }
            )
        }
        CustomButton(
            text = stringResource(id = R.string.sign_in_create_account),
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .height(48.dp),
            onClick = {
                viewModel.navigateTo(route = "${Screen.SignUpScreen.baseRoute}/".plus(0))
            },
            buttonType = PrimaryTertiaryUnderLined
        )
    }
    LoadingIndicator(viewModel.uiState.isLoading)

    // Dialog
    if (viewModel.uiState.openDialogCustom.value) {
        CustomDialog(
            title = stringResource(id = R.string.active_biometric_title),
            message = stringResource(id = R.string.active_biometric_message),
            positiveButtonText = stringResource(id = R.string.active_biometric_positive_button_label),
            negativeButtonText = stringResource(id = R.string.active_biometric_negative_button_label),
            onPositiveAction = {
                viewModel.onUIEvent(
                    OnFingerprintCheckedChanged(
                        value = true,
                        showDialog = false
                    )
                )
            },
            onNegativeAction = {
                viewModel.onUIEvent(
                    OnFingerprintCheckedChanged(
                        value = false,
                        showDialog = false
                    )
                )
            },
            onDismissAction = {
                viewModel.onUIEvent(
                    OnFingerprintCheckedChanged(
                        value = false,
                        showDialog = false
                    )
                )
            },
            openDialogCustom = viewModel.uiState.openDialogCustom
        )
    }

    if (viewModel.uiState.configureBiometric) {
        viewModel.onUIEvent(OnShowBiometricPromptForEncryption(fragmentActivity))
    }

    if (viewModel.uiState.biometricErrorDialog.first.value) {
        CustomDialog(
            title = stringResource(id = R.string.error),
            message = viewModel.uiState.biometricErrorDialog.second,
            onPositiveAction = { viewModel.onUIEvent(OnShowBiometricSignInChanged(false)) },
            onDismissAction = { viewModel.onUIEvent(OnShowBiometricSignInChanged(false)) },
            openDialogCustom = viewModel.uiState.biometricErrorDialog.first
        )
    }
}
