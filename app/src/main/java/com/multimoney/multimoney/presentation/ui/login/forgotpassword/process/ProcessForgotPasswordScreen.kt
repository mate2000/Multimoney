package com.multimoney.multimoney.presentation.ui.login.forgotpassword.process

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.process.ProcessForgotPasswordViewModel.BaseEvent.OnResendOtpToastEvent
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.process.ProcessForgotPasswordViewModel.UIEvent.OnAlertButtonClick
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.process.ProcessForgotPasswordViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.process.ProcessForgotPasswordViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.process.ProcessForgotPasswordViewModel.UIEvent.OnNewPasswordConfirmationValueChange
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.process.ProcessForgotPasswordViewModel.UIEvent.OnNewPasswordValueChange
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.process.ProcessForgotPasswordViewModel.UIEvent.OnOtpValueChange
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.process.ProcessForgotPasswordViewModel.UIEvent.OnResendOtpClick
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.process.ProcessForgotPasswordViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.process.ProcessForgotPasswordViewModel.UIState
import com.multimoney.multimoney.presentation.ui.login.signup.password.PasswordRequirementLabels
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomModalWarningBottomSheet
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.OtpTextField
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProcessForgotPasswordScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: ProcessForgotPasswordViewModel = hiltViewModel()
) {
    // Properties
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val resendOtpToastText =
        stringResource(id = R.string.process_forgot_password_resend_otp_toast)
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Expanded)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopBackStack = onPopBackStack)
            onUIEvent(OnStart)
            baseEvent.collect { event ->
                when (event) {
                    is OnResendOtpToastEvent -> {
                        Toast.makeText(context, resendOtpToastText, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    BackHandler {
        when {
            bottomSheetState.isVisible -> {
                coroutineScope.launch {
                    bottomSheetState.hide()
                }
            }
            else -> viewModel.onUIEvent(OnCloseClick(focusManager))
        }
    }
    ProcessForgotPasswordContent(
        focusManager = focusManager,
        uiState = viewModel.uiState,
        onCloseClick = { viewModel.onUIEvent(OnCloseClick(focusManager)) },
        onContinueClick = { viewModel.onUIEvent(OnContinueClick(focusManager)) },
        onOtpValueChange = { value -> viewModel.onUIEvent(OnOtpValueChange(value)) },
        onResendClick = { viewModel.onUIEvent(OnResendOtpClick(focusManager)) },
        onNewPasswordValueChange = { value -> viewModel.onUIEvent(OnNewPasswordValueChange(value)) },
        onNewPasswordConfirmationValueChange = { value -> viewModel.onUIEvent(OnNewPasswordConfirmationValueChange(value)) },
        onAlertButtonClick = { viewModel.onUIEvent(OnAlertButtonClick(focusManager)) },
        bottomSheetState = bottomSheetState,
        coroutineScope = coroutineScope
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun ProcessForgotPasswordContent(
    focusManager: FocusManager = LocalFocusManager.current,
    uiState: UIState = UIState(),
    onCloseClick: () -> Unit = {},
    onContinueClick: () -> Unit = {},
    onOtpValueChange: (String) -> Unit = {},
    onResendClick: () -> Unit = {},
    onNewPasswordValueChange: (String) -> Unit = {},
    onNewPasswordConfirmationValueChange: (String) -> Unit = {},
    onAlertButtonClick: () -> Unit = {},
    bottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Expanded),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    if (uiState.isAlertResultVisible) {
        AlertResult(
            iconResource = uiState.alertResultIconResource,
            titleResource = uiState.alertResultTitleResource,
            descriptionResource = uiState.alertResultDescriptionResource,
            buttonTextResource = uiState.alertResultButtonTextResource,
            isRightButtonVisible = false,
            isLeftButtonVisible = false,
            onButtonClick = { onAlertButtonClick() }
        )
    } else {
        Column(
            modifier = Modifier
                .background(MultimoneyTheme.colors.background)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                TopNavBar(
                    isLeftButtonVisible = false,
                    onRightButtonClick = { onCloseClick() }
                )
                Text(
                    text = stringResource(id = uiState.titleResource),
                    modifier = Modifier.padding(top = 35.dp, start = 16.dp, end = 16.dp),
                    style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.text
                )
                OtpTextField(
                    value = uiState.otp,
                    onValueChange = { onOtpValueChange(it) },
                    labelText = stringResource(id = R.string.process_forgot_password_otp_label),
                    digits = ProcessForgotPasswordViewModel.OTP_TOTAL_DIGITS,
                    placeHolder = stringResource(id = string.process_forgot_password_otp_placeholder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, start = 16.dp, end = 16.dp),
                    isRequired = true,
                    isRequiredMessage = stringResource(id = string.process_forgot_password_otp_required)
                )
                ClickableText(
                    text = AnnotatedString(stringResource(id = string.process_forgot_password_resend_otp_button)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    style = Typography.body2.copy(
                        textDecoration = TextDecoration.Underline,
                        color = MultimoneyTheme.colors.textLink,
                        textAlign = TextAlign.End
                    ),
                    onClick = { onResendClick() }
                )
                CustomOutlinedTextField(
                    value = uiState.newPassword,
                    onValueChange = {
                        onNewPasswordValueChange(it)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.clearFocus()
                    }),
                    labelText = stringResource(id = string.process_forgot_password_new_password_label),
                    isPassword = true,
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    isRequired = true,
                    isRequiredMessage = stringResource(id = string.process_forgot_password_new_password_required),
                    isError = uiState.newPasswordError.first,
                    errorMessage = if (uiState.newPasswordError.first) {
                        stringResource(id = uiState.newPasswordError.second)
                    } else {
                        null
                    }
                )
                CustomOutlinedTextField(
                    value = uiState.newPasswordConfirmation,
                    onValueChange = {
                        onNewPasswordConfirmationValueChange(it)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    labelText = stringResource(id = string.process_forgot_password_new_password_confirmation_label),
                    isPassword = true,
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    isRequired = true,
                    isRequiredMessage = stringResource(id = string.process_forgot_password_new_password_confirmation_required),
                    isError = uiState.newPasswordConfirmationError.first,
                    errorMessage = if (uiState.newPasswordConfirmationError.first) {
                        stringResource(id = uiState.newPasswordConfirmationError.second)
                    } else {
                        null
                    }
                )
                FlowRow(
                    Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    PasswordRequirementLabels(
                        text = stringResource(id = string.sign_up_password_requirement_eight_characters_minimum),
                        state = uiState.eightCharactersMinimumState
                    )
                    PasswordRequirementLabels(
                        text = stringResource(id = string.sign_up_password_requirement_one_uppercase),
                        state = uiState.oneUppercaseState
                    )
                    PasswordRequirementLabels(
                        text = stringResource(id = string.sign_up_password_requirement_one_lowercase),
                        state = uiState.oneLowercaseState
                    )
                    PasswordRequirementLabels(
                        text = stringResource(id = string.sign_up_password_requirement_one_number),
                        state = uiState.oneNumberState
                    )
                    PasswordRequirementLabels(
                        text = stringResource(id = string.sign_up_password_requirement_one_characer),
                        state = uiState.oneCharacterState
                    )
                }
            }
            CustomButton(
                onClick = { onContinueClick() },
                text = stringResource(id = string.button_continue),
                modifier = Modifier
                    .padding(bottom = 32.dp, top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                enable = uiState.isFormValid,
                buttonType = PrimaryPrimary
            )
        }
    }
    LoadingIndicator(uiState.isLoading)
    CustomModalWarningBottomSheet(
        titleResource = string.password_security_bottom_sheet_general_title,
        descriptionText = buildAnnotatedString {
            withStyle(
                style = Typography.subtitle1.toSpanStyle().copy(
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(stringResource(id = string.password_security_bottom_sheet_general_description))
            }
            withStyle(
                style = Typography.subtitle1.toSpanStyle()
            ) {
                append(stringResource(id = string.password_security_bottom_sheet_signup_description))
            }
        },
        modalBottomSheetState = bottomSheetState,
        coroutineScope = coroutineScope
    )
}
