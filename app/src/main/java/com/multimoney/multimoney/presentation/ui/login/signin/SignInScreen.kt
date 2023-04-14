package com.multimoney.multimoney.presentation.ui.login.signin

import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.extension.findActivity
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryTertiaryUnderLined
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.capitalized
import com.multimoney.multimoney.presentation.util.catalog.CognitoErrorCode
import com.multimoney.multimoney.presentation.util.getDeviceName
import com.multimoney.multimoney.presentation.util.getDeviceType
import com.multimoney.multimoney.presentation.util.getUserCountry
import com.multimoney.multimoney.presentation.util.splitByWhiteSpace
import com.multimoney.multimoney.util.firebase.FireBaseEvents

@Composable
@Preview
fun SignInScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SignInViewModel = hiltViewModel(),
) {
    // Properties
    val fragmentActivity = LocalContext.current as FragmentActivity
    val activity = LocalContext.current.findActivity()
    val signOutToastText = stringResource(id = R.string.automatic_logout_dialog_sign_in_toast)
    val context = LocalContext.current

    // Navigation
    LaunchedEffect(true) {
        viewModel.apply {
            onUIEvent(
                SignInViewModel.UIEvent.OnUpdateCountry(
                    context.getUserCountry()
                )
            )
            executeNavigation(onNavigate = onNavigate, onPopAndNavigate = onPopAndNavigate)
            onUIEvent(
                SignInViewModel.UIEvent.OnStart(
                    getDeviceName(fragmentActivity) ?: "",
                    getDeviceType(fragmentActivity).value,
                )
            )
            onUIEvent(SignInViewModel.UIEvent.OnSetCountryCode(context.getUserCountry()))
        }
    }

    viewModel.onUIEvent(
        SignInViewModel.UIEvent.OnInitializeBiometricPrompt(
            biometricPromptTitle = stringResource(id = R.string.biometric_dialog_title),
            biometricPromptDescription = stringResource(id = R.string.biometric_dialog_description),
            biometricPromptNegative = stringResource(id = R.string.cancel),
            fragmentActivity = fragmentActivity
        )
    )

    if (viewModel.uiState.toastIsVisible) {
        Toast.makeText(activity, signOutToastText, Toast.LENGTH_LONG).show()
        viewModel.onUIEvent(SignInViewModel.UIEvent.OnUpdateToastVisibility(false))
    }

    SignInContent(viewModel, fragmentActivity, context)
}

@Composable
fun SignInContent(
    viewModel: SignInViewModel,
    fragmentActivity: FragmentActivity,
    context: Context
) {
    val focusManager = LocalFocusManager.current

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
                .size(64.dp, 67.dp),
            alpha = 0.9f
        )

        Text(
            text = if (viewModel.isWelcomeWithName()) {
                buildAnnotatedString {
                    withStyle(
                        style = Typography.h5.toSpanStyle()
                            .copy(
                                color = MultimoneyTheme.colors.loginTitleText,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 28.sp
                            )
                    ) {
                        append(
                            stringResource(
                                id = R.string.sign_in_title_name,
                                viewModel.uiState.userName.splitByWhiteSpace().first().capitalized()
                            )
                        )
                    }
                }
            } else {
                buildAnnotatedString {
                    withStyle(
                        style = Typography.h5.toSpanStyle()
                            .copy(
                                color = MultimoneyTheme.colors.loginTitleText,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 28.sp
                            )
                    ) {
                        append(stringResource(id = R.string.sign_in_title))
                    }
                }
            },
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 24.dp, start = 24.dp, end = 24.dp)
        )

        // Fields
        CustomOutlinedTextField(
            value = viewModel.uiState.userEmail,
            onValueChange = {
                viewModel.onUIEvent(SignInViewModel.UIEvent.OnUserEmailValueChange(it))
            },
            onDebounceValidation = { viewModel.onUIEvent(SignInViewModel.UIEvent.OnValidateUserEmail) },
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
            modifier = Modifier.padding(top = 51.dp),
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
                        SignInViewModel.UIEvent.OnShowBiometricPromptForDecryption(
                            fragmentActivity
                        )
                    )
                },
                onLinkEnterWithPassword = {
                    viewModel.onUIEvent(
                        SignInViewModel.UIEvent.OnShowBiometricSignInChanged(
                            false
                        )
                    )
                }
            )
        } else {
            SignInPasswordScreen(
                viewModel = viewModel,
                focusManager = focusManager,
                onForgotPasswordClick = {
                    viewModel.onUIEvent(SignInViewModel.UIEvent.OnNavigateToForgotPassword)
                },
                onSignInWithBiometricLink = {
                    viewModel.onUIEvent(
                        SignInViewModel.UIEvent.OnShowBiometricSignInChanged(
                            true
                        )
                    )
                }
            )
        }
        CustomButton(
            text = stringResource(id = R.string.sign_in_create_account),
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .height(48.dp),
            onClick = {
                viewModel.onUIEvent(SignInViewModel.UIEvent.OnNavigateToSignUp)
            },
            buttonType = PrimaryTertiaryUnderLined
        )
    }
    LoadingIndicator(viewModel.uiState.isLoading)

    // Dialog
    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = viewModel.uiState.openDialog.title.ifEmpty { stringResource(id = viewModel.uiState.openDialog.titleResource) },
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            onPositiveAction = if (viewModel.uiState.errorCode == CognitoErrorCode.BlacklistedDevice) {
                { viewModel.onUIEvent(SignInViewModel.UIEvent.OnOpenWhatsappLink(context)) }
            } else {
                viewModel.uiState.openDialog.positiveAction
            },
            onNegativeAction = viewModel.uiState.openDialog.negativeAction,
            onDismissAction = viewModel.uiState.openDialog.dismissAction,
            openDialogCustom = viewModel.uiState.openDialog.isActive
        )
    }

    if (viewModel.uiState.configureBiometric) {
        viewModel.onUIEvent(
            SignInViewModel.UIEvent.OnShowBiometricPromptForEncryption(
                fragmentActivity
            )
        )
    }

    if (viewModel.uiState.biometricErrorDialog.first.value) {
        CustomDialog(
            title = stringResource(id = R.string.error),
            message = viewModel.uiState.biometricErrorDialog.second,
            onPositiveAction = {
                viewModel.onUIEvent(
                    SignInViewModel.UIEvent.OnShowBiometricSignInChanged(
                        false
                    )
                )
            },
            onDismissAction = {
                viewModel.onUIEvent(
                    SignInViewModel.UIEvent.OnShowBiometricSignInChanged(
                        false
                    )
                )
            },
            openDialogCustom = viewModel.uiState.biometricErrorDialog.first
        )
    }
}