package com.multimoney.multimoney.presentation.ui.login.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.util.NavEvent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
@Preview
fun SignInScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SignInViewModel = hiltViewModel()
) {
    // Properties
    val focusManager = LocalFocusManager.current
    val openDialogCustom = remember { mutableStateOf(false) }
    var isBiometricActive by remember { mutableStateOf(false) }
    var showBiometricSignIn by remember { mutableStateOf(false) }

    // Navigation
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onNavigate = onNavigate, onPopAndNavigate = onPopAndNavigate)
            userEmail = dataStorePreferences.getUserEmail().first()
            isBiometricActive = dataStorePreferences.isBiometricsEnabled().first()
            showBiometricSignIn = isBiometricActive
        }
    }

    val fragmentActivity = LocalContext.current as FragmentActivity

    viewModel.apply {
        biometricPromptTitle = stringResource(id = R.string.biometric_dialog_title)
        biometricPromptDescription = stringResource(id = R.string.biometric_dialog_description)
        biometricPromptNegative = stringResource(id = R.string.cancel)
    }

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
        )

        Text(
            text = viewModel.userName?.let {
                buildAnnotatedString {
                    withStyle(
                        style = Typography.h5.toSpanStyle()
                            .copy(fontWeight = FontWeight.SemiBold)
                    ) {
                        append(stringResource(id = R.string.sign_in_title_name, it))
                    }
                    withStyle(style = Typography.subtitle1.toSpanStyle()) {
                        append(stringResource(id = R.string.sign_in_title_no_name))
                    }
                }
            } ?: run {
                buildAnnotatedString {
                    withStyle(
                        style = Typography.h5.toSpanStyle()
                            .copy(fontWeight = FontWeight.SemiBold)
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
            value = viewModel.userEmail,
            onValueChange = {
                viewModel.apply {
                    userEmail = it
                    clearUserEmailError()
                    isFormValid()
                }
            },
            onDebounceValidation = { viewModel.isUserEmailValid() },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            labelText = stringResource(id = R.string.label_email),
            leadingIcon = R.drawable.ic_envelope,
            modifier = Modifier
                .padding(top = 44.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_in_email_required),
            isError = viewModel.userEmailError.first,
            errorMessage = stringResource(id = viewModel.userEmailError.second)
        )
        if (isBiometricActive && showBiometricSignIn) {
            SignInWithBiometric(
                Modifier.padding(top = 32.dp),
                onSignInWithBiometricAction = {
                    viewModel.apply {
                        viewModelScope.launch {
                            biometricHelper.showBiometricPrompt(
                                title = biometricPromptTitle,
                                description = biometricPromptDescription,
                                negative = biometricPromptNegative,
                                activity = fragmentActivity,
                                processSuccess = ::biometricPromptForDecryptionSuccess,
                                processError = ::biometricPromptError,
                                initializationVector = dataStorePreferences.getUserPasswordVector()
                                    .first()
                            )
                        }
                    }
                },
                onLinkEnterWithPassword = {
                    showBiometricSignIn = false
                }
            )
        } else {
            SignInWithPassword(
                signInViewModel = viewModel,
                focusManager = focusManager,
                openDialogCustom = openDialogCustom,
                isBiometricActive = isBiometricActive,
                isBiometricError = viewModel.biometricError,
                onSignInWithBiometricLink = { showBiometricSignIn = true }
            )
        }
        ClickableText(
            text = AnnotatedString(stringResource(id = R.string.sign_in_create_account)),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 24.dp),
            style = Typography.body1.copy(
                textDecoration = TextDecoration.Underline,
                color = MultimoneyTheme.colors.textLink
            ),
            onClick = {
                viewModel.navigateTo(route = Screen.SignUpScreen.route)
            }
        )
    }
    LoadingIndicator(viewModel.isLoading)

    // Dialog
    if (openDialogCustom.value) {
        CustomDialog(
            title = stringResource(id = R.string.active_biometric_title),
            message = stringResource(id = R.string.active_biometric_message),
            positiveButtonText = stringResource(id = R.string.active_biometric_positive_button_label),
            negativeButtonText = stringResource(id = R.string.active_biometric_negative_button_label),
            onPositiveAction = { viewModel.isFingerprintChecked = true },
            onNegativeAction = { viewModel.isFingerprintChecked = false },
            onDismissAction = { viewModel.isFingerprintChecked = false },
            openDialogCustom = openDialogCustom
        )
    }

    if (viewModel.configureBiometric) {
        viewModel.apply {
            biometricHelper.showBiometricPrompt(
                title = biometricPromptTitle,
                description = biometricPromptDescription,
                negative = biometricPromptNegative,
                activity = fragmentActivity,
                processSuccess = ::biometricPromptForEncryptionSuccess,
                processError = ::biometricPromptConfigurationError
            )
        }
    }

    if (viewModel.biometricErrorDialog.first.value) {
        CustomDialog(
            title = stringResource(id = R.string.error),
            message = viewModel.biometricErrorDialog.second,
            onPositiveAction = { showBiometricSignIn = false },
            onDismissAction = { showBiometricSignIn = false },
            openDialogCustom = viewModel.biometricErrorDialog.first
        )
    }
}
