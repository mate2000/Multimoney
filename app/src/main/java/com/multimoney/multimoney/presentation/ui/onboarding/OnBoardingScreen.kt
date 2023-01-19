package com.multimoney.multimoney.presentation.ui.onboarding

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
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.PoppinsFontFamily
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency20
import com.multimoney.multimoney.presentation.theme.WhiteTransparency70
import com.multimoney.multimoney.presentation.ui.onboarding.OnBoardingViewModel.UIEvent.OnGoToNextScreen
import com.multimoney.multimoney.presentation.ui.onboarding.OnBoardingViewModel.UIEvent.OnNavigateToNextScreen
import com.multimoney.multimoney.presentation.ui.onboarding.OnBoardingViewModel.UIEvent.OnPress
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.StoryProgressBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun OnBoardingScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit,
    viewModel: OnBoardingViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
        Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
            .pointerInput(Unit) {
                viewModel.onUIEvent(OnPress(this))
            }
    ) {
        Column(Modifier.weight(0.4f)) {
            StoryProgressBar(
                steps = OnBoardingViewModel.MAX_STEPS,
                currentStep = viewModel.currentStep,
                paused = viewModel.uiState.isPressed,
                onFinished = {
                    viewModel.onUIEvent(OnGoToNextScreen)
                },
                backgroundColor = WhiteTransparency20,
                progressColor = WhiteTransparency70,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(top = 16.dp, end = 16.dp, start = 16.dp)
            )
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .weight(0.6f),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = stringResource(id = viewModel.uiState.title),
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
                    .weight(
                        0.14f,
                        false
                    ),
                style = Typography.h3.copy(
                    fontSize = 48.sp,
                    color = MultimoneyTheme.colors.onBoardingTitleText,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = stringResource(id = viewModel.uiState.subtitle),
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 80.dp)
                    .weight(0.14f, true),
                style = Typography.h6.copy(
                    color = MultimoneyTheme.colors.onBoardingSubText,
                    fontSize = 20.sp
                )
            )
            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.035f),
                buttonType = CustomButtonType.PrimaryPrimary,
                text = stringResource(id = R.string.registration),
                onClick = {
                    viewModel.onUIEvent(OnNavigateToNextScreen(Screen.SignUpScreen.baseRoute))
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
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )
                ClickableText(
                    text = AnnotatedString(stringResource(id = R.string.sign_in)),
                    style = TextStyle(
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = MultimoneyTheme.colors.textLink,
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(start = 4.dp),
                    onClick = {
                        viewModel.onUIEvent(OnNavigateToNextScreen(Screen.SignInScreen.baseRoute))
                    }
                )
            }
        }
    }
}
