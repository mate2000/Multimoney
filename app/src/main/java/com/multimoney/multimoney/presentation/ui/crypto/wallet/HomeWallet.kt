package com.multimoney.multimoney.presentation.ui.crypto.wallet

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.balance.BalanceCryptoAccount
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.CurrencyItem
import com.multimoney.multimoney.presentation.ui.crypto.graphics.DateFilterDWMYSection
import com.multimoney.multimoney.presentation.ui.crypto.graphics.WalletCryptoGraphic
import com.multimoney.multimoney.presentation.ui.crypto.wallet.HomeWalletViewModel.UIEvent.OnGetBalanceClient
import com.multimoney.multimoney.presentation.ui.crypto.wallet.HomeWalletViewModel.UIEvent.OnGetUserInfo
import com.multimoney.multimoney.presentation.ui.crypto.wallet.HomeWalletViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.crypto.wallet.HomeWalletViewModel.UIEvent.OnSetDateRange
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoActionsSection
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.FilterDateByDays
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.calculateGainLoses
import com.multimoney.multimoney.presentation.util.calculatePercentage
import com.multimoney.multimoney.presentation.util.roundToTwoDecimalPlaces
import com.multimoney.multimoney.presentation.util.roundToTwoDecimalPlacesWithoutNegatives

@OptIn(ExperimentalLayoutApi::class)
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

        walletViewModel.onUIEvent(OnGetUserInfo)
        walletViewModel.onUIEvent(OnGetBalanceClient)
        walletViewModel.onUIEvent(OnSetDateRange(FilterDateByDays.YESTERDAY.days))
    }

    BackHandler { walletViewModel.onUIEvent(OnNavigateBack) }
    val isFocused = remember { mutableStateOf(false) }
    /*ConstraintLayout(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        val enableSendAndGive = walletViewModel.uiState.idBrand == Brand.CostaRica.id
        val (actionsButtons, content) = createRefs()

        CryptoActionsSection(
            modifier = Modifier.constrainAs(actionsButtons) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            hasSmartBalance = true,
            enableCryptoActions = true,
            enableSendAndGive = enableSendAndGive,
            hasBalanceAction = { /*todo go to buy crypto flow*/ },
            sellAction = { /*todo go to sell crypto flow*/ },
            sendAction = { /*todo go to send crypto flow*/ },
            giveAction = { /*todo go to receive crypto flow*/ }
        )
        HomeWalletContent(
            modifier = Modifier.constrainAs(content) {
                top.linkTo(parent.top)
                bottom.linkTo(actionsButtons.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
        )
    }*/
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        backgroundColor = MultimoneyTheme.colors.background,
        topBar = {
            AnimatedVisibility(visible = isFocused.value) {
                CustomOutlinedTextField(
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
                    keyboardActions = KeyboardActions(onSearch = { isFocused.value = isFocused.value.not() }),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    leadingIconComposable = { Icon(Icons.Filled.Search, contentDescription = null) },
                    placeHolder = "Buscar criptomoneda",
                )
            }
        },
        bottomBar = {
            val enableSendAndGive = walletViewModel.uiState.idBrand == Brand.CostaRica.id

            AnimatedVisibility(visible = isFocused.value.not()) {
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
        }
    ) { paddingValues ->
        HomeWalletContent(
            modifier = Modifier
                .consumedWindowInsets(paddingValues)
                .padding(paddingValues)
                .imePadding(),
            isFocused = isFocused,
        )
    }

}

@Composable
fun HomeWalletContent(
    modifier: Modifier = Modifier,
    walletViewModel: HomeWalletViewModel = hiltViewModel(),
    isFocused: MutableState<Boolean>,
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

        androidx.compose.animation.AnimatedVisibility(isFocused.value.not()) {
            Column {
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
        walletViewModel.uiState.areCoinsLoading.let { isLoading ->
            if (isLoading) {
                WalletSkeleton()
            } else {
                MyCoinsSection(
                    walletViewModel.uiState.balanceCryptoAccount,
                    isFocused = isFocused,
                )
            }
        }
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

@Composable
fun MyCoinsSection(balanceCryptoAccount: BalanceCryptoAccount?, isFocused: MutableState<Boolean>) {

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                textAlign = TextAlign.Start,
                text = stringResource(id = R.string.crypto_currencies),
                style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText
            )
            IconButton(onClick = { isFocused.value = isFocused.value.not() }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = MultimoneyTheme.colors.labelText
                )
            }
        }

        balanceCryptoAccount?.items?.let {
            it.forEach { item ->
                CurrencyItem(item = item)
            }
        }
    }
}
