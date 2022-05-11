package com.multimoney.multimoney.presentation.ui.login.signin.password

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import com.multimoney.multimoney.presentation.ui.login.signin.SignInViewModel
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomCheckBox
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField

@Composable
fun SignInWithPassword(
    signInViewModel: SignInViewModel,
    focusManager: FocusManager,
    openDialogCustom: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    isBiometricActive: Boolean,
    showBiometricSignIn: Boolean,
    onSignInWithBiometricLink: () -> Unit
) {
    Column(modifier) {
        CustomOutlinedTextField(
            value = signInViewModel.userPassword,
            onValueChange =
            {
                signInViewModel.apply {
                    signInViewModel.userPassword = it
                    clearUserPasswordError()
                    isFormValid()
                }
            },
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
            isError = signInViewModel.userPasswordError.first,
            errorMessage = if (signInViewModel.userPasswordError.first) {
                stringResource(id = signInViewModel.userPasswordError.second)
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
        if (!showBiometricSignIn && isBiometricActive) {
            CustomCheckBox(
                checked = signInViewModel.isFingerprintChecked,
                onCheckedChange =
                {
                    signInViewModel.isFingerprintChecked = it
                    if (it) {
                        openDialogCustom.value = true
                    }
                },
                text = stringResource(id = R.string.sign_in_activate_fingerprint),
                modifier = Modifier.padding(top = 51.dp)
            )
        } else {
            ClickableText(
                text = AnnotatedString(stringResource(id = R.string.sign_in_activate_fingerprint)),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 51.dp),
                style = Typography.body2.copy(
                    textDecoration = TextDecoration.Underline,
                    color = MultimoneyTheme.colors.textLink
                ),
                onClick = { onSignInWithBiometricLink() }
            )
        }
        CustomButton(
            onClick = {
                signInViewModel.signIn()
            },
            text = stringResource(id = R.string.sign_in),
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
                .height(48.dp),
            enable = signInViewModel.isSignInEnabled
        )
    }
}