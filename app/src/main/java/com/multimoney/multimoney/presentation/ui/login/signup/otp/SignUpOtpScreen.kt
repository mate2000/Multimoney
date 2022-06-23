package com.multimoney.multimoney.presentation.ui.login.signup.otp

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.SemanticNegative500
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.Companion.PHONE_HARDCODED
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.Companion.PHASE_FIVE
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.Companion.PHASE_FOUR
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.Companion.PHASE_ONE
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.Companion.PHASE_THREE
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.Companion.PHASE_TWO
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.Companion.SEND_METHOD_PHONE
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.Companion.TIMER_DELAY
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.Companion.TOTAL_DIGITS
import com.multimoney.multimoney.presentation.uielement.OtpTextField
import com.multimoney.multimoney.presentation.uielement.SystemBroadcastReceiver
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.format
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink
import com.multimoney.multimoney.presentation.util.transformation.PhoneNumberTransformation
import kotlinx.coroutines.delay
import java.time.Duration

@Composable
@Preview
fun SignUpOtpScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SignUpOtpViewModel = hiltViewModel(),
    sharedViewModel: SignUpViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate)
        sharedViewModel.apply {
            sharedViewModel.onUIEvent(OnContinueEnable(viewModel.isFormValid()))
            nextAction = {
                userData?.currentStep = SignUpStep.Four.name
                callMutationUpdateUserRegisterUseCase()
            }
        }
    }

    // Create start activity result for SMS Retrieve
    val launchSmsActivityResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    data?.apply {
                        getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)?.let {
                            viewModel.getOtpFromMessage(it)
                        }
                    }
                }
            }
        }

    LaunchedEffect(true) {
        viewModel.isFirstLoad = true
        sharedViewModel.apply {
            viewModel.callMutationSendPinProcess(
                userData?.identification ?: "",
                userData?.firstName ?: "",
                userData?.email ?: "",
                userData?.phoneNumber ?: "",
                SEND_METHOD_PHONE,
                userData?.pkUser ?: "",
                Brand.Revamp.id,
                userData?.email ?: ""
            )
        }
    }

    LaunchedEffect(viewModel.isLoading) {
        if (viewModel.isFirstLoad.not()) {
            sharedViewModel.isLoading = viewModel.isLoading
        }
    }

    LaunchedEffect(viewModel.onSuccessOtp) {
        if (viewModel.isFirstLoad.not()) {
            viewModel.apply {
                phaseCount = PHASE_ONE
                isTimerRunning = true
                remainingTime = Duration.ofSeconds(SignUpOtpViewModel.TIMER_DURATION)
                remainingTimeText = remainingTime.format()
                otp = ""
            }
        }
    }

    val linkWhatsapp = stringResource(id = R.string.whatsapp_deep_link, PHONE_HARDCODED)

    LaunchedEffect(viewModel.onFailure) {
        if (viewModel.isFirstLoad.not()) {
            sharedViewModel.apply {
                openDialog = viewModel.onFailure.copy(positiveAction = {
                    context.openWhatsAppDeepLink(
                        linkWhatsapp
                    )
                    viewModel.navigateToSignIn()
                })
            }
        }
    }

    viewModel.isFirstLoad = false

    LaunchedEffect(key1 = viewModel.remainingTime, key2 = viewModel.isTimerRunning) {
        viewModel.apply {
            if (isTimerTick()) {
                delay(TIMER_DELAY)
                onTimerTick()
            } else if (viewModel.isTimerRunning) {
                onTimerFinish()
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

    Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
        Text(
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            text = stringResource(
                id = R.string.sign_up_otp_title,
                PhoneNumberTransformation(sharedViewModel.countryCode.uppercase()).filter(
                    AnnotatedString(
                        sharedViewModel.userData?.phoneNumber ?: ""
                    )
                ).text
            ),
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            style = Typography.body2.copy(color = MultimoneyTheme.colors.textSubhead),
            text = stringResource(id = R.string.sign_up_otp_subtitle),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        ClickableText(
            text = AnnotatedString(stringResource(id = R.string.sign_up_otp_change_phone)),
            modifier = Modifier.padding(top = 16.dp),
            style = Typography.body2.copy(
                textDecoration = TextDecoration.Underline,
                color = MultimoneyTheme.colors.textLink
            ),
            onClick = {
                focusManager.clearFocus()
                sharedViewModel.previousStep()
            }
        )

        // Fields
        OtpTextField(
            value = viewModel.otp,
            onValueChange = {
                viewModel.apply {
                    otp = it
                    isOtpFromSms = false
                    clearOtpError()
                    sharedViewModel.onUIEvent(OnContinueEnable(isFormValid()))
                }
            },
            isValueFromSms = viewModel.isOtpFromSms,
            digits = TOTAL_DIGITS,
            placeHolder = stringResource(id = R.string.sign_up_otp_code_placeholder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_otp_code_required),
            isError = viewModel.otpError.first,
            errorMessage = stringResource(id = viewModel.otpError.second)
        )

        when (viewModel.phaseCount) {
            PHASE_ONE, PHASE_THREE, PHASE_FIVE -> {
                Row {
                    Text(
                        text = stringResource(id = viewModel.getPhaseResourceString()),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 32.dp),
                        style = Typography.body2.copy(color = MultimoneyTheme.colors.textSubhead)
                    )
                    Text(
                        text = viewModel.remainingTimeText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 32.dp)
                            .width(45.dp),
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.textInformation,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = stringResource(id = R.string.sign_up_otp_expiration_time_phase_seconds),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 32.dp),
                        style = Typography.body2.copy(color = MultimoneyTheme.colors.textSubhead)
                    )
                }
            }
            PHASE_TWO, PHASE_FOUR -> ClickableText(
                text = AnnotatedString(stringResource(id = viewModel.getPhaseResourceString())),
                modifier = Modifier.padding(top = 32.dp),
                style = Typography.body2.copy(
                    textDecoration = TextDecoration.Underline,
                    color = MultimoneyTheme.colors.textLink
                ),
                onClick = {
                    sharedViewModel.apply {
                        viewModel.callMutationSendPinProcess(
                            userData?.identification ?: "",
                            userData?.firstName ?: "",
                            userData?.email ?: "",
                            userData?.phoneNumber ?: "",
                            SEND_METHOD_PHONE,
                            userData?.pkUser ?: "",
                            Brand.Revamp.id,
                            userData?.email ?: ""
                        )
                    }
                    viewModel.getPhaseAction()
                }
            )
            else -> Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = Typography.body2.toSpanStyle()
                            .copy(color = SemanticNegative500)
                    ) {
                        append(stringResource(id = viewModel.getPhaseResourceString()))
                    }
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 32.dp)
            )
        }
    }
}