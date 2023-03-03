package com.multimoney.multimoney.presentation.ui.crypto.receive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CryptoReceiveSteps
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.crypto.receive.cryptoaddress.CryptoReceiveAddressScreen
import com.multimoney.multimoney.presentation.ui.crypto.receive.listofcurrencies.CryptoReceiveCurrenciesListScreen
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.MaintenanceAlertScreen
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun CryptoReceiveFlowScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: CryptoReceiveSharedViewModel = hiltViewModel(),
) {
    LaunchedEffect(true) {
        viewModel.onUIEvent(CryptoReceiveSharedViewModel.UIEvent.OnGetUserInfo)
        viewModel.executeNavigation(
            onNavigate = onNavigate,
            onPopAndNavigate = onPopAndNavigate,
            onPopBackStack = onPopBackStack
        )
        viewModel.baseEvent.collect { event ->
            when (event) {
                is CryptoReceiveSharedViewModel.BaseEvent.OnShowMaintenance -> {
                    viewModel.onUIEvent(
                        CryptoReceiveSharedViewModel.UIEvent.SetPaxosMaintenanceState(true)
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
                    isLeftButtonVisible = true,
                    isRightButtonVisible = false,
                    onLeftButtonClick = {
                        viewModel.onUIEvent(CryptoReceiveSharedViewModel.UIEvent.OnPreviousStep)
                    },
                )
            }
            Column(
                modifier = Modifier.weight(0.1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                when (viewModel.idBrand) {
                    Brand.ElSalvador.id -> {
                        /* Receive crypto is temporarily disabled to El Salvador. */
                    }
                    Brand.CostaRica.id -> {
                        if (viewModel.comingFromDetails) {
                            CRReceiveCryptoDirectFlow(
                                step = viewModel.uiState.currentStep,
                                viewModel = viewModel
                            )
                        } else {
                            CRReceiveCryptoFlow(
                                step = viewModel.uiState.currentStep,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CRReceiveCryptoDirectFlow(
    step: Int,
    viewModel: CryptoReceiveSharedViewModel
) {
    when (step) {
        CryptoReceiveSteps.One.pageNumber -> {
            CryptoReceiveAddressScreen(sharedViewModel = viewModel)
        }
    }
}
@Composable
fun CRReceiveCryptoFlow(
    step: Int,
    viewModel: CryptoReceiveSharedViewModel
) {
    when (step) {
        CryptoReceiveSteps.One.pageNumber -> CryptoReceiveCurrenciesListScreen(sharedViewModel = viewModel)
        CryptoReceiveSteps.Two.pageNumber -> CryptoReceiveAddressScreen(sharedViewModel = viewModel)
    }
}