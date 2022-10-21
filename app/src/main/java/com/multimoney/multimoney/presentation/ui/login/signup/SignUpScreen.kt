package com.multimoney.multimoney.presentation.ui.login.signup

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_UP_STEP
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.Companion.SIGN_UP_INDICATOR_TOTAL_STEPS
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnInitializeText
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnMoveToStep
import com.multimoney.multimoney.presentation.ui.login.signup.email.SignUpEmailScreen
import com.multimoney.multimoney.presentation.ui.login.signup.idverification.SignUpIdVerificationScreen
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpScreen
import com.multimoney.multimoney.presentation.ui.login.signup.password.SignUpPasswordScreen
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataScreen
import com.multimoney.multimoney.presentation.ui.login.signup.phone.SignUpPhoneScreen
import com.multimoney.multimoney.presentation.ui.login.signup.splash.DEFAULT_STEP
import com.multimoney.multimoney.presentation.ui.smart.origin.sourceofincome.SourceOfIncomeScreen
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.StepProgressBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SignUpScreen(
    navBackStackEntry: NavBackStackEntry,
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current

    // Navigation
    LaunchedEffect(true) {
        viewModel.executeNavigation(onNavigate = onNavigate, onPopAndNavigate = onPopAndNavigate)
        navBackStackEntry.arguments?.getString(SIGN_UP_STEP, DEFAULT_STEP)?.let { step ->
            if (step != DEFAULT_STEP) {
                viewModel.onUIEvent(OnMoveToStep(step.toInt()))
            }
        }
    }

    viewModel.onUIEvent(OnInitializeText(stringResource(id = R.string.sign_up_close_dialog_description)))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
    ) {
        Column {
            TopNavBar(
                isLeftButtonVisible = viewModel.uiState.currentStep != SignUpStep.Six.id,
                isRightButtonVisible = viewModel.uiState.isCloseVisible,
                onLeftButtonClick = { viewModel.onUIEvent(OnBackClick(focusManager)) },
                onRightButtonClick = { viewModel.onUIEvent(OnCloseClick(focusManager)) })
            if (viewModel.uiState.currentStep != SignUpStep.Five.id) {
                StepProgressBar(
                    steps = SIGN_UP_INDICATOR_TOTAL_STEPS,
                    currentStep = if (viewModel.uiState.currentStep == SignUpStep.Six.id) SignUpStep.Five.id else viewModel.uiState.currentStep,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize(),
                //.verticalScroll(rememberScrollState()), // FIXME, revert this change
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            GetStepContent(
                step = viewModel.uiState.currentStep,
                viewModel = viewModel,
                onPopAndNavigate
            )
            CustomButton(
                onClick = { viewModel.onUIEvent(OnContinueClick(focusManager)) },
                text = stringResource(id = R.string.button_continue),
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 32.dp, top = 16.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                buttonType = CustomButtonType.PrimaryPrimary,
                enable = viewModel.uiState.isContinueEnabled
            )
        }
    }

    LoadingIndicator(viewModel.uiState.isLoading)

    BackHandler {
        viewModel.onUIEvent(OnBackClick(focusManager))
    }

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = viewModel.uiState.openDialog.description,
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }
}

@Composable
fun GetStepContent(
    step: Int,
    viewModel: SignUpViewModel,
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {}
) {
    val context = LocalContext.current
    when (step) {
       // SignUpStep.One.id -> SignUpEmailScreen(sharedViewModel = viewModel) // FIXME, revert this change
        SignUpStep.One.id -> SourceOfIncomeScreen() { sourceOfIncome ->
            // FIXME, should open the proper screen
            Toast.makeText(context, "${sourceOfIncome?.label}", Toast.LENGTH_SHORT).show()
        }
        SignUpStep.Two.id -> SignUpPersonalDataScreen(sharedViewModel = viewModel)
        SignUpStep.Three.id -> {
            SignUpPhoneScreen(sharedViewModel = viewModel)
        }
        SignUpStep.Four.id -> SignUpOtpScreen(
            onPopAndNavigate,
            sharedViewModel = viewModel
        )
        SignUpStep.Five.id -> SignUpIdVerificationScreen(sharedViewModel = viewModel)
        else -> {
            SignUpPasswordScreen(sharedViewModel = viewModel)
        }
    }
}
