package com.multimoney.multimoney.presentation.ui.visa.verifydeposit

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.Companion.PHONE_HARDCODED
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.BaseEvent.OnOpenWhatsApp
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.Companion.TOTAL_DIGITS
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.UIEvent.OnAlertButtonClick
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.UIEvent.OnAlertCloseClick
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.UIEvent.OnDoNotSeeClick
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.UIEvent.OnMicroDepositValueChange
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.UIEvent.OnResendClick
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.UIState
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.uielement.VerifyDepositField
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink

@Composable
@Preview
fun VisaVerifyDepositScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: VisaVerifyDepositViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val whatsAppLink = stringResource(
        id = string.whatsapp_deep_link,
        PHONE_HARDCODED
    )

    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
            OnStart(whatsAppLink)
            baseEvent.collect { event ->
                when (event) {
                    is OnOpenWhatsApp -> {
                        context.openWhatsAppDeepLink(viewModel.linkWhatsapp)
                    }
                }
            }
        }
    }

    viewModel.apply {
        VisaVerifyDepositContent(
            uiState = uiState,
            onMicroDepositValueChange = { value -> onUIEvent(OnMicroDepositValueChange(value)) },
            onDoNotSeeClick = { onUIEvent(OnDoNotSeeClick) },
            onResendClick = { onUIEvent(OnResendClick) },
            onCloseClick = { onUIEvent(OnCloseClick) },
            onBackClick = { onUIEvent(OnBackClick) },
            onContinueClick = { onUIEvent(OnContinueClick) },
            onAlertCloseClick = { onUIEvent(OnAlertCloseClick) },
            onAlertButtonClick = { onUIEvent(OnAlertButtonClick) }
        )
        BackHandler {
            onUIEvent(OnBackClick)
        }
    }
}

@Composable
@Preview
fun VisaVerifyDepositContent(
    uiState: UIState = UIState(),
    onMicroDepositValueChange: (String) -> Unit = {},
    onDoNotSeeClick: () -> Unit = {},
    onResendClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {},
    onAlertCloseClick: () -> Unit = {},
    onAlertButtonClick: () -> Unit = {}
) {
    if (uiState.isAlertResultVisible) {
        uiState.apply {
            AlertResult(
                iconResource = alertResultIconResource,
                titleResource = alertResultTitleResource,
                descriptionResource = alertResultDescriptionResource,
                descriptionString = alertResultDescription,
                buttonTextResource = alertResultButtonResource,
                isLeftButtonVisible = false,
                onRightButtonClick = { onAlertCloseClick() },
                onButtonClick = { onAlertButtonClick() }
            )
        }
    } else {
        Column(
            modifier = Modifier
                .background(MultimoneyTheme.colors.background)
                .fillMaxSize()
        ) {
            TopNavBar(
                onLeftButtonClick = { onBackClick() },
                onRightButtonClick = { onCloseClick() }
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
                            id = R.string.visa_direct_verify_deposit_title
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(top = 24.dp).fillMaxWidth()
                    )

                    Text(
                        style = Typography.body2.copy(color = MultimoneyTheme.colors.labelText),
                        text = stringResource(id = R.string.visa_direct_verify_deposit_subtitle),
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )

                    ClickableText(
                        text = AnnotatedString(stringResource(id = R.string.visa_direct_verify_deposit_do_not_see)),
                        modifier = Modifier.padding(top = 48.dp),
                        style = Typography.body2.copy(
                            textDecoration = TextDecoration.Underline,
                            color = MultimoneyTheme.colors.textLink
                        ),
                        onClick = { onDoNotSeeClick() }
                    )

                    // Fields
                    VerifyDepositField(
                        value = uiState.microDeposit,
                        onValueChange = { onMicroDepositValueChange(it) },
                        digits = TOTAL_DIGITS,
                        placeHolder = stringResource(id = R.string.visa_direct_verify_deposit_micro_deposit_placeholder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        isError = uiState.microDepositError.first,
                        errorMessage = stringResource(id = uiState.microDepositError.second)
                    )

                    if (uiState.isTimerRunning) {
                        Row {
                            Text(
                                text = stringResource(id = R.string.visa_direct_verify_deposit_timer_begin),
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
                                text = stringResource(id = R.string.visa_direct_verify_deposit_timer_end),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 32.dp),
                                style = Typography.body2.copy(color = MultimoneyTheme.colors.textSubhead)
                            )
                        }
                    } else {
                        ClickableText(
                            text = AnnotatedString(stringResource(id = R.string.visa_direct_verify_deposit_resend)),
                            modifier = Modifier.padding(top = 32.dp),
                            style = Typography.body2.copy(
                                textDecoration = TextDecoration.Underline,
                                color = MultimoneyTheme.colors.textLink
                            ),
                            onClick = { onResendClick() }
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
}
