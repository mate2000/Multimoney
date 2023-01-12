package com.multimoney.multimoney.presentation.ui.login.registereduser.email

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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.registereduser.email.RegisteredUserEmailViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.login.registereduser.email.RegisteredUserEmailViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.login.registereduser.email.RegisteredUserEmailViewModel.UIEvent.OnEmailValueChange
import com.multimoney.multimoney.presentation.ui.login.registereduser.email.RegisteredUserEmailViewModel.UIEvent.OnValidateEmail
import com.multimoney.multimoney.presentation.ui.login.registereduser.email.RegisteredUserEmailViewModel.UIState
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun RegisteredUserEmailScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: RegisteredUserEmailViewModel = hiltViewModel()
) {
    // Navigation
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
        }
    }
    RegisteredUserEmailContent(
        uiState = viewModel.uiState,
        maskedMail = viewModel.userData?.maskedMail.orEmpty(),
        onEmailValueChange = { value -> viewModel.onUIEvent(OnEmailValueChange(value)) },
        onValidateEmail = { viewModel.onUIEvent(OnValidateEmail) },
        onBackClick = { viewModel.onUIEvent(OnBackClick) },
        onContinueClick = { viewModel.onUIEvent(OnContinueClick) }
    )
}

@Composable
@Preview
fun RegisteredUserEmailContent(
    uiState: UIState = UIState(),
    maskedMail: String = "",
    onEmailValueChange: (String) -> Unit = {},
    onValidateEmail: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    Column(modifier = Modifier.background(MultimoneyTheme.colors.background).fillMaxSize()) {
        TopNavBar(
            isRightButtonVisible = false,
            onLeftButtonClick = { onBackClick() }
        )
        Column(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(R.string.registered_user_email_title, maskedMail),
                    modifier = Modifier.padding(top = 42.dp),
                    style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.text
                )
                Text(
                    text = stringResource(R.string.registered_user_email_subtitle),
                    modifier = Modifier.padding(top = 16.dp),
                    style = Typography.body2,
                    color = MultimoneyTheme.colors.labelText
                )
                CustomOutlinedTextField(
                    value = uiState.email,
                    onValueChange = { value -> onEmailValueChange(value) },
                    onDebounceValidation = { onValidateEmail() },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    placeHolder = stringResource(id = R.string.registered_user_email_placeholder),
                    modifier = Modifier.padding(top = 36.dp),
                    isRequired = true,
                    isRequiredMessage = stringResource(id = R.string.registered_user_email_required),
                    isError = uiState.emailError.first,
                    errorMessage = stringResource(id = uiState.emailError.second)
                )
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

    BackHandler {
        onBackClick()
    }
}
