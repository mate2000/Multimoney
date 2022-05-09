package com.multimoney.multimoney.presentation.ui.login.signin.biometric

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.GrayScale800
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Primary300
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.Typography

@Composable
fun SignInWithBiometric(
    modifier: Modifier = Modifier,
    onSignInWithBiometricAction: () -> Unit,
    onLinkEnterWithPassword: () -> Unit
) {
    Column(modifier = modifier, horizontalAlignment = CenterHorizontally) {
        Text(text = stringResource(id = R.string.sign_in_biometric_title))
        Box(
            modifier = Modifier
                .padding(top = 32.dp)
                .clip(CircleShape)
                .background(Primary500)
                .border(1.dp, Primary300, CircleShape)
                .padding(24.dp)
        ) {
            IconButton(
                onClick = onSignInWithBiometricAction
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_fingerprint),
                    contentDescription = "",
                    tint = DefaultWhite
                )
            }
        }
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
                color = GrayScale800
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
                onClick = { onLinkEnterWithPassword() }
            )
        }
    }
}