package com.multimoney.multimoney.presentation.ui.crypto.purchase

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.PurchaseCryptoSteps
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency.BuyCurrencyScreen
import com.multimoney.multimoney.presentation.ui.crypto.purchase.listofcurrency.ListCryptoCurrenciesScreen
import com.multimoney.multimoney.presentation.ui.crypto.purchase.selectaccount.SelectSmartAccountScreen
import com.multimoney.multimoney.presentation.ui.crypto.purchase.voucher.BuyCryptoVoucherScreen
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun PurchaseCryptoFlow(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: PurchaseCryptoSharedViewModel = hiltViewModel()
) {
    val comingFromCryptoDetails = false

    LaunchedEffect(true) {
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
            TopNavBar()
        }
        Column(
            modifier = Modifier.weight(0.1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            when (viewModel.idBrand) {
                Brand.ElSalvador.id -> {
                    if (comingFromCryptoDetails) {
                        SvPurchaseCryptoDirectFlow(step = viewModel.uiState.currentStep, viewModel = viewModel)
                    }
                    SvPurchaseCryptoFlow(step = viewModel.uiState.currentStep, viewModel = viewModel)
                }
                Brand.CostaRica.id -> {
                    if (comingFromCryptoDetails) {
                        CRPurchaseCryptoDirectFlow(step = viewModel.uiState.currentStep, viewModel = viewModel)
                    }
                    CRPurchaseCryptoFlow(step = viewModel.uiState.currentStep, viewModel = viewModel)
                }
            }
        }
    }

    LoadingIndicator(viewModel.uiState.isLoading)
    BackHandler {
        // exit from buy crypto flow
    }
}

@Composable
fun SvPurchaseCryptoDirectFlow(step: Int, viewModel: PurchaseCryptoSharedViewModel) {
    when (step) {
        PurchaseCryptoSteps.One.id -> BuyCurrencyScreen(sharedViewModel = viewModel)
        PurchaseCryptoSteps.Two.id -> BuyCryptoVoucherScreen(sharedViewModel = viewModel)
    }
}

@Composable
fun CRPurchaseCryptoDirectFlow(step: Int, viewModel: PurchaseCryptoSharedViewModel) {
    when (step) {
        PurchaseCryptoSteps.One.id -> SelectSmartAccountScreen(sharedViewModel = viewModel)
        PurchaseCryptoSteps.Two.id -> BuyCurrencyScreen(sharedViewModel = viewModel)
        PurchaseCryptoSteps.Three.id -> BuyCryptoVoucherScreen(sharedViewModel = viewModel)
    }
}

@Composable
fun SvPurchaseCryptoFlow(step: Int, viewModel: PurchaseCryptoSharedViewModel) {
    when (step) {
        PurchaseCryptoSteps.One.id -> ListCryptoCurrenciesScreen(sharedViewModel = viewModel)
        PurchaseCryptoSteps.Two.id -> BuyCurrencyScreen(sharedViewModel = viewModel)
        PurchaseCryptoSteps.Three.id -> BuyCryptoVoucherScreen(sharedViewModel = viewModel)
    }
}

@Composable
fun CRPurchaseCryptoFlow(step: Int, viewModel: PurchaseCryptoSharedViewModel) {
    when (step) {
        PurchaseCryptoSteps.One.id -> ListCryptoCurrenciesScreen(sharedViewModel = viewModel)
        PurchaseCryptoSteps.Two.id -> SelectSmartAccountScreen(sharedViewModel = viewModel)
        PurchaseCryptoSteps.Three.id -> BuyCurrencyScreen(sharedViewModel = viewModel)
        PurchaseCryptoSteps.Four.id -> BuyCryptoVoucherScreen(sharedViewModel = viewModel)
    }
}