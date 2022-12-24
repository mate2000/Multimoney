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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.amplifyframework.core.Amplify
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.Companion.TOTAL_DIGITS
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.OtpTextField
import com.multimoney.multimoney.presentation.uielement.SystemBroadcastReceiver
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.OTPMessageStatus

@Preview
@Composable
fun SignInOTPScreen(
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SignInOTPViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val launchSmsActivityResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    data?.apply {
                        getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)?.let {
                            //  viewModel.onUIEvent(SignInOTPViewModel.UIEvent.OnGetOtpFromMessage(it))
                        }
                    }
                }
            }
        }
    LaunchedEffect(true) {
        viewModel.executeNavigation(
            onPopBackStack = onPopBackStack,
            onNavigate = onNavigate,
            onPopAndNavigate = onPopAndNavigate
        )
        requestOTP(viewModel)
    }

    BackHandler {
        viewModel.onUIEvent(SignInOTPViewModel.UIEvent.OnNavigateBack)
    }
    if (viewModel.uiState.isAlertResultVisible) {
        AlertResult(
            titleString = "error title",
            descriptionString = "error description",
            buttonTextResource = R.string.payment_amount_error_button,
            isLeftButtonVisible = false,
            isRightButtonVisible = false,
            onButtonClick = { }
        )
    } else {
        SignInOTPContent(viewModel = viewModel)
        LoadingIndicator(viewModel.uiState.isLoading)
        if (viewModel.uiState.openDialog.isActive.value) {
            CustomDialog(
                title = stringResource(id = viewModel.uiState.openDialog.titleResource),
                message = viewModel.uiState.openDialog.description,
                positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
                negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
                openDialogCustom = viewModel.uiState.openDialog.isActive,
                onPositiveAction = viewModel.uiState.openDialog.positiveAction,
                onNegativeAction = viewModel.uiState.openDialog.negativeAction
            )
        }
    }

    LaunchedEffect(true) {
//        viewModel.onCallMutationSendPinProcessEvent.collect { event ->
//            event.onSuccess {
//                viewModel.apply {
//                    onUIEvent(ValidateOTPViewModel.UIEvent.OnLoadingValueChange(false))
//                    onUIEvent(ValidateOTPViewModel.UIEvent.OnCallMutationSendPinProcessSuccess(it))
//                }
//            }.onMessage {
//                viewModel.onUIEvent(
//                    ValidateOTPViewModel.UIEvent.OnFailureWithDialog(false,
//                        DialogParameters(
//                            titleResource = R.string.sign_up_email_blocked_dialog_title,
//                            description = viewModel.userBlockedForMaxAttend,
//                            isActive = mutableStateOf(true),
//                            positiveResource = R.string.contact,
//                            negativeResource = R.string.cancel,
//                            negativeAction = {
//                                viewModel.onUIEvent(ValidateOTPViewModel.UIEvent.OnNavigateTLogOut)
//                            },
//                            positiveAction = {
//                                viewModel.onUIEvent(ValidateOTPViewModel.UIEvent.OnOpenWhatsappLink(context,whatsAppLink))
//                            }
//                        )))
//            }.onFailure {
//                ValidateOTPViewModel.UIEvent.OnFailureWithDialog(false,
//                    DialogParameters(
//                        titleResource = R.string.something_went_wrong,
//                        description = it.getError() ?: "",
//                        isActive = mutableStateOf(true),
//                        positiveResource = R.string.button_continue,
//                    ))
//
//            }.onLoading {
//                viewModel.onUIEvent(ValidateOTPViewModel.UIEvent.OnLoadingValueChange(true))
//            }
//        }
    }


    //full screen dialog
    if (viewModel.uiState.isAlertResultVisible) {
        AlertResult(
            titleString = stringResource(id = R.string.sign_in_error_alert_dialog),
            descriptionString = stringResource(R.string.empty),
            buttonTextResource = R.string.sign_in_error_alert_dialog_button,
            isLeftButtonVisible = false,
            isRightButtonVisible = false,
            onButtonClick = {
                viewModel.onUIEvent(SignInOTPViewModel.UIEvent.OnNavigateBack)
            }
        )
    }

    // Start SMS Retriever client
    SmsRetriever.getClient(LocalContext.current).startSmsUserConsent(null)

    SystemBroadcastReceiver(SmsRetriever.SMS_RETRIEVED_ACTION) { intent ->
        val extras = intent?.extras
        val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status
        when (status.statusCode) {
            CommonStatusCodes.SUCCESS -> {
                val messageIntent =
                    extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                launchSmsActivityResult.launch(messageIntent)
            }
        }
    }
}

@Composable
fun SignInOTPContent(viewModel: SignInOTPViewModel) {
    ConstraintLayout(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        val (topNavBar, otpField, timerText, titleText, headerText, continueButton, statusText) = createRefs()

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
            text = stringResource(id = R.string.profile_identity_verification),
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
            text = stringResource(id = viewModel.uiState.enterTheCodeTextResource),
            style = Typography.body2
        )

        when (viewModel.uiState.messageStatus) {
            OTPMessageStatus.RESEND_OTP, OTPMessageStatus.RESEND_OTP_AGAIN -> {
                ClickableText(
                    text = AnnotatedString(stringResource(id = R.string.profile_otp_resend)),
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp)
                        .constrainAs(statusText) {
                            top.linkTo(headerText.bottom, margin = 12.dp)
                        }
                        .fillMaxWidth(),
                    style = Typography.body2.copy(
                        textDecoration = TextDecoration.Underline,
                        color = MultimoneyTheme.colors.textLink
                    ),
                    onClick = {
                        requestOTP(viewModel)
                    }
                )
            }
            OTPMessageStatus.COULD_NOT_VERIFY_ID -> {
                ClickableText(
                    text = AnnotatedString(stringResource(id = R.string.profile_couldnt_verify_identity)),
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp)
                        .constrainAs(statusText) {
                            top.linkTo(headerText.bottom, margin = 12.dp)
                        }
                        .fillMaxWidth(),
                    style = Typography.body2.copy(
                        color = MultimoneyTheme.colors.textAlertColor
                    ),
                    onClick = {
                        requestOTP(viewModel)
                    }
                )
            }
            else -> {
                Text(
                    text = stringResource(id = R.string.empty),
                    modifier = Modifier.constrainAs(statusText) {
                        top.linkTo(
                            headerText.bottom,
                            margin = 12.dp
                        )
                    }
                )
            }
        }

        OtpTextField(
            value = viewModel.uiState.otp,
            onValueChange = {
                viewModel.onUIEvent(SignInOTPViewModel.UIEvent.OnOTPValueChange(it))
            },
            isValueFromSms = viewModel.uiState.isOtpFromSms,
            digits = TOTAL_DIGITS,
            placeHolder = stringResource(id = R.string.sign_up_otp_code_placeholder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, start = 32.dp, end = 32.dp)
                .constrainAs(otpField) {
                    top.linkTo(statusText.bottom)
                },
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_otp_code_required),
            isError = viewModel.uiState.otpError.first,
            errorMessage = stringResource(id = viewModel.uiState.otpError.second)
        )

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .constrainAs(timerText) {
                top.linkTo(otpField.bottom)
            }) {
            Text(
                text = stringResource(
                    id = viewModel.uiState.statusTextResource,
                    viewModel.uiState.remainingTimeText
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth(),
                style = Typography.body2.copy(
                    color = MultimoneyTheme.colors.timerColor,
                    fontWeight = FontWeight.SemiBold
                )
            )
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
            text = stringResource(id = R.string.profile_send_code),
            enable = viewModel.isFormValid(),
            onClick = {
                viewModel.onUIEvent(SignInOTPViewModel.UIEvent.OnValidateOTP)
            }
        )
    }
}

fun requestOTP(viewModel: SignInOTPViewModel) {
    viewModel.onUIEvent(
        SignInOTPViewModel.UIEvent.OnCallMutationRequestChangeDevice
    )
//    viewModel.onUIEvent(
//        SignInOTPViewModel.UIEvent.OnCallMutationSendPinProcess(
//            viewModel.uiState.identification ?: "",
//            viewModel.uiState.firstName ?: "",
//            viewModel.uiState.email ?: "",
//            viewModel.uiState.phoneNumber ?: "",
//            viewModel.uiState.sendMethod ?: "",
//            viewModel.uiState.pkUser ?: "",
//            viewModel.uiState.idBrand ?: 0,
//            viewModel.uiState.email ?: ""
//        )
//    )
}