package com.multimoney.multimoney.presentation.ui.login.signup

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.Companion.SIGN_UP_TOTAL_STEPS
import com.multimoney.multimoney.presentation.ui.login.signup.idverification.SignUpIdVerificationScreen
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpScreen
import com.multimoney.multimoney.presentation.ui.login.signup.password.SignUpPasswordScreen
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataScreen
import com.multimoney.multimoney.presentation.ui.login.signup.phone.SignUpPhoneScreen
import com.multimoney.multimoney.presentation.ui.test.shimmer.ShimmerTest
import com.multimoney.multimoney.presentation.uielement.BackCloseNavBar
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.StepProgressBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
@Preview
fun SignUpScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current

    // Navigation
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
    ) {
        Column {
            BackCloseNavBar(
                isBackVisible = true,
                isCloseVisible = viewModel.isCloseVisible,
                onBackClick = {
                    focusManager.clearFocus()
                    viewModel.previousStep()
                },
                onCloseClick = {
                    focusManager.clearFocus()
                    viewModel.popAndNavigateTo(
                        route = Screen.SignInScreen.route,
                        popTo = Screen.SignUpScreen.route
                    )
                })
            if (viewModel.currentStep != SignUpStep.Five.id) {
                StepProgressBar(
                    steps = SIGN_UP_TOTAL_STEPS,
                    currentStep = if (viewModel.currentStep == SignUpStep.Six.id) SignUpStep.Five.id else viewModel.currentStep,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            GetStepContent(step = viewModel.currentStep, viewModel = viewModel, onPopAndNavigate)
            CustomButton(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.nextAction.invoke()
                },
                text = stringResource(id = R.string.button_continue),
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 32.dp, top = 16.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                buttonType = CustomButtonType.PrimaryPrimary,
                enable = viewModel.isContinueEnabled
            )
        }
    }
    LoadingIndicator(viewModel.isLoading)

    BackHandler {
        viewModel.previousStep()
    }

    if (viewModel.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.openDialog.title),
            message = viewModel.openDialog.description,
            positiveButtonText = stringResource(id = viewModel.openDialog.positiveText),
            negativeButtonText = stringResource(id = viewModel.openDialog.negativeText),
            onPositiveAction = viewModel.openDialog.positiveAction,
            onNegativeAction = viewModel.openDialog.negativeAction,
            openDialogCustom = viewModel.openDialog.isActive
        )
    }
}

@Composable
fun GetStepContent(
    step: Int,
    viewModel: SignUpViewModel,
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {}
) {
    when (step) {
        //SignUpStep.One.id -> SignUpEmailScreen(sharedViewModel = viewModel)
        SignUpStep.One.id -> ShimmerTest()
        SignUpStep.Two.id -> SignUpPersonalDataScreen(sharedViewModel = viewModel)
        SignUpStep.Three.id -> SignUpPhoneScreen(sharedViewModel = viewModel)
        SignUpStep.Four.id -> SignUpOtpScreen(
            onPopAndNavigate,
            sharedViewModel = viewModel
        )
        SignUpStep.Five.id -> SignUpIdVerificationScreen(sharedViewModel = viewModel)
        else -> {
            SignUpPasswordScreen(sharedViewModel = viewModel)
            viewModel.apply {
                isBiometricAvailable = biometricHelper.isBiometricAvailable(LocalContext.current)
            }
        }
    }
}
