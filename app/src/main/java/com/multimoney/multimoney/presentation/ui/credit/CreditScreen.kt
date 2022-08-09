package com.multimoney.multimoney.presentation.ui.credit

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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.Companion.CREDIT_INDICATOR_TOTAL_STEPS
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnInitializeText
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountScreen
import com.multimoney.multimoney.presentation.ui.credit.jobplace.JobPlaceScreen
import com.multimoney.multimoney.presentation.ui.credit.montlyincome.MonthlyIncomeScreen
import com.multimoney.multimoney.presentation.uielement.BackCloseNavBar
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.StepProgressBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun CreditScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: CreditViewModel = hiltViewModel()
) {

    val focusManager = LocalFocusManager.current

    // Navigation
    LaunchedEffect(true) {
        viewModel.executeNavigation(onNavigate = onNavigate, onPopAndNavigate = onPopAndNavigate)
    }

    viewModel.onUIEvent(OnInitializeText(stringResource(id = string.credit_close_dialog_description)))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
    ) {
        Column {
            BackCloseNavBar(
                isCloseVisible = viewModel.uiState.isCloseVisible,
                onBackClick = { viewModel.onUIEvent(OnBackClick(focusManager)) },
                onCloseClick = { viewModel.onUIEvent(OnCloseClick(focusManager)) })
            StepProgressBar(
                steps = CREDIT_INDICATOR_TOTAL_STEPS,
                currentStep = viewModel.uiState.currentStep,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            GetStepContent(
                step = viewModel.uiState.currentStep,
                viewModel = viewModel
            )
            CustomButton(
                onClick = { viewModel.onUIEvent(OnContinueClick(focusManager)) },
                text = stringResource(id = string.button_continue),
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 32.dp, top = 16.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                buttonType = PrimaryPrimary,
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
            title = stringResource(id = viewModel.uiState.openDialog.title),
            message = viewModel.uiState.openDialog.description,
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveText),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeText),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }
}

@Composable
fun GetStepContent(
    step: Int,
    viewModel: CreditViewModel
) {
    when (step) {
        CreditStep.One.id -> CreditAmountScreen(sharedViewModel = viewModel)
        CreditStep.Three.id -> JobPlaceScreen(sharedViewModel = viewModel)
        else -> MonthlyIncomeScreen(sharedViewModel = viewModel)
    }
}