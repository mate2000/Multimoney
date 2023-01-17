package com.multimoney.multimoney.presentation.ui.smart

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.SmartSteps
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCallMutationInitialRequest
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCloseAlertClick
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCtaAlertClick
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnInitializeText
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiary.SmartBeneficiaryScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress.SmartLivingAddressScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeScreen
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.StepProgressBar
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SmartScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SmartViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current

    // Navigation
    LaunchedEffect(true) {
        viewModel.executeNavigation(
            onNavigate = onNavigate,
            onPopAndNavigate = onPopAndNavigate,
            onPopBackStack = onPopBackStack
        )
        viewModel.onUIEvent(OnCallMutationInitialRequest)
    }

    viewModel.onUIEvent(
        OnInitializeText(
            stringResource(R.string.smart_close_origination_dialog_description)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
    ) {
        Column {
            TopNavBar(
                isLeftButtonVisible = viewModel.uiState.currentStep <= viewModel.getTotalStepperCounter(),
                isRightButtonVisible = viewModel.uiState.isCloseVisible,
                onLeftButtonClick = { viewModel.onUIEvent(OnBackClick(focusManager)) },
                onRightButtonClick = { viewModel.onUIEvent(OnCloseClick(focusManager)) }
            )
            if (viewModel.uiState.currentStep <= viewModel.getTotalStepperCounter()) {
                StepProgressBar(
                    steps = viewModel.getTotalStepperCounter(),
                    currentStep = viewModel.uiState.currentStep,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                )
            }
        }
        Column(
            modifier = Modifier.weight(0.1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Each country has it's own steps on the smart origination flow
            if (viewModel.idBrandAsInt == Brand.ElSalvador.id) {
                GetSvStepContent(
                    step = viewModel.uiState.currentStep,
                    viewModel = viewModel
                )
            } else {
                GetCrStepContent(
                    step = viewModel.uiState.currentStep,
                    viewModel = viewModel
                )
            }
        }
        CustomButton(
            onClick = { viewModel.onUIEvent(OnContinueClick(focusManager)) },
            text = stringResource(id = viewModel.uiState.buttonTextRes),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 32.dp, top = 16.dp)
                .fillMaxWidth()
                .height(48.dp),
            buttonType = PrimaryPrimary,
            enable = viewModel.uiState.isContinueEnabled,
            visible = viewModel.uiState.isContinueVisible
        )
    }

    LoadingIndicator(viewModel.uiState.isLoading)
    BackHandler {
        viewModel.onUIEvent(OnBackClick(focusManager))
    }

    if (viewModel.uiState.isAlertResultVisible) {
        AlertResult(
            titleString = viewModel.uiState.alertResultTitle
                ?: stringResource(R.string.error_no_internet_title),
            descriptionString = viewModel.uiState.alertResultDescription
                ?: stringResource(R.string.smart_account_no_internet_error_description),
            buttonTextResource = R.string.common_try_again,
            isLeftButtonVisible = false,
            onRightButtonClick = { viewModel.onUIEvent(OnCloseAlertClick) },
            onButtonClick = { viewModel.onUIEvent(OnCtaAlertClick(focusManager)) }
        )
    }

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = viewModel.uiState.openDialog.description.ifBlank {
                stringResource(viewModel.uiState.openDialog.descriptionResource)
            },
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }

    if (viewModel.uiState.bottomSheetState.isVisible) {
        viewModel.uiState.bottomSheet()
    }
}

@Composable
fun GetSvStepContent(step: Int, viewModel: SmartViewModel) {
    when (step) {
        SmartSteps.One.id -> SmartDocumentScreen(sharedViewModel = viewModel)
        SmartSteps.Two.id -> SmartLivingAddressScreen(sharedViewModel = viewModel)
        SmartSteps.Three.id -> SourceIncomeScreen(sharedViewModel = viewModel)
        SmartSteps.Four.id -> SmartBeneficiaryScreen(sharedViewModel = viewModel)
        SmartSteps.Five.id -> SmartFactaScreen(sharedViewModel = viewModel)
    }
}

@Composable
fun GetCrStepContent(step: Int, viewModel: SmartViewModel) {
    when (step) {
        SmartSteps.One.id -> SmartLivingAddressScreen(sharedViewModel = viewModel)
        SmartSteps.Two.id -> SourceIncomeScreen(sharedViewModel = viewModel)
        SmartSteps.Three.id -> SmartFactaScreen(sharedViewModel = viewModel)
    }
}
