package com.multimoney.multimoney.presentation.ui.login.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomBiometricIconButton

@Composable
fun SignInWithBiometric(
    modifier: Modifier = Modifier,
    onSignInWithBiometricAction: () -> Unit,
    onLinkEnterWithPassword: () -> Unit
) {
    Column(modifier = modifier, horizontalAlignment = CenterHorizontally) {
        Text(text = stringResource(id = R.string.sign_in_biometric_title))
        CustomBiometricIconButton(
            modifier = Modifier.padding(top = 32.dp),
            padding = 24.dp,
            icon = R.drawable.ic_fingerprint,
            onClick = onSignInWithBiometricAction
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.sign_in_biometric_enter_with_password),
                textAlign = TextAlign.Left,
                modifier = Modifier.wrapContentSize(),
                style = Typography.subtitle1,
                color = MultimoneyTheme.colors.text
            )
            ClickableText(
                text = AnnotatedString(stringResource(id = R.string.sign_in_biometric_enter_with_password_link)),
                style = Typography.body1.copy(
                    textDecoration = TextDecoration.Underline,
                    color = MultimoneyTheme.colors.textLink
                ),
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 4.dp),
                onClick = {
                    onLinkEnterWithPassword()
                }
            )
        }
    }
}