package com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.validateotp

import android.app.Activity
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.runtime.mutableStateOf
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
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.multimoney.data.util.catalog.FieldToChange
import com.multimoney.data.util.catalog.FlowOriginChangeProfileInfo
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel
import com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.validateotp.ValidateOTPViewModel.UIEvent.OnGetWhatsAppLink
import com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.verifyidentity.VerifyIdentityViewModel.Companion.SEND_EMAIL_METHOD
import com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.verifyidentity.VerifyIdentityViewModel.Companion.SEND_PHONE_METHOD
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
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.OTPMessageStatus

@Preview
@Composable
fun VerifyNewValueOTPScreen(
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: ValidateOTPViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val phoneNumberChangedToastText =
        stringResource(id = R.string.profile_phone_number_changed_toast)
    val emailChangedToastText =
        stringResource(id = R.string.profile_email_changed_toast)

    val launchSmsActivityResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    data?.apply {
                        getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)?.let {
                            viewModel.onUIEvent(ValidateOTPViewModel.UIEvent.OnGetOtpFromMessage(it))
                        }
                    }
                }
            }
        }

    LaunchedEffect(true) {
        viewModel.apply {
            onUIEvent(ValidateOTPViewModel.UIEvent.OnInit)
            executeNavigation(
                onPopBackStack = onPopBackStack,
                onNavigate = onNavigate,
                onPopAndNavigate = onPopAndNavigate
            )
            onUIEvent(OnGetWhatsAppLink)
            requestSecondOTP(this)
        }

    }

    BackHandler {
        viewModel.onUIEvent(ValidateOTPViewModel.UIEvent.OnNavigateBack)
    }

    VerifyNewValueOTPContent(viewModel = viewModel)
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

    if (viewModel.uiState.openmaxAttemptsReachedDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openmaxAttemptsReachedDialog.titleResource),
            message = viewModel.uiState.openmaxAttemptsReachedDialog.description,
            positiveButtonText = stringResource(id = viewModel.uiState.openmaxAttemptsReachedDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openmaxAttemptsReachedDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openmaxAttemptsReachedDialog.isActive,
            onPositiveAction = {
                viewModel.onUIEvent(
                    ValidateOTPViewModel.UIEvent.OnOpenWhatsappLink(
                        context
                    )
                )
            },
            onNegativeAction = viewModel.uiState.openmaxAttemptsReachedDialog.negativeAction,
            isCancelable = false
        )
    }

    viewModel.onUIEvent(
        ValidateOTPViewModel.UIEvent.OnStart(
            stringResource(viewModel.uiState.dialogTextResource)
        )
    )

    LaunchedEffect(true) {
        viewModel.onCallMutationSendPinProcessEvent.collect { event ->
            event.onSuccess {
                viewModel.apply {
                    onUIEvent(ValidateOTPViewModel.UIEvent.OnLoadingValueChange(false))
                    onUIEvent(ValidateOTPViewModel.UIEvent.OnCallMutationSendPinProcessSuccess(it))
                }
            }.onMessage {
                viewModel.onUIEvent(
                    ValidateOTPViewModel.UIEvent.OnFailureWithDialog(
                        false,
                        DialogParameters(
                            titleResource = R.string.sign_up_email_blocked_dialog_title,
                            description = viewModel.userBlockedForMaxAttend,
                            isActive = mutableStateOf(true),
                            positiveResource = R.string.contact,
                            negativeResource = R.string.cancel,
                            negativeAction = {
                                viewModel.onUIEvent(ValidateOTPViewModel.UIEvent.OnNavigateTLogOut)
                            },
                            positiveAction = {
                                viewModel.onUIEvent(
                                    ValidateOTPViewModel.UIEvent.OnOpenWhatsappLink(context)
                                )
                            }
                        )
                    )
                )
            }.onFailure {
                viewModel.onUIEvent(ValidateOTPViewModel.UIEvent.OnError)
            }.onLoading {
                viewModel.onUIEvent(ValidateOTPViewModel.UIEvent.OnLoadingValueChange(true))
            }
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.baseEvent.collect { event ->
            when (event) {
                is HomeViewModel.BaseEvent.OnPhoneNumberChangedToastEvent -> {
                    Toast.makeText(context, phoneNumberChangedToastText, Toast.LENGTH_LONG).show()
                }
                is HomeViewModel.BaseEvent.OnEmailChangedToastEvent -> {
                    Toast.makeText(context, emailChangedToastText, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // full screen dialog
    if (viewModel.uiState.isAlertResultVisible) {
        AlertResult(
            titleString = stringResource(id = if (viewModel.uiState.changingField == FieldToChange.PHONE.value) R.string.profile_error_changing_phone_title else R.string.profile_error_changing_email_title),
            descriptionString = stringResource(viewModel.uiState.alertTextResource),
            buttonTextResource = R.string.profile_error_changing_phone_button,
            isLeftButtonVisible = false,
            isRightButtonVisible = false,
            onButtonClick = {
                viewModel.onUIEvent(ValidateOTPViewModel.UIEvent.OnNavigateBack)
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
fun VerifyNewValueOTPContent(viewModel: ValidateOTPViewModel) {
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
                viewModel.onUIEvent(ValidateOTPViewModel.UIEvent.OnNavigateBack)
            },
            isRightButtonVisible = false
        )
        Text(
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                .constrainAs(titleText) {
                    top.linkTo(topNavBar.bottom)
                },
            text = stringResource(
                id = if (viewModel.uiState.changingField == FieldToChange.PHONE.value) R.string.profile_identity_verification_verify_your_new_phone else R.string.profile_identity_verification_verify_your_new_email
            ),
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
                id = viewModel.uiState.enterTheCodeTextResource,
                if (viewModel.uiState.changingField == FieldToChange.PHONE.value) viewModel.uiState.newPhoneNumberCode.plus(
                    viewModel.uiState.newValue
                ) else viewModel.uiState.newValue ?: ""
            ),
            style = Typography.body2,
            color = MultimoneyTheme.colors.labelText
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
                        requestSecondOTP(viewModel)
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
                        requestSecondOTP(viewModel)
                    }
                )
            }
            else -> {
                Text(
                    text = stringResource(id = R.string.empty),
                    modifier = Modifier.constrainAs(statusText) {
                        top.linkTo(headerText.bottom, margin = 12.dp)
                    }
                )
            }
        }

        OtpTextField(
            value = viewModel.uiState.otp,
            onValueChange = {
                viewModel.onUIEvent(ValidateOTPViewModel.UIEvent.OnOtpValueChange(it))
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .constrainAs(timerText) {
                    top.linkTo(otpField.bottom)
                }
        ) {
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
            text = stringResource(id = R.string.profile_verify_code),
            enable = viewModel.isFormValid(),
            onClick = {
                viewModel.onUIEvent(ValidateOTPViewModel.UIEvent.OnValidateSecondOtpClicked)
            }
        )
    }
}

fun requestSecondOTP(viewModel: ValidateOTPViewModel) {
    viewModel.onUIEvent(
        ValidateOTPViewModel.UIEvent.OnCallMutationSendPinProcess(
            viewModel.uiState.identification ?: "",
            viewModel.uiState.firstName ?: "",
            (if (viewModel.uiState.changingField == FieldToChange.EMAIL.value) viewModel.uiState.newValue else viewModel.uiState.email)
                ?: "",
            (if (viewModel.uiState.changingField == FieldToChange.PHONE.value) (viewModel.uiState.newPhoneNumberCode.plus(
                viewModel.uiState.newValue
            ).replace(" ", "")
                    ) else viewModel.uiState.phoneNumber) ?: "",
            if (viewModel.uiState.changingField == FieldToChange.PHONE.value) SEND_PHONE_METHOD else SEND_EMAIL_METHOD,
            viewModel.uiState.pkUser ?: "",
            viewModel.uiState.idBrand ?: 0,
            viewModel.uiState.userName ?: "",
            FlowOriginChangeProfileInfo.CHANGE.value
        )
    )
}
