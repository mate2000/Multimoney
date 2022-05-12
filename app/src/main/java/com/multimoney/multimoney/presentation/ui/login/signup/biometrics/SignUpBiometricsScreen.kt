package com.multimoney.multimoney.presentation.ui.login.signup.biometrics

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.navgraph.USER_EMAIL_ARG_KEY
import com.multimoney.multimoney.presentation.navigation.navgraph.USER_PASSWORD_ARG_KEY
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.PoppinsFontFamily
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SignUpBiometricsScreen(
    navBackStackEntry: NavBackStackEntry,
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SignUpBiometricsViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopAndNavigate = onPopAndNavigate)
            userEmail = navBackStackEntry.arguments?.getString(USER_EMAIL_ARG_KEY) ?: ""
            userPassword = navBackStackEntry.arguments?.getString(USER_PASSWORD_ARG_KEY) ?: ""
        }
    }

    val fragmentActivity = LocalContext.current as FragmentActivity

    viewModel.apply {
        biometricPromptTitle = stringResource(id = R.string.biometric_dialog_title)
        biometricPromptDescription = stringResource(id = R.string.biometric_dialog_description)
        biometricPromptNegative = stringResource(id = R.string.cancel)
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CustomImage(
            drawableResource = R.drawable.ic_biometrics,
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
                .weight(0.5f)
        ) {
            Text(
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(id = R.string.sign_up_biometrics_facial_title),
                style = Typography.h5.copy(
                    color = MultimoneyTheme.colors.text,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentWidth(),
                text = stringResource(id = R.string.sign_up_biometrics_facial_subtitle),
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
                text = AnnotatedString(stringResource(id = R.string.sign_up_biometrics_activate_later)),
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
                onClick = {
                    viewModel.navigateToSignUpCompleted()
                }
            )
            CustomButton(
                text = stringResource(id = R.string.sign_up_biometrics_activate_now),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                onClick = {
                    viewModel.apply {
                        biometricHelper.showBiometricPrompt(
                            title = biometricPromptTitle,
                            description = biometricPromptDescription,
                            negative = biometricPromptNegative,
                            activity = fragmentActivity,
                            processSuccess = ::biometricPromptForEncryptionSuccess,
                            processError = ::biometricPromptError
                        )
                    }
                }
            )
        }
    }
    BackHandler {
        viewModel.navigateToSignUpCompleted()
    }
}