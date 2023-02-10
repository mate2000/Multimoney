package com.multimoney.multimoney.presentation.ui.crypto.purchase

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.BuyCryptoStep
import com.multimoney.data.util.catalog.PurchaseCryptoSteps
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency.BuyCurrencyScreen
import com.multimoney.multimoney.presentation.ui.crypto.purchase.listofcurrency.ListCryptoCurrenciesScreen
import com.multimoney.multimoney.presentation.ui.crypto.purchase.selectaccount.ConfirmationBottomSheet
import com.multimoney.multimoney.presentation.ui.crypto.purchase.selectaccount.SelectSmartAccountScreen
import com.multimoney.multimoney.presentation.ui.crypto.purchase.voucher.BuyCryptoVoucherScreen
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PurchaseCryptoFlow(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: PurchaseCryptoSharedViewModel = hiltViewModel()
) {
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnGetUserInfo)
        viewModel.executeNavigation(
            onNavigate = onNavigate,
            onPopAndNavigate = onPopAndNavigate,
            onPopBackStack = onPopBackStack
        )

        viewModel.baseEvent.collect { event ->
            when (event) {
                is PurchaseCryptoSharedViewModel.BaseEvent.OnShowDisclaimer -> {
                    bottomSheetState.show()
                }
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
                isLeftButtonVisible = viewModel.uiState.currentStepType != BuyCryptoStep.LOADING_SCREEN && viewModel.uiState.currentStepType != BuyCryptoStep.PURCHASE_VOUCHER,
                isRightButtonVisible = viewModel.uiState.currentStepType != BuyCryptoStep.LOADING_SCREEN && viewModel.uiState.currentStepType != BuyCryptoStep.SELECT_SMART_ACCOUNT,
                onRightButtonClick = {
                    if (viewModel.uiState.currentStepType == BuyCryptoStep.PURCHASE_VOUCHER) {
                        viewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnNavigateHome)
                    } else {
                        viewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnCloseClick)
                    }
                },
                onLeftButtonClick = {
                    viewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnPreviousStep)
                },
                isCenterContentVisible = viewModel.uiState.currentStepType == BuyCryptoStep.PURCHASE_VOUCHER
            )
        }
        Column(
            modifier = Modifier.weight(0.1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            when (viewModel.idBrand) {
                Brand.ElSalvador.id -> {
                    if (viewModel.comingFromDetails) {
                        SvPurchaseCryptoDirectFlow(
                            step = viewModel.uiState.currentStep,
                            viewModel = viewModel
                        )
                    } else {
                        SvPurchaseCryptoFlow(
                            step = viewModel.uiState.currentStep,
                            viewModel = viewModel
                        )
                    }
                }
                Brand.CostaRica.id -> {
                    if (viewModel.comingFromDetails) {
                        CRPurchaseCryptoDirectFlow(
                            step = viewModel.uiState.currentStep,
                            viewModel = viewModel
                        )
                    } else {
                        CRPurchaseCryptoFlow(
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
        viewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnCloseClick)
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
    ConfirmationBottomSheet(
        modalBottomSheetState = bottomSheetState,
        coroutineScope = coroutineScope,
        onCheckedChange = {
            viewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnDisclaimerChecked(it))
        },
        onContinueClicked = {
            viewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnNextStep)
            viewModel.onUIEvent(
                PurchaseCryptoSharedViewModel.UIEvent.OnUpdateShouldShowDisclaimer(
                    viewModel.uiState.dontShowAgainChecked
                )
            )
            coroutineScope.launch {
                bottomSheetState.hide()
            }
        },
        checked = viewModel.uiState.dontShowAgainChecked
    )
}

@Composable
fun SvPurchaseCryptoDirectFlow(step: Int, viewModel: PurchaseCryptoSharedViewModel) {
    when (step) {
        PurchaseCryptoSteps.One.pageNumber -> {
            viewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnSetFlowStep(BuyCryptoStep.BUY_CURRENCY))
            BuyCurrencyScreen(sharedViewModel = viewModel)
        }
        PurchaseCryptoSteps.Two.pageNumber -> {
            viewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnSetFlowStep(BuyCryptoStep.PURCHASE_VOUCHER))
            BuyCryptoVoucherScreen(sharedViewModel = viewModel)
        }
    }
}

@Composable
fun CRPurchaseCryptoDirectFlow(step: Int, viewModel: PurchaseCryptoSharedViewModel) {
    when (step) {
        PurchaseCryptoSteps.One.pageNumber -> {
            viewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnSetFlowStep(BuyCryptoStep.SELECT_SMART_ACCOUNT))
            SelectSmartAccountScreen(sharedViewModel = viewModel)
        }
        PurchaseCryptoSteps.Two.pageNumber -> {
            viewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnSetFlowStep(BuyCryptoStep.BUY_CURRENCY))
            BuyCurrencyScreen(sharedViewModel = viewModel)
        }
        PurchaseCryptoSteps.Three.pageNumber -> {
            viewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnSetFlowStep(BuyCryptoStep.PURCHASE_VOUCHER))
            BuyCryptoVoucherScreen(sharedViewModel = viewModel)
        }
    }
}

@Composable
fun SvPurchaseCryptoFlow(step: Int, viewModel: PurchaseCryptoSharedViewModel) {
    when (step) {
        PurchaseCryptoSteps.One.pageNumber -> {
            viewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnSetFlowStep(BuyCryptoStep.LIST_CRYPTO_CURRENCIES))
            ListCryptoCurrenciesScreen(sharedViewModel = viewModel)
        }
        PurchaseCryptoSteps.Two.pageNumber -> {
            viewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnSetFlowStep(BuyCryptoStep.BUY_CURRENCY))
            BuyCurrencyScreen(sharedViewModel = viewModel)
        }
        PurchaseCryptoSteps.Three.pageNumber -> {
            viewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnSetFlowStep(BuyCryptoStep.PURCHASE_VOUCHER))
            BuyCryptoVoucherScreen(sharedViewModel = viewModel)
        }
    }
}

@Composable
fun CRPurchaseCryptoFlow(step: Int, viewModel: PurchaseCryptoSharedViewModel) {
    when (step) {
        PurchaseCryptoSteps.One.pageNumber -> {
            viewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnSetFlowStep(BuyCryptoStep.LIST_CRYPTO_CURRENCIES))
            ListCryptoCurrenciesScreen(sharedViewModel = viewModel)
        }
        PurchaseCryptoSteps.Two.pageNumber -> {
            viewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnSetFlowStep(BuyCryptoStep.SELECT_SMART_ACCOUNT))
            SelectSmartAccountScreen(sharedViewModel = viewModel)
        }
        PurchaseCryptoSteps.Three.pageNumber -> {
            viewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnSetFlowStep(BuyCryptoStep.BUY_CURRENCY))
            BuyCurrencyScreen(sharedViewModel = viewModel)
        }
        PurchaseCryptoSteps.Four.pageNumber -> {
            viewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnSetFlowStep(BuyCryptoStep.PURCHASE_VOUCHER))
            BuyCryptoVoucherScreen(sharedViewModel = viewModel)
        }
    }
}