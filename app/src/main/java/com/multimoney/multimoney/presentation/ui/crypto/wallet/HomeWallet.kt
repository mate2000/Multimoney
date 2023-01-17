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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.balance.BalanceCryptoAccount
import com.multimoney.domain.model.balance.BalanceCryptoAccountItems
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
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
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
        walletViewModel.onUIEvent(OnSetDateRange(FilterDateByDays.YESTERDAY.time))
    }

    val isFocused = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }
    BackHandler {
        if (isFocused.value.not()) walletViewModel.onUIEvent(OnNavigateBack)
        else isFocused.value = isFocused.value.not()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        backgroundColor = MultimoneyTheme.colors.background,
        topBar = {
            AnimatedVisibility(visible = isFocused.value) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        IconButton(onClick = { isFocused.value = isFocused.value.not() }) {
                            Icon(
                                modifier = Modifier.size(32.dp),
                                painter = painterResource(id = R.drawable.ic_close),
                                tint = MultimoneyTheme.colors.textLink,
                                contentDescription = ""
                            )
                        }
                    }
                    CustomOutlinedTextField(
                        modifier = Modifier.padding(
                            top = 8.dp,
                            bottom = 24.dp,
                            start = 16.dp,
                            end = 16.dp
                        ),
                        value = searchQuery.value,
                        isRequired = false,
                        onValueChange = { searchQuery.value = it },
                        keyboardActions = KeyboardActions.Default,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        leadingIconComposable = {
                            Icon(
                                modifier = Modifier.size(16.dp),
                                painter = painterResource(id = R.drawable.ic_search),
                                tint = it,
                                contentDescription = "",
                            )
                        },
                        trailingIcon = R.drawable.ic_close,
                        trailingIconAction = { searchQuery.value = "" },
                        trailingIconActionEnabled = true,
                        placeHolder = stringResource(id = R.string.crypto_wallet_search_crypto_currency),
                    )
                }
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
            searchQuery = searchQuery,
        )
    }

}

@Composable
fun HomeWalletContent(
    modifier: Modifier = Modifier,
    walletViewModel: HomeWalletViewModel = hiltViewModel(),
    isFocused: MutableState<Boolean>,
    searchQuery: MutableState<String>,
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
    var selectedDateRange by remember { mutableStateOf(FilterDateByDays.YESTERDAY.time) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AnimatedVisibility(isFocused.value.not()) {
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
        if (walletViewModel.uiState.areCoinsLoading) {
            WalletSkeleton()
        } else {
            MyCoinsSection(
                walletViewModel.uiState.balanceCryptoAccount,
                isFocused = isFocused,
                searchQuery = searchQuery,
                onItemClick = {
                    walletViewModel.onUIEvent(HomeWalletViewModel.UIEvent.OnNavigateToCryptoDetailScreen(it))
                }
            )
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
            text = stringResource(
                id = R.string.currency_item_dollar_symbol,
                globalCryptoBalance.roundToTwoDecimalPlacesWithoutNegatives()
            ),
            style = Typography.h4.copy(color = MultimoneyTheme.colors.text)
        )
        Text(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
            text = stringResource(
                id = R.string.currency_item_gain_or_losses_description,
                gainsOrLossesSymbol,
                gainsOrLosses.roundToTwoDecimalPlacesWithoutNegatives(),
                percentage.roundToTwoDecimalPlaces()
            ),
            style = Typography.body2.copy(color = graphicColor)
        )
    }
}

@Composable
fun MyCoinsSection(
    balanceCryptoAccount: BalanceCryptoAccount?,
    isFocused: MutableState<Boolean>,
    searchQuery: MutableState<String>,
    onItemClick: (BalanceCryptoAccountItems) -> Unit = {}
) {

    val filteredList = if (searchQuery.value.isNotEmpty()) balanceCryptoAccount?.items?.filter {
        it.asset.contains(searchQuery.value) || it.descriptionCurrency.contains(searchQuery.value)
    } ?: emptyList() else balanceCryptoAccount?.items ?: emptyList()

    Row(
        modifier = Modifier
            .padding(end = 16.dp, start = 16.dp, top = 8.dp)
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
        if (isFocused.value.not()) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { isFocused.value = isFocused.value.not() }) {
                    Text(
                        textAlign = TextAlign.End,
                        text = stringResource(id = R.string.crypto_wallet_show_all_coins),
                        style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                        color = MultimoneyTheme.colors.textLink
                    )
                }
                IconButton(onClick = { isFocused.value = isFocused.value.not() }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = MultimoneyTheme.colors.labelText
                    )
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        filteredList.forEach { item ->
            CurrencyItem(
                imageUrl = item.url_image,
                descriptionCurrency = item.descriptionCurrency,
                asset = item.asset,
                balanceDollars = item.balanceDollars,
                priceOfTheDay = item.priceOfTheDay,
                percentageInvestedCurrency = item.percentageInvestedCurrency,
                available = item.available,
                onClick = {
                    onItemClick(item)
                }
            )
        }
    }
}
