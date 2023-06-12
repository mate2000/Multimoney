package com.multimoney.multimoney.presentation.ui.login.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomBiometricIconButton
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryTertiary

@Composable
fun SignInWithBiometric(
    modifier: Modifier = Modifier,
    onSignInWithBiometricAction: () -> Unit,
    onLinkEnterWithPassword: () -> Unit
) {
    val context = LocalContext.current
    Column(modifier = modifier, horizontalAlignment = CenterHorizontally) {
        Text(
            text = stringResource(id = R.string.sign_in_biometric_title),
            style = Typography.body2,
            color = MultimoneyTheme.colors.labelText
        )
        CustomBiometricIconButton(
            modifier = Modifier.padding(top = 32.dp),
            padding = 24.dp,
            icon = R.drawable.ic_fingerprint,
            onClick = {
                onSignInWithBiometricAction()
            }
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 61.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            CustomButton(
                onClick = { onLinkEnterWithPassword() },
                text = stringResource(id = R.string.sign_in_biometric_enter_with_password),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                buttonType = PrimaryTertiary
            )
        }
    }
}