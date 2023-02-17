package com.multimoney.multimoney.presentation.ui.login.forgotpassword.request

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.extension.findActivity
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.request.RequestForgotPasswordViewModel.UIEvent.OnChangePasswordClick
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.request.RequestForgotPasswordViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.request.RequestForgotPasswordViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.request.RequestForgotPasswordViewModel.UIEvent.OnEmailValueChange
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.request.RequestForgotPasswordViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.login.forgotpassword.request.RequestForgotPasswordViewModel.UIEvent.OnValidateEmail
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters

@Composable
fun RequestForgotPasswordScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: RequestForgotPasswordViewModel = hiltViewModel()
) {
    // Properties
    val focusManager = LocalFocusManager.current
    val activity = LocalContext.current.findActivity() as FragmentActivity

    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopBackStack = onPopBackStack, onPopAndNavigate = onPopAndNavigate)
        }
    }
    BackHandler {
        viewModel.onUIEvent(OnNavigateBack(focusManager))
    }
    RequestForgotPasswordContent(
        focusManager = focusManager,
        email = viewModel.uiState.email,
        emailError = viewModel.uiState.emailError,
        isFormValid = viewModel.uiState.isFormValid,
        isLoading = viewModel.uiState.isLoading,
        isAlertResultVisible = viewModel.uiState.isAlertResultVisible,
        openDialog = viewModel.uiState.openDialog,
        onEmailValueChange = { value -> viewModel.onUIEvent(OnEmailValueChange(value)) },
        onValidateEmail = { viewModel.onUIEvent(OnValidateEmail) },
        onNavigateBack = { viewModel.onUIEvent(OnNavigateBack(focusManager)) },
        onCloseClick = { viewModel.onUIEvent(OnCloseClick(focusManager)) },
        onChangePasswordClick = { viewModel.onUIEvent(OnChangePasswordClick) },
        onContinueClick = { viewModel.onUIEvent(OnContinueClick(activity, focusManager)) }
    )
}

@Composable
@Preview
fun RequestForgotPasswordContent(
    focusManager: FocusManager = LocalFocusManager.current,
    email: String = "",
    emailError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
    isFormValid: Boolean = false,
    isLoading: Boolean = false,
    isAlertResultVisible: Boolean = false,
    openDialog: DialogParameters = DialogParameters(),
    onEmailValueChange: (String) -> Unit = {},
    onValidateEmail: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onContinueClick: () -> Unit = {}
) {
    if (isAlertResultVisible) {
        AlertResult(
            iconResource = R.drawable.ic_success_symbol,
            descriptionAnnotatedString = buildAnnotatedString {
                withStyle(
                    style = Typography.body1.toSpanStyle().copy(color = MultimoneyTheme.colors.labelText)
                ) { append(stringResource(id = string.request_forgot_password_success_title)) }
                withStyle(
                    style = Typography.h6.toSpanStyle()
                        .copy(color = MultimoneyTheme.colors.textLink, fontWeight = FontWeight.Bold, fontSize = 21.sp)
                ) { append(email) }
                withStyle(
                    style = Typography.body1.toSpanStyle().copy(color = MultimoneyTheme.colors.labelText)
                ) { append(stringResource(id = string.request_forgot_password_success_description)) }
            },
            buttonTextResource = R.string.request_forgot_password_success_button,
            onLeftButtonClick = { onNavigateBack() },
            onRightButtonClick = { onCloseClick() },
            onButtonClick = { onChangePasswordClick() }
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
                    onLeftButtonClick = { onNavigateBack() },
                    onRightButtonClick = { onCloseClick() }
                )
                Text(
                    text = stringResource(id = R.string.request_forgot_password_title),
                    modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp),
                    style = Typography.h6.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
                    color = MultimoneyTheme.colors.text
                )
                Text(
                    text = stringResource(id = R.string.request_forgot_password_subtitle),
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    style = Typography.subtitle1,
                    color = MultimoneyTheme.colors.subTitleText
                )
                CustomOutlinedTextField(
                    value = email,
                    onValueChange = { onEmailValueChange(it) },
                    onDebounceValidation = { onValidateEmail() },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onNext = { focusManager.clearFocus() }),
                    labelText = stringResource(id = string.label_email),
                    modifier = Modifier.padding(top = 36.dp, start = 16.dp, end = 16.dp),
                    isRequired = true,
                    isRequiredMessage = stringResource(id = string.request_forgot_password_email_required),
                    isError = emailError.first,
                    errorMessage = stringResource(id = emailError.second)
                )
            }
            Column {
                CustomButton(
                    onClick = { onContinueClick() },
                    text = stringResource(id = string.button_continue),
                    modifier = Modifier
                        .padding(bottom = 32.dp, top = 16.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth()
                        .height(48.dp),
                    enable = isFormValid,
                    buttonType = PrimaryPrimary
                )
            }
        }
    }

    if (openDialog.isActive.value) {
        CustomDialog(
            message = stringResource(id = openDialog.descriptionResource).ifEmpty { openDialog.description },
            positiveButtonText = stringResource(id = openDialog.positiveResource),
            openDialogCustom = openDialog.isActive,
            onPositiveAction = openDialog.positiveAction
        )
    }
    LoadingIndicator(isLoading)
}
