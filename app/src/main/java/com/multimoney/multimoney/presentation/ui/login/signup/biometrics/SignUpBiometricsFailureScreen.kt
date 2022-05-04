package com.multimoney.multimoney.presentation.ui.login.signup.biometrics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.PoppinsFontFamily
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomImage

@Composable
@Preview
fun SignUpBiometricsFailureScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CustomImage(
            drawableResource = R.drawable.ic_biometrics_failure,
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
                text = stringResource(id = R.string.sign_up_biometric_failure_title),
                style = Typography.h5.copy(
                    color = MultimoneyTheme.colors.text,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentWidth(),
                text = stringResource(id = R.string.sign_up_biometric_failure_subtitle),
                style = Typography.body1.copy(
                    color = MultimoneyTheme.colors.textSubhead,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            )
        }
        Column(
            Modifier
                .fillMaxWidth()
                .weight(0.17f)
        ) {
            ClickableText(
                text = AnnotatedString(stringResource(id = R.string.sign_up_biometric_failure_retry)),
                style = TextStyle(
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = Primary500,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp),
                onClick = {}
            )
            CustomButton(
                text = stringResource(id =R.string.finalize),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                onClick = {}
            )
        }
    }
}