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
import androidx.navigation.NavBackStackEntry
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.navigation.navgraph.CREDIT_STEP
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_USER_REQUEST
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.Companion.CREDIT_INDICATOR_TOTAL_STEPS
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnInitializeText
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnUpdateUserData
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressScreen
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountScreen
import com.multimoney.multimoney.presentation.ui.credit.document.CreditDocumentScreen
import com.multimoney.multimoney.presentation.ui.credit.homeaddress.HomeAddressScreen
import com.multimoney.multimoney.presentation.ui.credit.jobinfo.JobPlaceScreen
import com.multimoney.multimoney.presentation.ui.credit.montlyincome.MonthlyIncomeScreen
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryTertiaryUnderLined
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.StepProgressBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun CreditScreen(
    navBackStackEntry: NavBackStackEntry,
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: CreditViewModel = hiltViewModel()
) {

    val focusManager = LocalFocusManager.current

    // Navigation
    LaunchedEffect(true) {
        viewModel.executeNavigation(onNavigate = onNavigate, onPopAndNavigate = onPopAndNavigate)
        viewModel.onUIEvent(
            OnUpdateUserData(
                navBackStackEntry.arguments?.getString(ID_BRAND, "") ?: "",
                navBackStackEntry.arguments?.getString(PK_USER, "") ?: "",
                navBackStackEntry.arguments?.getString(IDENTIFICATION, "") ?: "",
                navBackStackEntry.arguments?.getString(EMAIL, "") ?: "",
                (navBackStackEntry.arguments?.getString(
                    CREDIT_STEP,
                    CreditStep.One.id.toString()
                ))?.toInt() ?: 0,
                navBackStackEntry.arguments?.getString(ID_USER_REQUEST, "") ?: ""
            )
        )
    }

    viewModel.onUIEvent(OnInitializeText(stringResource(id = string.credit_close_dialog_description)))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
    ) {
        Column {
            TopNavBar(
                isLeftButtonVisible = viewModel.uiState.currentStep != CreditStep.One.id,
                isRightButtonVisible = viewModel.uiState.isCloseVisible,
                onLeftButtonClick = { viewModel.onUIEvent(OnBackClick(focusManager)) },
                onRightButtonClick = { viewModel.onUIEvent(OnCloseClick(focusManager)) })
            if (viewModel.uiState.currentStep > CreditStep.One.id && viewModel.uiState.currentStep < CreditStep.Six.id) {
                StepProgressBar(
                    steps = CREDIT_INDICATOR_TOTAL_STEPS,
                    currentStep = viewModel.uiState.currentStep - 1,
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
                step = viewModel.uiState.currentStep,
                onNavigate = onNavigate,
                viewModel = viewModel
            )

            Column {
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
                if (viewModel.uiState.isCurrentLocationButtonVisible) {
                    CustomButton(
                        text = stringResource(id = R.string.credit_home_address_select_current_location),
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth()
                            .height(48.dp),
                        onClick = {
                            // active the location to select de current location
                        },
                        buttonType = PrimaryTertiaryUnderLined
                    )
                }
            }
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
    onNavigate: (NavEvent.Navigate) -> Unit,
    viewModel: CreditViewModel
) {
    when (step) {
        CreditStep.One.id -> CreditAmountScreen(
            onNavigate = onNavigate,
            sharedViewModel = viewModel
        )
        CreditStep.Two.id -> MonthlyIncomeScreen(sharedViewModel = viewModel)
        CreditStep.Three.id -> JobPlaceScreen(sharedViewModel = viewModel)
        CreditStep.Four.id -> CompanyAddressScreen(sharedViewModel = viewModel)
        CreditStep.Five.id -> HomeAddressScreen(sharedViewModel = viewModel)
        else -> CreditDocumentScreen(sharedViewModel = viewModel)
    }
}