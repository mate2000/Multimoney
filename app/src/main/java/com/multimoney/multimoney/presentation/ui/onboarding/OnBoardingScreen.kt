package com.multimoney.multimoney.presentation.ui.onboarding

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.theme.DefaultBlack
import com.multimoney.multimoney.presentation.theme.LinkGreen
import com.multimoney.multimoney.presentation.theme.WhiteTransparency20
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.PoppinsFontFamily
import com.multimoney.multimoney.presentation.theme.WhiteTransparency70
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.LockScreenOrientation
import com.multimoney.multimoney.presentation.uielement.StoryProgressBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun OnBoardingScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit,
    viewModel: OnBoardingViewModel = hiltViewModel()
) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate)
    }
    OnBoarding(viewModel)
}

@Composable
fun OnBoarding(
    viewModel: OnBoardingViewModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
        Modifier
            .background(DefaultBlack)
            .fillMaxSize()
            .pointerInput(Unit) { viewModel.onPress(this) }
    ) {
        Column(Modifier.weight(0.4f)) {
            StoryProgressBar(
                steps = OnBoardingViewModel.MAX_STEPS,
                currentStep = viewModel.currentStep,
                paused = viewModel.isPressed,
                onFinished = viewModel.goToNextScreen,
                backgroundColor = WhiteTransparency20,
                progressColor = WhiteTransparency70,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(top = 12.dp)
            )
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .weight(0.6f)
        ) {
            Text(
                text = stringResource(id = viewModel.title),
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp)
                    .weight(0.13f),
                style = Typography.h4.copy(color = DefaultWhite, fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = stringResource(id = viewModel.subtitle),
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.11f),
                style = Typography.h6.copy(color = DefaultWhite)
            )
            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.04f),
                buttonType = CustomButtonType.PrimaryTertiary,
                text = stringResource(id = R.string.registration),
                onClick = {
                    viewModel.navigateToNextScreen(Screen.SignUpScreen.route)
                }
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .weight(0.05f),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.onboarding_account_already_created),
                    textAlign = TextAlign.Left,
                    modifier = Modifier.wrapContentSize(),
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontSize = 14.sp
                )
                ClickableText(
                    text = AnnotatedString(stringResource(id = R.string.sign_in)),
                    style = TextStyle(
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = LinkGreen,
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(start = 4.dp),
                    onClick = {
                        viewModel.navigateToNextScreen(Screen.SignInScreen.route)
                    }
                )
            }
        }
    }
}