package com.multimoney.multimoney.presentation.ui.crypto.send

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CryptoSendSteps
import com.multimoney.data.util.catalog.SendCryptoStep
import com.multimoney.domain.model.balance.BalanceCryptoAccountItems
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.crypto.send.cryptoaddress.CryptoSendAddressScreen
import com.multimoney.multimoney.presentation.ui.crypto.send.cryptoamount.CryptoSendAmountScreen
import com.multimoney.multimoney.presentation.ui.crypto.send.listofcurrencies.CryptoSendListOfCurrenciesScreen
import com.multimoney.multimoney.presentation.ui.crypto.send.voucher.SendCryptoVoucher
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.MaintenanceAlertScreen
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun CryptoSendFlow(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: CryptoSendSharedViewModel = hiltViewModel(),
    onNavigateToQrCodeScanner: () -> Unit = {},
    qrCodeResult: String,
    balances: List<BalanceCryptoAccountItems> = emptyList()
) {

    LaunchedEffect(true) {
        viewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnGetUserInfo(balances))
        viewModel.executeNavigation(
            onNavigate = onNavigate,
            onPopAndNavigate = onPopAndNavigate,
            onPopBackStack = onPopBackStack
        )
        viewModel.baseEvent.collect { event ->
            when (event) {
                is CryptoSendSharedViewModel.BaseEvent.OnShowMaintenance -> {
                    viewModel.onUIEvent(
                        CryptoSendSharedViewModel.UIEvent.SetPaxosMaintenanceState(true)
                    )
                }
            }
        }
    }

    if (viewModel.uiState.isPaxosInMaintenance) {
        MaintenanceAlertScreen(
            onBackToHomeAction = {
                viewModel.navigateTo(Screen.HomeScreen.route)
            }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MultimoneyTheme.colors.background)
        ) {
            Column {
                TopNavBar(
                    isLeftButtonVisible = viewModel.uiState.currentStepType != SendCryptoStep.LOADING && viewModel.uiState.currentStepType != SendCryptoStep.SEND_VOUCHER && viewModel.uiState.currentStepType != SendCryptoStep.SEND_FAILED && viewModel.uiState.currentStepType != SendCryptoStep.SEND_ERROR,
                    isRightButtonVisible = viewModel.uiState.currentStepType != SendCryptoStep.LOADING && viewModel.uiState.currentStepType != SendCryptoStep.SEND_FAILED && viewModel.uiState.currentStepType != SendCryptoStep.SEND_ERROR,
                    isCenterContentVisible = viewModel.uiState.currentStepType == SendCryptoStep.SEND_VOUCHER,
                    onLeftButtonClick = {
                        viewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnPreviousStep)
                    },
                    onRightButtonClick = {
                        viewModel.onUIEvent(
                            if (viewModel.uiState.currentStepType == SendCryptoStep.SEND_VOUCHER) {
                                CryptoSendSharedViewModel.UIEvent.OnNavigateHome
                            } else {
                                CryptoSendSharedViewModel.UIEvent.OnCloseClick
                            }
                        )
                    }
                )
            }
            Box(
                modifier = Modifier.weight(0.1f),
            ) {
                when (viewModel.idBrand) {
                    Brand.ElSalvador.id -> {
                        /* Sending crypto is temporarily disabled to El Salvador. */
                    }
                    Brand.CostaRica.id -> {
                        if (viewModel.comingFromCurrencyDetails) {
                            CRSendCryptoDirectFlow(
                                step = viewModel.uiState.currentStep,
                                viewModel = viewModel,
                                onNavigateToQrCodeScanner = onNavigateToQrCodeScanner,
                                qrCodeResult = qrCodeResult
                            )
                        } else {
                            CRSendCryptoFlow(
                                step = viewModel.uiState.currentStep,
                                viewModel = viewModel,
                                onNavigateToQrCodeScanner = onNavigateToQrCodeScanner,
                                qrCodeResult = qrCodeResult
                            )
                        }
                    }
                }
            }
        }
    }

    LoadingIndicator(viewModel.uiState.isLoading)
    BackHandler {
        viewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnPreviousStep)
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
            onNegativeAction = viewModel.uiState.openDialog.negativeAction
        )
    }
}

@Composable
fun CRSendCryptoDirectFlow(
    step: Int,
    viewModel: CryptoSendSharedViewModel,
    onNavigateToQrCodeScanner: () -> Unit,
    qrCodeResult: String
) {
    when (step) {
        CryptoSendSteps.One.pageNumber -> {
            viewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnSetFlowStep(SendCryptoStep.CRYPTO_ADDRESS))
            CryptoSendAddressScreen(
                sharedViewModel = viewModel,
                onNavigateToQrCodeScanner = onNavigateToQrCodeScanner,
                qrCodeResult = qrCodeResult
            )
        }
        CryptoSendSteps.Two.pageNumber -> {
            viewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnSetFlowStep(SendCryptoStep.SEND_CRYPTO))
            CryptoSendAmountScreen(
                sharedViewModel = viewModel
            )
        }
        CryptoSendSteps.Three.pageNumber -> {
            viewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnSetFlowStep(SendCryptoStep.SEND_VOUCHER))
            SendCryptoVoucher(sharedViewModel = viewModel)
        }
    }
}

@Composable
fun CRSendCryptoFlow(
    step: Int,
    viewModel: CryptoSendSharedViewModel,
    onNavigateToQrCodeScanner: () -> Unit,
    qrCodeResult: String
) {
    when (step) {
        CryptoSendSteps.One.pageNumber -> {
            viewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnSetFlowStep(SendCryptoStep.LIST_CRYPTO_CURRENCIES))
            CryptoSendListOfCurrenciesScreen(sharedViewModel = viewModel)
        }
        CryptoSendSteps.Two.pageNumber -> {
            viewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnSetFlowStep(SendCryptoStep.CRYPTO_ADDRESS))
            CryptoSendAddressScreen(
                sharedViewModel = viewModel,
                onNavigateToQrCodeScanner = onNavigateToQrCodeScanner,
                qrCodeResult = qrCodeResult
            )
        }
        CryptoSendSteps.Three.pageNumber -> {
            viewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnSetFlowStep(SendCryptoStep.SEND_CRYPTO))
            CryptoSendAmountScreen(
                sharedViewModel = viewModel
            )
        }
        CryptoSendSteps.Four.pageNumber -> {
            viewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnSetFlowStep(SendCryptoStep.SEND_VOUCHER))
            SendCryptoVoucher(sharedViewModel = viewModel)
        }
    }
}
