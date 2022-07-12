package com.multimoney.multimoney.presentation.ui.login.signin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnCallCognitoSignIn
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnFingerprintCheckedChanged
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel.UIEvent.OnUserPasswordValueChange
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomCheckBox
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField

@Composable
fun SignInWithPassword(
    viewModel: SignInViewModel,
    focusManager: FocusManager,
    modifier: Modifier = Modifier,
    isBiometricError: Boolean,
    isBiometricActive: Boolean,
    onSignInWithBiometricLink: () -> Unit
) {
    Column(modifier) {
        CustomOutlinedTextField(
            value = viewModel.uiState.userPassword,
            onValueChange = { viewModel.onUIEvent(OnUserPasswordValueChange(it)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone =
            {
                focusManager.clearFocus()
            }),
            labelText = stringResource(id = R.string.sign_in_label_password),
            isPassword = true,
            modifier = Modifier
                .padding(top = 16.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_in_password_required),
            isError = viewModel.uiState.userPasswordError.first,
            errorMessage = if (viewModel.uiState.userPasswordError.first) {
                stringResource(id = viewModel.uiState.userPasswordError.second)
            } else {
                null
            }
        )
        ClickableText(
            text = AnnotatedString(stringResource(id = R.string.sign_in_forgot_password)),
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 4.dp),
            style = Typography.body2.copy(
                textDecoration = TextDecoration.Underline,
                color = MultimoneyTheme.colors.textLink
            ),
            onClick = {}
        )
        if (!isBiometricError) {
            if (isBiometricActive) {
                ClickableText(
                    text = AnnotatedString(stringResource(id = R.string.sign_in_access_with_biometrics)),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 51.dp),
                    style = Typography.body2.copy(
                        textDecoration = TextDecoration.Underline,
                        color = MultimoneyTheme.colors.textLink
                    ),
                    onClick = { onSignInWithBiometricLink() }
                )
            } else {
                CustomCheckBox(
                    checked = viewModel.uiState.isFingerprintChecked,
                    onCheckedChange = { viewModel.onUIEvent(OnFingerprintCheckedChanged(it, it)) },
                    text = stringResource(id = R.string.sign_in_activate_fingerprint),
                    modifier = Modifier.padding(top = 51.dp)
                )
            }
        }
        CustomButton(
            onClick = { viewModel.onUIEvent(OnCallCognitoSignIn) },
            text = stringResource(id = R.string.sign_in),
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
                .height(48.dp),
            enable = viewModel.uiState.isSignInEnabled
        )
    }
}