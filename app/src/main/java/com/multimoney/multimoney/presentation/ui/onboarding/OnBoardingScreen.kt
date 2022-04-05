package com.multimoney.multimoney.presentation.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.theme.Complementary3500
import com.multimoney.multimoney.presentation.theme.PoppinsFontFamily
import com.multimoney.multimoney.presentation.theme.Primary600
import com.multimoney.multimoney.presentation.ui.onboarding.OnBoardingViewModel.Companion.STEP_ICON
import com.multimoney.multimoney.presentation.ui.onboarding.OnBoardingViewModel.Companion.STEP_SUBTITLE
import com.multimoney.multimoney.presentation.ui.onboarding.OnBoardingViewModel.Companion.STEP_TITLE
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomImage
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
    OnBoarding(viewModel)
}

@Composable
fun OnBoarding(
    viewModel: OnBoardingViewModel
) {
    val isPressed = remember { mutableStateOf(false) }
    val title = remember { mutableStateOf(R.string.onboarding_step_one_title) }
    val mutableCurrentStep = remember { mutableStateOf(1) }
    val subtitle = remember { mutableStateOf(R.string.onboarding_step_one_sub_title) }
    val icon = remember { mutableStateOf(R.drawable.ic_onboarding_step_one) }
    val goToNextScreen = {
        if (mutableCurrentStep.value <= OnBoardingViewModel.MAX_STEPS) {
            mutableCurrentStep.value++
            val newValues = viewModel.getStepContent(mutableCurrentStep.value)
            title.value = newValues[STEP_TITLE]
            subtitle.value = newValues[STEP_SUBTITLE]
            icon.value = newValues[STEP_ICON]
        }
    }
    val goToPreviousScreen = {
        if (mutableCurrentStep.value - 1 > 0) {
            mutableCurrentStep.value--
            val newValues = viewModel.getStepContent(mutableCurrentStep.value)
            title.value = newValues[STEP_TITLE]
            subtitle.value = newValues[STEP_SUBTITLE]
            icon.value = newValues[STEP_ICON]
        }
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
            .pointerInput(Unit) {
                val maxWidth = this.size.width
                detectTapGestures(
                    onPress = {
                        val pressStartTime = System.currentTimeMillis()
                        isPressed.value = true
                        this.tryAwaitRelease()
                        val pressEndTime = System.currentTimeMillis()
                        val totalPressTime = pressEndTime - pressStartTime
                        if (totalPressTime < 200) {
                            val isTapOnRightThreeQuarters = (it.x > (maxWidth / 4))
                            if (isTapOnRightThreeQuarters) {
                                goToNextScreen()
                            } else {
                                goToPreviousScreen()
                            }
                        }
                        isPressed.value = false
                    },
                )
            }
    ) {
        Column(Modifier.weight(0.4f)) {
            StoryProgressBar(
                OnBoardingViewModel.MAX_STEPS,
                mutableCurrentStep.value,
                isPressed.value,
                goToNextScreen,
                Color.White.copy(alpha = 0.4f),
                Complementary3500,
                Modifier
                    .wrapContentHeight()
                    .padding(top = 12.dp)
            )
            Text(
                text = stringResource(id = title.value),
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontSize = 34.sp
            )
            Text(
                text = stringResource(id = subtitle.value),
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = PoppinsFontFamily,
                color = Color.White,
                fontSize = 20.sp
            )
        }

        Column(
            Modifier
                .fillMaxSize()
                .weight(0.6f)
        ) {
            CustomImage(
                drawableResource = icon.value,
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
                    viewModel.popAndNavigateTo(
                        route = Screen.LoginScreen.route,
                        popTo = Screen.OnBoardingScreen.route
                    )
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
                        viewModel.popAndNavigateTo(
                            route = Screen.LoginScreen.route,
                            popTo = Screen.OnBoardingScreen.route
                        )

                    }
                )
            }
        }
    }
}