package com.multimoney.multimoney.presentation.ui.crypto.sell

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.SellCryptoStep
import com.multimoney.data.util.catalog.SellCryptoSteps
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.crypto.sell.listofcurrencies.SellCurrenciesListScreen
import com.multimoney.multimoney.presentation.ui.crypto.sell.selectaccount.SelectSmartAccountScreen
import com.multimoney.multimoney.presentation.ui.crypto.sell.sellcurrency.SellCurrencyScreen
import com.multimoney.multimoney.presentation.ui.crypto.sell.voucher.SellCryptoVoucherScreen
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SellCryptoFlow(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SellCryptoSharedViewModel = hiltViewModel()
) {

    LaunchedEffect(true) {
        viewModel.onUIEvent(SellCryptoSharedViewModel.UIEvent.OnGetUserInfo)
        viewModel.executeNavigation(
            onNavigate = onNavigate,
            onPopAndNavigate = onPopAndNavigate,
            onPopBackStack = onPopBackStack
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
    ) {
        Column {
            TopNavBar(
                isLeftButtonVisible = viewModel.uiState.currentStepType != SellCryptoStep.LOADING_SCREEN && viewModel.uiState.currentStepType != SellCryptoStep.SELL_VOUCHER && viewModel.uiState.currentStepType != SellCryptoStep.PURCHASE_FAILED,
                isRightButtonVisible = viewModel.uiState.currentStepType != SellCryptoStep.LOADING_SCREEN && viewModel.uiState.currentStepType != SellCryptoStep.SELECT_SMART_ACCOUNT && viewModel.uiState.currentStepType != SellCryptoStep.PURCHASE_FAILED,
                onRightButtonClick = {
                    if (viewModel.uiState.currentStepType == SellCryptoStep.SELL_VOUCHER) {
                        viewModel.onUIEvent(SellCryptoSharedViewModel.UIEvent.OnNavigateHome)
                    } else {
                        viewModel.onUIEvent(SellCryptoSharedViewModel.UIEvent.OnCloseClick)
                    }
                },
                onLeftButtonClick = {
                    viewModel.onUIEvent(SellCryptoSharedViewModel.UIEvent.OnPreviousStep)
                },
                isCenterContentVisible = viewModel.uiState.currentStepType == SellCryptoStep.SELL_VOUCHER
            )
        }
        Column(
            modifier = Modifier.weight(0.1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            when (viewModel.idBrand) {
                Brand.ElSalvador.id -> {
                    if (viewModel.comingFromDetails) {
                        SvSellCryptoDirectFlow(
                            step = viewModel.uiState.currentStep,
                            viewModel = viewModel
                        )
                    } else {
                        SvSellCryptoFlow(
                            step = viewModel.uiState.currentStep,
                            viewModel = viewModel
                        )
                    }
                }
                Brand.CostaRica.id -> {
                    if (viewModel.comingFromDetails) {
                        CRSellCryptoDirectFlow(
                            step = viewModel.uiState.currentStep,
                            viewModel = viewModel
                        )
                    } else {
                        CRSellCryptoFlow(
                            step = viewModel.uiState.currentStep,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }

    LoadingIndicator(viewModel.uiState.isLoading)
    BackHandler {
        if (viewModel.uiState.currentStepType == SellCryptoStep.SELL_VOUCHER) {
            viewModel.onUIEvent(SellCryptoSharedViewModel.UIEvent.OnNavigateHome)
        } else {
            viewModel.onUIEvent(SellCryptoSharedViewModel.UIEvent.OnCloseClick)
        }
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
}

@Composable
fun SvSellCryptoDirectFlow(step: Int, viewModel: SellCryptoSharedViewModel) {
    when (step) {
        SellCryptoSteps.One.pageNumber -> {
            viewModel.onUIEvent(
                SellCryptoSharedViewModel.UIEvent.OnSetFlowStep(SellCryptoStep.SELL_CRYPTO)
            )
            SellCurrencyScreen(sharedViewModel = viewModel)
        }
        SellCryptoSteps.Two.pageNumber -> {
            viewModel.onUIEvent(
                SellCryptoSharedViewModel.UIEvent.OnSetFlowStep(SellCryptoStep.SELL_VOUCHER)
            )
            SellCryptoVoucherScreen(sharedViewModel = viewModel)
        }
    }
}

@Composable
fun CRSellCryptoDirectFlow(step: Int, viewModel: SellCryptoSharedViewModel) {
    when (step) {
        SellCryptoSteps.One.pageNumber -> {
            viewModel.onUIEvent(
                SellCryptoSharedViewModel.UIEvent.OnSetFlowStep(SellCryptoStep.SELECT_SMART_ACCOUNT)
            )
            SelectSmartAccountScreen(sharedViewModel = viewModel)
        }
        SellCryptoSteps.Two.pageNumber -> {
            viewModel.onUIEvent(
                SellCryptoSharedViewModel.UIEvent.OnSetFlowStep(SellCryptoStep.SELL_CRYPTO)
            )
            SellCurrencyScreen(sharedViewModel = viewModel)
        }
        SellCryptoSteps.Three.pageNumber -> {
            viewModel.onUIEvent(
                SellCryptoSharedViewModel.UIEvent.OnSetFlowStep(SellCryptoStep.SELL_VOUCHER)
            )
            SellCryptoVoucherScreen(sharedViewModel = viewModel)
        }
    }
}

@Composable
fun SvSellCryptoFlow(step: Int, viewModel: SellCryptoSharedViewModel) {
    when (step) {
        SellCryptoSteps.One.pageNumber -> {
            viewModel.onUIEvent(
                SellCryptoSharedViewModel.UIEvent.OnSetFlowStep(SellCryptoStep.LIST_CRYPTO_CURRENCIES)
            )
            SellCurrenciesListScreen(sharedViewModel = viewModel)
        }
        SellCryptoSteps.Two.pageNumber -> {
            viewModel.onUIEvent(
                SellCryptoSharedViewModel.UIEvent.OnSetFlowStep(SellCryptoStep.SELL_CRYPTO)
            )
            SellCurrencyScreen(sharedViewModel = viewModel)
        }
        SellCryptoSteps.Three.pageNumber -> {
            viewModel.onUIEvent(
                SellCryptoSharedViewModel.UIEvent.OnSetFlowStep(SellCryptoStep.SELL_VOUCHER)
            )
            SellCryptoVoucherScreen(sharedViewModel = viewModel)
        }
    }
}

@Composable
fun CRSellCryptoFlow(step: Int, viewModel: SellCryptoSharedViewModel) {
    when (step) {
        SellCryptoSteps.One.pageNumber -> {
            viewModel.onUIEvent(
                SellCryptoSharedViewModel.UIEvent.OnSetFlowStep(SellCryptoStep.LIST_CRYPTO_CURRENCIES)
            )
            SellCurrenciesListScreen(sharedViewModel = viewModel)
        }
        SellCryptoSteps.Two.pageNumber -> {
            viewModel.onUIEvent(
                SellCryptoSharedViewModel.UIEvent.OnSetFlowStep(SellCryptoStep.SELECT_SMART_ACCOUNT)
            )
            SelectSmartAccountScreen(sharedViewModel = viewModel)
        }
        SellCryptoSteps.Three.pageNumber -> {
            viewModel.onUIEvent(
                SellCryptoSharedViewModel.UIEvent.OnSetFlowStep(SellCryptoStep.SELL_CRYPTO)
            )
            SellCurrencyScreen(sharedViewModel = viewModel)
        }
        SellCryptoSteps.Four.pageNumber -> {
            viewModel.onUIEvent(
                SellCryptoSharedViewModel.UIEvent.OnSetFlowStep(SellCryptoStep.SELL_VOUCHER)
            )
            SellCryptoVoucherScreen(sharedViewModel = viewModel)
        }
    }
}