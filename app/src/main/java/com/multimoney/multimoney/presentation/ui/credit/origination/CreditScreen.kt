package com.multimoney.multimoney.presentation.ui.credit.origination

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
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.Companion.CREDIT_INDICATOR_TOTAL_STEPS
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnSetCloseDialogTexts
import com.multimoney.multimoney.presentation.ui.credit.origination.companyaddress.CompanyAddressScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.creditamount.CreditAmountScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.CreditBankScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.document.CreditDocumentScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.homeaddress.HomeAddressScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.ibanaccount.IbanAccountScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeScreen
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryTertiaryUnderLined
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.LoadingMultiMoney
import com.multimoney.multimoney.presentation.uielement.StepProgressBar
import com.multimoney.multimoney.presentation.uielement.TopNavBar
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

    if (viewModel.idBrand.isNotEmpty()) {
        if (viewModel.idBrand.toInt() == Brand.Guatemala.id) {
            viewModel.onUIEvent(
                OnSetCloseDialogTexts(
                    R.string.credit_close_dialog_gt_title,
                    stringResource(id = R.string.credit_close_dialog_gt_description)
                )
            )
        } else {
            viewModel.onUIEvent(
                OnSetCloseDialogTexts(
                    R.string.credit_close_dialog_title,
                    stringResource(id = R.string.credit_close_dialog_description)
                )
            )
        }
    }

    if (viewModel.uiState.loadContent) {
        if (viewModel.uiState.lastStep != 1) {
            val stringId = viewModel.getLoadingString()
            LoadingMultiMoney(textRes = stringId, viewModel)
            LaunchedEffect(true) {
                viewModel.queryCreditSteps()
            }
        } else {
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
                        onRightButtonClick = { viewModel.onUIEvent(OnCloseClick(focusManager)) }
                    )
                    if (viewModel.uiState.currentStep > CreditStep.One.id && viewModel.uiState.currentStep < CreditStep.Seven.id) {
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
                            text = stringResource(id = R.string.button_continue),
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
    onNavigate: (NavEvent.Navigate) -> Unit,
    viewModel: CreditViewModel
) {
    when (step) {
        CreditStep.One.id -> CreditAmountScreen(onNavigate = onNavigate, sharedViewModel = viewModel)
        CreditStep.Two.id -> if (viewModel.idBrand.toInt() == Brand.CostaRica.id) {
            IbanAccountScreen(sharedViewModel = viewModel)
        } else {
            CreditBankScreen(sharedViewModel = viewModel)
        }
        CreditStep.Three.id -> MonthlyIncomeScreen(sharedViewModel = viewModel)
        CreditStep.Four.id -> JobInfoScreen(sharedViewModel = viewModel)
        CreditStep.Five.id -> CompanyAddressScreen(sharedViewModel = viewModel)
        CreditStep.Six.id -> HomeAddressScreen(sharedViewModel = viewModel)
        else -> CreditDocumentScreen(sharedViewModel = viewModel)
    }
}
