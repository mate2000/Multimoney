package com.multimoney.multimoney.presentation.ui.onboarding

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.layout.ContentScale
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
import com.multimoney.multimoney.presentation.theme.Complementary3500
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.PoppinsFontFamily
import com.multimoney.multimoney.presentation.theme.Primary600
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.onboarding.OnBoardingViewModel.UIEvent.OnGoToNextScreen
import com.multimoney.multimoney.presentation.ui.onboarding.OnBoardingViewModel.UIEvent.OnNavigateToNextScreen
import com.multimoney.multimoney.presentation.ui.onboarding.OnBoardingViewModel.UIEvent.OnPress
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomImage
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary600)
    ) {
        Spacer(modifier = Modifier.weight(0.4f))
        CustomImage(
            drawableResource = R.drawable.ic_onboarding_background,
            modifier = Modifier
                .weight(0.6f)
                .fillMaxWidth(),
            contentScale = ContentScale.FillBounds
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
        Modifier
            .padding(16.dp)
            .fillMaxSize()
            .pointerInput(Unit) { viewModel.onUIEvent(OnPress(this)) }
    ) {
        Column(Modifier.weight(0.4f)) {
            StoryProgressBar(
                steps = OnBoardingViewModel.MAX_STEPS,
                currentStep = viewModel.currentStep,
                paused = viewModel.uiState.isPressed,
                onFinished = { viewModel.onUIEvent(OnGoToNextScreen) },
                backgroundColor = Color.White.copy(alpha = 0.4f),
                progressColor = Complementary3500,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(top = 12.dp)
            )
            Text(
                text = stringResource(id = viewModel.uiState.title),
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                style = Typography.h4.copy(color = DefaultWhite, fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = stringResource(id = viewModel.uiState.subtitle),
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth(),
                style = Typography.h6.copy(color = DefaultWhite)
            )
        }

        Column(
            Modifier
                .fillMaxSize()
                .weight(0.6f)
        ) {
            CustomImage(
                drawableResource = viewModel.uiState.icon,
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.CenterHorizontally)
                    .weight(0.78f),
                contentScale = ContentScale.FillBounds
            )
            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.11f),
                buttonType = CustomButtonType.PrimaryTertiary,
                text = stringResource(id = R.string.registration),
                onClick = {
                    viewModel.onUIEvent(OnNavigateToNextScreen(Screen.SignUpScreen.route))
                }
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .weight(0.11f),
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
                        color = Color.White,
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(start = 4.dp),
                    onClick = {
                        viewModel.onUIEvent(OnNavigateToNextScreen(Screen.SignInScreen.route))
                    }
                )
            }
        }
    }
}