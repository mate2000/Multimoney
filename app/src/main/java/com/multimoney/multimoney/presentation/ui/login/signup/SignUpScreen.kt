package com.multimoney.multimoney.presentation.ui.login.signup

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.Companion.SIGN_UP_TOTAL_STEPS
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.Companion.STEP_ONE
import com.multimoney.multimoney.presentation.uielement.BackCloseNavBar
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.StepProgressBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
@Preview
fun SignUpScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SignUpViewModel = hiltViewModel()
) {
    // Navigation
    LaunchedEffect(true) {
        viewModel.executeNavigation(onNavigate = onNavigate, onPopAndNavigate = onPopAndNavigate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            BackCloseNavBar(
                isBackVisible = true,
                isCloseVisible = viewModel.isCloseVisible,
                onBackClick = { viewModel.previousStep() },
                onCloseClick = {
                    viewModel.popAndNavigateTo(
                        route = Screen.SignInScreen.route,
                        popTo = Screen.SignUpScreen.route
                    )
                })
            StepProgressBar(
                steps = SIGN_UP_TOTAL_STEPS,
                currentStep = viewModel.currentStep,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            GetStepContent(step = viewModel.currentStep, viewModel = viewModel)
        }


        CustomButton(
            onClick = { viewModel.nextStep() },
            text = stringResource(id = R.string.button_continue),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.Bottom)
                .padding(top = 16.dp, bottom = 32.dp, start = 16.dp, end = 16.dp),
            enable = viewModel.isContinueEnabled
        )
    }

    LoadingIndicator(viewModel.isLoading)

    BackHandler {
        viewModel.previousStep()
    }
}

@Composable
fun GetStepContent(
    step: Int,
    viewModel: SignUpViewModel
) {
    viewModel.isFormStepValid()
    when (step) {
        STEP_ONE -> SignUpEmailScreen(viewModel = viewModel)
        else -> SignUpEmailScreen(viewModel = viewModel)
    }
}
