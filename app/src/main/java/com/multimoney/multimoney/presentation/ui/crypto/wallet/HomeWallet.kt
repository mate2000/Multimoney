package com.multimoney.multimoney.presentation.ui.crypto.wallet

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.graphics.DateFilterDWMYSection
import com.multimoney.multimoney.presentation.ui.crypto.graphics.WalletCryptoGraphic
import com.multimoney.multimoney.presentation.ui.crypto.wallet.HomeWalletViewModel.UIEvent.OnGetUserInfo
import com.multimoney.multimoney.presentation.ui.crypto.wallet.HomeWalletViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.crypto.wallet.HomeWalletViewModel.UIEvent.OnSetDateRange
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoActionsSection
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.FilterDateByDays
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.calculateGainLoses
import com.multimoney.multimoney.presentation.util.calculatePercentage
import com.multimoney.multimoney.presentation.util.roundToTwoDecimalPlaces
import com.multimoney.multimoney.presentation.util.roundToTwoDecimalPlacesWithoutNegatives

@Composable
fun HomeWallet(
    walletViewModel: HomeWalletViewModel = hiltViewModel(),
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
) {

    LaunchedEffect(key1 = true) {
        walletViewModel.executeNavigation(
            onPopBackStack = onPopBackStack,
            onNavigate = onNavigate,
            onPopAndNavigate = onPopAndNavigate
        )
        walletViewModel.onUIEvent(OnSetDateRange(FilterDateByDays.YESTERDAY.days))
        walletViewModel.onUIEvent(OnGetUserInfo)
    }

    BackHandler { walletViewModel.onUIEvent(OnNavigateBack) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = MultimoneyTheme.colors.background,
        bottomBar = {
            val enableSendAndGive = walletViewModel.uiState.idBrand == Brand.CostaRica.id

            CryptoActionsSection(
                hasSmartBalance = true,
                enableCryptoActions = true,
                enableSendAndGive = enableSendAndGive,
                hasBalanceAction = { /*todo go to buy crypto flow*/ },
                sellAction = { /*todo go to sell crypto flow*/ },
                sendAction = { /*todo go to send crypto flow*/ },
                giveAction = { /*todo go to receive crypto flow*/ }
            )
        }
    ) { paddingValues ->
        HomeWalletContent(
            modifier = Modifier.padding(paddingValues)
        )
    }

}

@Composable
fun HomeWalletContent(
    modifier: Modifier = Modifier,
    walletViewModel: HomeWalletViewModel = hiltViewModel(),
) {

    val globalCryptoBalance = walletViewModel.uiState.globalCryptoBalance?.toDouble() ?: 0.0

    val gainsOrLosses = calculateGainLoses(
        globalCryptoBalance,
        walletViewModel.uiState.clientCryptoBalanceHistory
    )
    val percentage = calculatePercentage(
        globalCryptoBalance,
        walletViewModel.uiState.clientCryptoBalanceHistory
    )
    val isInGainOrLoss = gainsOrLosses >= 0
    val graphicColor = if (isInGainOrLoss)
        MultimoneyTheme.colors.cryptoWalletGainsColor else MultimoneyTheme.colors.cryptoLossesColor
    var selectedDateRange by remember { mutableStateOf(FilterDateByDays.YESTERDAY.days) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopNavBar(
            isRightButtonVisible = false,
            onLeftButtonClick = { walletViewModel.onUIEvent(OnNavigateBack) },
        )
        WalletHeader()
        BalanceSection(
            globalCryptoBalance = globalCryptoBalance,
            isInGainOrLoss = isInGainOrLoss,
            gainsOrLosses = gainsOrLosses,
            percentage = percentage,
            graphicColor = graphicColor,
        )
        WalletCryptoGraphic(
            clientCryptoBalanceHistory = walletViewModel.uiState.clientCryptoBalanceHistory,
            graphicColor = graphicColor,
        )
        DateFilterDWMYSection(
            selectedDateFilter = selectedDateRange,
            onDateFilterSelected = {
                selectedDateRange = it
                walletViewModel.onUIEvent(OnSetDateRange(it))
            }
        )
    }
}

@Composable
fun WalletHeader() {

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {

        Text(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            text = stringResource(R.string.crypto_wallet_header_title),
            style = Typography.h5.copy(color = MultimoneyTheme.colors.text)
        )
    }
}

@Composable
fun BalanceSection(
    globalCryptoBalance: Double,
    isInGainOrLoss: Boolean,
    graphicColor: Color,
    gainsOrLosses: Double,
    percentage: Double,
) {
    val gainsOrLossesSymbol =
        if (isInGainOrLoss) stringResource(R.string.crypto_gains_symbol) else stringResource(R.string.crypto_losses_symbol)

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
            text = stringResource(R.string.crypto_wallet_balance_section_label),
            style = Typography.subtitle1.copy(color = MultimoneyTheme.colors.quickActionLabelColor)
        )
        Text(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
            text = "\$${globalCryptoBalance.roundToTwoDecimalPlacesWithoutNegatives()}",
            style = Typography.h4.copy(color = MultimoneyTheme.colors.text)
        )
        Text(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
            text = "${gainsOrLossesSymbol}\$${gainsOrLosses.roundToTwoDecimalPlacesWithoutNegatives()} (${percentage.roundToTwoDecimalPlaces()}%)",
            style = Typography.body2.copy(color = graphicColor)
        )
    }
}
