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
import androidx.compose.runtime.mutableStateOf
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
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.SemanticNegative500
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.Companion.PHONE_HARDCODED
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnShowCloseIcon
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.Companion.PHASE_FIVE
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.Companion.PHASE_FOUR
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.Companion.PHASE_ONE
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.Companion.PHASE_THREE
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.Companion.PHASE_TWO
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.Companion.SEND_METHOD_PHONE
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.Companion.TIMER_DURATION
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.Companion.TOTAL_DIGITS
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.UIEvent.OnNavigateToSignIn
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.OtpTextField
import com.multimoney.multimoney.presentation.uielement.SystemBroadcastReceiver
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink
import com.multimoney.multimoney.presentation.util.transformation.PhoneNumberTransformation
import com.multimoney.multimoney.util.firebase.FireBaseEvents

@Composable
@Preview
fun SignUpOtpScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SignUpOtpViewModel = hiltViewModel(),
    sharedViewModel: SignUpViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    // Create start activity result for SMS Retrieve
    val launchSmsActivityResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    data?.apply {
                        getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)?.let {
                            viewModel.onUIEvent(SignUpOtpViewModel.UIEvent.OnGetOtpFromMessage(it))
                        }
                    }
                }
            }
        }

    viewModel.onUIEvent(
        SignUpOtpViewModel.UIEvent.OnStart(
            stringResource(
                id = R.string.whatsapp_deep_link,
                PHONE_HARDCODED
            ),
            stringResource(id = R.string.sign_up_otp_code_user_blocked_for_exceed_the_max_of_attempts),
            sharedViewModel.idBrand
        )
    )

    LaunchedEffect(true) {
        sharedViewModel.onUIEvent(OnShowCloseIcon(true))
        viewModel.apply {
            executeNavigation(onPopAndNavigate = onPopAndNavigate)
            onUIEvent(SignUpOtpViewModel.UIEvent.OnInitializeTimer(PHASE_ONE, TIMER_DURATION))
            baseEvent.collect { event ->
                when (event) {
                    is OnFormValidateCompleted -> sharedViewModel.onUIEvent(
                        SignUpViewModel.UIEvent.OnContinueEnable(
                            event.isFormValid
                        )
                    )
                }
            }
        }
    }
    LaunchedEffect(true) {
        viewModel.onUIEvent(SignUpOtpViewModel.UIEvent.OnValidateForm)
        sharedViewModel.apply {
            viewModel.onUIEvent(
                SignUpOtpViewModel.UIEvent.OnCallMutationSendPinProcess(
                    userData?.identification ?: "",
                    userData?.firstName ?: "",
                    userData?.email ?: "",
                    userData?.phoneNumber ?: "",
                    SEND_METHOD_PHONE,
                    userData?.pkUser ?: "",
                    idBrand ?: 0,
                    userData?.email ?: ""
                )
            )
            onUIEvent(
                SignUpViewModel.UIEvent.OnSetNavigation(
                    nextAction = {
                        viewModel.onUIEvent(
                            SignUpOtpViewModel.UIEvent.OnNextActionClick(
                                pkUser = userData?.pkUser,
                                idBrand = idBrand,
                                phone = userData?.phoneNumber,
                                name = userData?.firstName,
                                onUseDataValueChange = {
                                    onUIEvent(
                                        SignUpViewModel.UIEvent.OnUseDataValueChange(
                                            userData = userData?.copy(
                                                currentStep = viewModel.getNextStep(
                                                    isOnFidoVerified
                                                ).name
                                            )
                                        )
                                    )
                                },
                                onCallMutationUpdateUserRegisterUseCase = {
                                    onUIEvent(SignUpViewModel.UIEvent.OnCallMutationUpdateUserRegisterUseCase)
                                },
                                onPhoneVerifiedChanged = {
                                    onUIEvent(
                                        SignUpViewModel.UIEvent.OnPhoneVerifiedChanged(
                                            true
                                        )
                                    )
                                },
                                onLoadingValueChange = { isLoading ->
                                    onUIEvent(SignUpViewModel.UIEvent.OnLoadingValueChange(isLoading))
                                },
                                onFailureWithDialog = { isLoading, dialogParameter ->
                                    onUIEvent(
                                        SignUpViewModel.UIEvent.OnFailureWithDialog(
                                            isLoading,
                                            dialogParameter
                                        )
                                    )
                                }
                            )
                        )
                        viewModel.provideFireBaseEventHelper.logEvent(FireBaseEvents.SignUpFour)
                    },
                    nextStep = viewModel.getNextStep(isOnFidoVerified).id,
                    previousStep = SignUpStep.Three.id
                )
            )
        }
    }

    LaunchedEffect(true) {
        viewModel.onCallMutationSendPinProcessEvent.collect { event ->
            event.onSuccess {
                viewModel.onUIEvent(
                    SignUpOtpViewModel.UIEvent.OnCallMutationSendPinProcessSuccess({
                        sharedViewModel.onUIEvent(
                            SignUpViewModel.UIEvent.OnLoadingValueChange(false)
                        )
                    }, it)
                )
            }.onMessage {
                sharedViewModel.onUIEvent(
                    SignUpViewModel.UIEvent.OnFailureWithDialog(
                        isLoading = false,
                        openDialog = DialogParameters(
                            titleResource = string.sign_up_email_blocked_dialog_title,
                            description = viewModel.userBlockedForMaxAttend,
                            isActive = mutableStateOf(true),
                            positiveResource = string.contact,
                            positiveAction = {
                                context.openWhatsAppDeepLink(viewModel.linkWhatsapp)
                                viewModel.onUIEvent(OnNavigateToSignIn)
                            },
                            negativeResource = string.cancel,
                            negativeAction = {
                                viewModel.onUIEvent(OnNavigateToSignIn)
                            },
                            isCancelable = false
                        )
                    )
                )
            }.onFailure {
                sharedViewModel.onUIEvent(
                    SignUpViewModel.UIEvent.OnFailureWithDialog(
                        isLoading = false,
                        openDialog = DialogParameters(
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
            style = Typography.h6.copy(
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.SemiBold
            ),
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
            style = Typography.body2.copy(color = MultimoneyTheme.colors.subTitleText),
            text = stringResource(id = viewModel.uiState.subtitleResource),
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
            onClick = { sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnBackClick(focusManager)) }
        )

        // Fields
        OtpTextField(
            value = viewModel.uiState.otp,
            onValueChange = { viewModel.onUIEvent(SignUpOtpViewModel.UIEvent.OnOtpValueChange(it)) },
            isValueFromSms = viewModel.uiState.isOtpFromSms,
            digits = TOTAL_DIGITS,
            placeHolder = stringResource(id = R.string.sign_up_otp_code_placeholder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_otp_code_required),
            isError = viewModel.uiState.otpError.first,
            errorMessage = stringResource(id = viewModel.uiState.otpError.second)
        )

        when (viewModel.uiState.phaseCount) {
            PHASE_ONE, PHASE_THREE, PHASE_FIVE -> {
                Row {
                    Text(
                        text = stringResource(id = viewModel.getPhaseResourceString()),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 32.dp),
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
                        viewModel.onUIEvent(
                            SignUpOtpViewModel.UIEvent.OnCallMutationSendPinProcess(
                                userData?.identification ?: "",
                                userData?.firstName ?: "",
                                userData?.email ?: "",
                                userData?.phoneNumber ?: "",
                                viewModel.uiState.otpResend ?: SEND_METHOD_PHONE,
                                userData?.pkUser ?: "",
                                idBrand ?: 0,
                                userData?.email ?: ""
                            )
                        )
                    }
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

    if (viewModel.uiState.openUserBlockedDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openUserBlockedDialog.titleResource),
            message = viewModel.uiState.openUserBlockedDialog.description,
            positiveButtonText = stringResource(id = viewModel.uiState.openUserBlockedDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openUserBlockedDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openUserBlockedDialog.isActive,
            onPositiveAction = {
                context.openWhatsAppDeepLink(viewModel.linkWhatsapp)
                viewModel.onUIEvent(OnNavigateToSignIn)
            },
            onNegativeAction = {
                viewModel.onUIEvent(OnNavigateToSignIn)
            },
            isCancelable = false
        )
    }
}
