package com.multimoney.multimoney.presentation.ui.login.signup.biometrics

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomImage

@Composable
@Preview
fun SignUpBiometricsScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CustomImage(
            drawableResource = R.drawable.ic_biometric_success,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .align(Alignment.CenterHorizontally)
                .weight(0.6f)
        )
        Column(
            Modifier
                .fillMaxWidth()
                .weight(0.6f)
        ) {
            Text(
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(id = R.string.sign_up_biometric_success_title),
                style = Typography.h5.copy(
                    color = MultimoneyTheme.colors.text,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentWidth(),
                text = stringResource(id = R.string.sign_up_biometric_success_subtitle),
                style = Typography.body1.copy(
                    color = MultimoneyTheme.colors.textSubhead,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            )
        }
        CustomButton(
            text = stringResource(id = R.string.finalize),
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.08f)
        )
    }
}