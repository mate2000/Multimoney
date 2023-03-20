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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.Companion.SIGN_UP_INDICATOR_TOTAL_STEPS
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnMoveToStep
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnUpdateIso3Country
import com.multimoney.multimoney.presentation.ui.login.signup.email.SignUpEmailScreen
import com.multimoney.multimoney.presentation.ui.login.signup.idverification.SignUpIdVerificationScreen
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpScreen
import com.multimoney.multimoney.presentation.ui.login.signup.password.SignUpPasswordScreen
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataScreen
import com.multimoney.multimoney.presentation.ui.login.signup.phone.SignUpPhoneScreen
import com.multimoney.multimoney.presentation.ui.login.signup.splash.DEFAULT_STEP
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomModalWarningBottomSheet
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.StepProgressBar
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SignUpScreen(
    isRestart: Boolean = true,
    step: String,
    idBrand: Int? = 0,
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val actualStep = if(isRestart) step else DEFAULT_STEP

    if (step != DEFAULT_STEP) {
        viewModel.onUIEvent(SignUpViewModel.UIEvent.OnSetIdBrand(idBrand = idBrand ?: 0))
    }
    val context = LocalContext.current

    // Navigation
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onNavigate = onNavigate, onPopAndNavigate = onPopAndNavigate)
            onUIEvent(OnUpdateIso3Country(context.resources.configuration.locale.isO3Country))
            if (actualStep != DEFAULT_STEP) {
                onUIEvent(OnMoveToStep(actualStep.toInt()))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
    ) {
        Column {
            TopNavBar(
                isRightButtonVisible = viewModel.uiState.isCloseVisible,
                onLeftButtonClick = { viewModel.onUIEvent(OnBackClick(focusManager)) },
                onRightButtonClick = {
                    viewModel.onUIEvent(OnCloseClick(focusManager = focusManager))
                }
            )
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            GetStepContent(
                isRestart = isRestart,
                onNavigate = onNavigate,
                step = viewModel.uiState.currentStep,
                viewModel = viewModel,
                onPopAndNavigate = onPopAndNavigate
            )
            CustomButton(
                onClick = { viewModel.onUIEvent(OnContinueClick(focusManager)) },
                text = stringResource(id = R.string.button_continue),
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 32.dp, top = 16.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                buttonType = CustomButtonType.PrimaryPrimary,
                enable = viewModel.uiState.isContinueEnabled
            )
        }
    }

    LoadingIndicator(viewModel.uiState.isLoading)

    BackHandler {
        viewModel.onUIEvent(OnBackClick(focusManager))
    }

    if (viewModel.uiState.currentStep == SignUpStep.Six.id) {
        CustomModalWarningBottomSheet(
            titleResource = R.string.password_security_bottom_sheet_general_title,
            descriptionText = buildAnnotatedString {
                withStyle(
                    style = Typography.subtitle1.toSpanStyle().copy(
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(stringResource(id = R.string.password_security_bottom_sheet_general_description))
                }
                withStyle(
                    style = Typography.subtitle1.toSpanStyle()
                ) {
                    append(stringResource(id = R.string.password_security_bottom_sheet_signup_description))
                }
            },
            modalBottomSheetState = viewModel.uiState.bottomSheetVisibleState,
            coroutineScope = coroutineScope
        )
    }

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction,
            onNegativeAction = viewModel.uiState.openDialog.negativeAction,
            isCancelable = viewModel.uiState.openDialog.isCancelable
        )
    }
}

@Composable
fun GetStepContent(
    isRestart: Boolean = true,
    step: Int,
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: SignUpViewModel,
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {}
) {
    when (step) {
        SignUpStep.One.id -> SignUpEmailScreen(sharedViewModel = viewModel)
        SignUpStep.Two.id -> SignUpPersonalDataScreen(
            isRestart = isRestart,
            onNavigate = onNavigate,
            sharedViewModel = viewModel
        )
        SignUpStep.Three.id -> SignUpPhoneScreen(sharedViewModel = viewModel)
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
