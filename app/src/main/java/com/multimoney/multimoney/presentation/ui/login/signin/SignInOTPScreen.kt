package com.multimoney.multimoney.presentation.ui.login.signin

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.SemanticNegative500
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signin.SignInOTPViewModel.UIEvent.OnGetWhatsAppLink
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.OtpTextField
import com.multimoney.multimoney.presentation.uielement.SystemBroadcastReceiver
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SignInOTPScreen(
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SignInOTPViewModel = hiltViewModel(),
    signInViewModel: SignInViewModel
) {
    val context = LocalContext.current
    val launchSmsActivityResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    data?.apply {
                        getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)?.let {
                            viewModel.onUIEvent(SignInOTPViewModel.UIEvent.OnGetOtpFromMessage(it))
                        }
                    }
                }
            }
        }
    LaunchedEffect(true) {
        viewModel.onUIEvent(
            SignInOTPViewModel.UIEvent.OnSetupResources(context)
        )
        viewModel.onUIEvent(OnGetWhatsAppLink)
        viewModel.executeNavigation(
            onPopBackStack = onPopBackStack,
            onNavigate = onNavigate,
            onPopAndNavigate = onPopAndNavigate
        )
        viewModel.onUIEvent(
            SignInOTPViewModel.UIEvent.OnCallMutationRequestChangeDevice
        )
    }

    BackHandler {
        viewModel.onUIEvent(SignInOTPViewModel.UIEvent.OnNavigateBack)
    }

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = stringResource(viewModel.uiState.openDialog.descriptionResource),
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = {
                viewModel.onUIEvent(
                    SignInOTPViewModel.UIEvent.OnOpenWhatsappLink(
                        context
                    )
                )
            },
            onNegativeAction = {
                viewModel.onUIEvent(
                    SignInOTPViewModel.UIEvent.OnNavigateBack
                )
            }
        )
    }
    if (viewModel.uiState.isAlertResultVisible) {
        AlertResult(
            titleString = stringResource(id = R.string.sign_in_verify_otp_error_title),
            descriptionString = stringResource(id = R.string.sign_in_verify_otp_error_subtitle),
            buttonTextResource = R.string.payment_amount_error_button,
            isLeftButtonVisible = false,
            isRightButtonVisible = false,
            onButtonClick = {
                viewModel.onUIEvent(
                    SignInOTPViewModel.UIEvent.OnNavigateBack
                )
            }
        )
    }
    SignInOTPContent(viewModel, signInViewModel)
    LoadingIndicator(viewModel.uiState.isLoading)

    // Start SMS Retriever client
    SmsRetriever.getClient(context).startSmsUserConsent(null)

    SystemBroadcastReceiver(SmsRetriever.SMS_RETRIEVED_ACTION) { intent ->
        val extras = intent?.extras
        val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status
        when (status.statusCode) {
            CommonStatusCodes.SUCCESS -> {
                val messageIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                launchSmsActivityResult.launch(messageIntent)
            }
        }
    }
}

@Composable
fun SignInOTPContent(viewModel: SignInOTPViewModel, signInViewModel: SignInViewModel) {
    ConstraintLayout(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        val (topNavBar, otpField, titleText, headerText, timerText, continueButton) = createRefs()

        TopNavBar(
            modifier = Modifier.constrainAs(topNavBar) {
                top.linkTo(parent.top)
            },
            onLeftButtonClick = {
                viewModel.onUIEvent(SignInOTPViewModel.UIEvent.OnNavigateBack)
            },
            isRightButtonVisible = false
        )
        Text(
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                .constrainAs(titleText) {
                    top.linkTo(topNavBar.bottom)
                },
            text = stringResource(id = R.string.sign_in_device_verification),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText,
            textAlign = TextAlign.Left
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .constrainAs(headerText) {
                    top.linkTo(titleText.bottom)
                },
            text = stringResource(
                id = viewModel.uiState.weSentYouACodeTextResource,
                viewModel.uiState.phoneNumber
            ),
            style = Typography.body2.copy(color = MultimoneyTheme.colors.titleText)
        )

        OtpTextField(
            value = viewModel.uiState.otp,
            onValueChange = {
                viewModel.onUIEvent(SignInOTPViewModel.UIEvent.OnOTPValueChange(it))
            },
            isValueFromSms = viewModel.uiState.isOtpFromSms,
            digits = SignInOTPViewModel.TOTAL_DIGITS,
            placeHolder = stringResource(id = R.string.sign_up_otp_code_placeholder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .constrainAs(otpField) {
                    top.linkTo(headerText.bottom)
                },
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_otp_code_required),
            isError = viewModel.uiState.otpError.first,
            errorMessage = stringResource(id = viewModel.uiState.otpError.second)
        )

        when (viewModel.uiState.phaseCount) {
            SignUpOtpViewModel.PHASE_ONE, SignUpOtpViewModel.PHASE_THREE, SignUpOtpViewModel.PHASE_FIVE -> {
                Row(
                    modifier = Modifier
                        .constrainAs(timerText) {
                            top.linkTo(otpField.bottom, margin = 12.dp)
                        }
                ) {
                    Text(
                        text = stringResource(id = viewModel.getPhaseResourceString()),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(start = 16.dp, top = 32.dp),
                        style = Typography.body2.copy(color = MultimoneyTheme.colors.textSubhead)
                    )
                    Text(
                        text = viewModel.uiState.remainingTimeText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 32.dp)
                            .width(45.dp),
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.timerColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = stringResource(id = R.string.sign_in_otp_expiration_time_phase_seconds),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 32.dp),
                        style = Typography.body2.copy(color = MultimoneyTheme.colors.textSubhead)
                    )
                }
            }
            SignUpOtpViewModel.PHASE_TWO, SignUpOtpViewModel.PHASE_FOUR -> ClickableText(
                text = AnnotatedString(stringResource(id = viewModel.getPhaseResourceString())),
                modifier = Modifier
                    .padding(start = 16.dp, top = 32.dp)
                    .constrainAs(timerText) { top.linkTo(otpField.bottom, margin = 12.dp) },
                style = Typography.body2.copy(
                    textDecoration = TextDecoration.Underline,
                    color = MultimoneyTheme.colors.textLink
                ),
                onClick = {
                    viewModel.onUIEvent(
                        SignInOTPViewModel.UIEvent.OnResendOTP
                    )
                }
            )
            else -> {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = Typography.body2.toSpanStyle()
                                .copy(color = SemanticNegative500)
                        ) {
                            append(stringResource(id = viewModel.getPhaseResourceString()))
                        }
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 32.dp)
                        .constrainAs(timerText) { top.linkTo(otpField.bottom, margin = 12.dp) }
                )
            }
        }

        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp)
                .constrainAs(continueButton) {
                    bottom.linkTo(parent.bottom, margin = 40.dp)
                },
            buttonType = CustomButtonType.PrimaryPrimary,
            text = stringResource(id = R.string.sign_in_verify_otp_button),
            enable = viewModel.isFormValid() && viewModel.uiState.isButtonEnabled,
            onClick = {
                viewModel.onUIEvent(SignInOTPViewModel.UIEvent.OnValidateOTP {
                    signInViewModel.onUIEvent(SignInViewModel.UIEvent.OnCallCognitoSignIn(true))
                })
            }
        )
    }
}