package com.multimoney.multimoney.presentation.ui.login.registereduser.otp

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.SemanticNegative500
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.BaseEvent.OnOpenWhatsApp
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.Companion.PHASE_FIVE
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.Companion.PHASE_FOUR
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.Companion.PHASE_ONE
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.Companion.PHASE_THREE
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.Companion.PHASE_TWO
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.Companion.TIMER_DURATION
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.Companion.TOTAL_DIGITS
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.UIEvent.OnCallMutationSendPinProcess
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.UIEvent.OnGetOtpFromMessage
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.UIEvent.OnInitializeTimer
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.UIEvent.OnOtherPhoneNumberClick
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.UIEvent.OnOtpValueChange
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.UIState
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.Companion.PHONE_HARDCODED
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.OtpTextField
import com.multimoney.multimoney.presentation.uielement.SystemBroadcastReceiver
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink

@Composable
@Preview
fun RegisteredUserOtpScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: RegisteredUserOtpViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Create start activity result for SMS Retrieve
    val launchSmsActivityResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    data?.apply {
                        getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)?.let {
                            viewModel.onUIEvent(OnGetOtpFromMessage(it))
                        }
                    }
                }
            }
        }

    viewModel.onUIEvent(
        OnStart(
            stringResource(
                id = string.whatsapp_deep_link,
                PHONE_HARDCODED
            )
        )
    )

    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopAndNavigate = onPopAndNavigate, onPopBackStack = onPopBackStack)
            onUIEvent(OnInitializeTimer(PHASE_ONE, TIMER_DURATION))
            onUIEvent(OnCallMutationSendPinProcess)
            baseEvent.collect { event ->
                when (event) {
                    is OnOpenWhatsApp -> {
                        context.openWhatsAppDeepLink(viewModel.linkWhatsapp)
                    }
                }
            }
        }
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

    viewModel.apply {
        RegisteredUserOtpContent(
            uiState = uiState,
            onOtpValueChange = { value -> onUIEvent(OnOtpValueChange(value)) },
            onOtherPhoneNumberClick = { onUIEvent(OnOtherPhoneNumberClick) },
            getPhaseResourceString = { getPhaseResourceString() },
            onCallMutationSendPinProcess = { onUIEvent(OnCallMutationSendPinProcess) },
            onBackClick = { onUIEvent(OnBackClick) },
            onContinueClick = { onUIEvent(OnContinueClick) }
        )
    }
}

@Composable
@Preview
fun RegisteredUserOtpContent(
    uiState: UIState = UIState(),
    onOtpValueChange: (String) -> Unit = {},
    onOtherPhoneNumberClick: () -> Unit = {},
    getPhaseResourceString: () -> Int = { R.string.empty },
    onCallMutationSendPinProcess: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            isRightButtonVisible = false,
            onLeftButtonClick = { onBackClick() }
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    style = Typography.h6.copy(
                        color = MultimoneyTheme.colors.text,
                        fontWeight = FontWeight.SemiBold
                    ),
                    text = stringResource(
                        id = uiState.titleResource,
                        uiState.titleOtpMethod
                    ),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(top = 24.dp).fillMaxWidth()
                )

                Text(
                    style = Typography.body2.copy(color = MultimoneyTheme.colors.labelText),
                    text = stringResource(id = R.string.registered_user_otp_subtitle),
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )

                if (uiState.isOtherPhoneNumberVisible) {
                    ClickableText(
                        text = AnnotatedString(stringResource(id = R.string.registered_user_otp_other_phone_number)),
                        modifier = Modifier.padding(top = 24.dp),
                        style = Typography.body2.copy(
                            textDecoration = TextDecoration.Underline,
                            color = MultimoneyTheme.colors.textLink
                        ),
                        onClick = { onOtherPhoneNumberClick() }
                    )
                }

                // Fields
                OtpTextField(
                    value = uiState.otp,
                    onValueChange = { onOtpValueChange(it) },
                    isValueFromSms = uiState.isOtpFromSms,
                    digits = TOTAL_DIGITS,
                    placeHolder = stringResource(id = R.string.registered_user_otp_code_placeholder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    isRequired = true,
                    isRequiredMessage = stringResource(id = R.string.registered_user_otp_code_required),
                    isError = uiState.otpError.first,
                    errorMessage = stringResource(id = uiState.otpError.second)
                )

                when (uiState.phaseCount) {
                    PHASE_ONE, PHASE_THREE, PHASE_FIVE -> {
                        Row {
                            Text(
                                text = stringResource(id = getPhaseResourceString()),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 32.dp),
                                style = Typography.body2.copy(color = MultimoneyTheme.colors.textSubhead)
                            )
                            Text(
                                text = uiState.remainingTimeText,
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
                    PHASE_TWO, PHASE_FOUR -> ClickableText(
                        text = AnnotatedString(stringResource(id = getPhaseResourceString())),
                        modifier = Modifier.padding(top = 32.dp),
                        style = Typography.body2.copy(
                            textDecoration = TextDecoration.Underline,
                            color = MultimoneyTheme.colors.textLink
                        ),
                        onClick = { onCallMutationSendPinProcess() }
                    )
                    else -> Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = Typography.body2.toSpanStyle()
                                    .copy(color = SemanticNegative500)
                            ) {
                                append(stringResource(id = getPhaseResourceString()))
                            }
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 32.dp)
                    )
                }
            }
            CustomButton(
                onClick = { onContinueClick() },
                enable = uiState.isFormValid,
                text = stringResource(id = string.button_continue),
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
                    .height(48.dp)
            )
        }
    }
    LoadingIndicator(uiState.isLoading)
    if (uiState.dialogParameters.isActive.value) {
        CustomDialog(
            title = stringResource(id = uiState.dialogParameters.titleResource),
            message = stringResource(id = uiState.dialogParameters.descriptionResource).ifEmpty { uiState.dialogParameters.description },
            positiveButtonText = stringResource(id = uiState.dialogParameters.positiveResource),
            negativeButtonText = stringResource(id = uiState.dialogParameters.negativeResource),
            openDialogCustom = uiState.dialogParameters.isActive,
            onPositiveAction = uiState.dialogParameters.positiveAction,
            onNegativeAction = uiState.dialogParameters.negativeAction
        )
    }
    BackHandler {
        onBackClick()
    }
}
