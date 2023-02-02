package com.multimoney.multimoney.presentation.ui.crypto.currencydetail

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.LocalMultimoneyColors
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.CryptoCurrencyMovementItem
import com.multimoney.multimoney.presentation.ui.crypto.currencydetail.CryptoCurrencyMovementsViewModel.Companion.TODAY_TEXT
import com.multimoney.multimoney.presentation.ui.crypto.graphics.DateFilterDWMYSection
import com.multimoney.multimoney.presentation.ui.crypto.graphics.MarketCurrencyDetailsGraphic
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoActionsSection
import com.multimoney.multimoney.presentation.uielement.BalanceTextView
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.FilterDateByDays
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.addTextStyleToTextPortion
import com.multimoney.multimoney.presentation.util.toCurrencyFormat

@Composable
fun CurrencyMovementsScreen(
    viewModel: CryptoCurrencyMovementsViewModel = hiltViewModel(),
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack, onNavigate = onNavigate, onPopAndNavigate = onPopAndNavigate)

        viewModel.onUIEvent(CryptoCurrencyMovementsViewModel.UIEvent.OnSetDateRange(FilterDateByDays.YESTERDAY.time))
        viewModel.onUIEvent(CryptoCurrencyMovementsViewModel.UIEvent.OnGetUserInfo)
        viewModel.onUIEvent(CryptoCurrencyMovementsViewModel.UIEvent.OnGetAssetHistory)
        viewModel.onUIEvent(CryptoCurrencyMovementsViewModel.UIEvent.OnGetMovements)
    }

    BackHandler { viewModel.onUIEvent(CryptoCurrencyMovementsViewModel.UIEvent.OnNavigateBack) }

    CurrencyDetailContent(
        uiState = viewModel.uiState,
        backPressed = { viewModel.onUIEvent(CryptoCurrencyMovementsViewModel.UIEvent.OnNavigateBack) },
        onDateChanged = { dateSelected ->
            viewModel.onUIEvent(
                CryptoCurrencyMovementsViewModel.UIEvent.OnSetDateRange(
                    dateSelected
                )
            )
        },
        viewAllClick = { viewModel.onUIEvent(CryptoCurrencyMovementsViewModel.UIEvent.OnViewAllMovements) }
    )
}

@Composable
fun CurrencyDetailContent(
    uiState: CryptoCurrencyMovementsViewModel.UiState,
    backPressed: () -> Unit,
    onDateChanged: (Long) -> Unit,
    viewAllClick: () -> Unit
) {

    val movements = uiState.cryptoMovements.collectAsLazyPagingItems()

    var selectedDateRange by remember { mutableStateOf(FilterDateByDays.YESTERDAY.time) }
    val graphicColor =
        if (uiState.cryptoItem?.investedBalanceCurrency?.contains("+") == true)
            MultimoneyTheme.colors.cryptoWalletGainsColor else MultimoneyTheme.colors.cryptoLossesColor
    val gainOrLossColor = if (uiState.cryptoItem?.investedBalanceCurrency?.contains('-') == true) MultimoneyTheme.colors.cryptoLossesColor
        else MultimoneyTheme.colors.cryptoGainsColor
    Scaffold(
        topBar = {
            TopNavBar(
                isRightButtonVisible = false,
                onLeftButtonClick = backPressed
            )
        },
        modifier = Modifier.fillMaxSize(),
        backgroundColor = MultimoneyTheme.colors.background,
        bottomBar = {
            val enableSendAndGive = uiState.idBrand == Brand.CostaRica.id

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
    ) {
        Box(modifier = Modifier.padding(it)) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = rememberAsyncImagePainter(uiState.cryptoItem?.url_image),
                        contentDescription = ""
                    )
                    Text(
                        modifier = Modifier.padding(start = 12.dp),
                        text = stringResource(
                            id = R.string.currency_detail_title,
                            uiState.cryptoItem?.descriptionCurrency ?: "",
                        ),
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MultimoneyTheme.colors.text
                        )
                    )
                }
                BalanceTextView(
                    balanceText = uiState.cryptoItem?.balanceDollars?.toCurrencyFormat(useCurrentCurrency = false) ?: "0.0",
                    currencyStyle = Typography.h4.copy(
                        color = MultimoneyTheme.colors.text,
                        fontWeight = FontWeight.Bold
                    ),
                    currencyDecimalStyle = Typography.body2.copy(
                        color = MultimoneyTheme.colors.text,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = "${uiState.cryptoItem?.available} ${uiState.cryptoItem?.asset}",
                    style = Typography.body2,
                    color = LocalMultimoneyColors.current.labelText
                )
                Text(
                    text = stringResource(
                        id = R.string.currency_detail_daily_invest,
                        uiState.cryptoItem?.investedBalanceCurrency ?: "",
                        uiState.cryptoItem?.percentageInvestedCurrency ?: ""
                    ).addTextStyleToTextPortion(
                        textToStyle = TODAY_TEXT,
                        style = Typography.body2.copy(color = LocalMultimoneyColors.current.subTitleText)
                    ),
                    style = Typography.body2,
                    color = gainOrLossColor
                )
                MarketCurrencyDetailsGraphic(
                    currencyHistory = uiState.historicalBalance,
                    graphicColor = graphicColor
                )
                DateFilterDWMYSection(
                    selectedDateFilter = selectedDateRange,
                    onDateFilterSelected = { dateSelected ->
                        selectedDateRange = dateSelected
                        onDateChanged(selectedDateRange)
                    }
                )
                Row(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        textAlign = TextAlign.Start,
                        text = stringResource(id = R.string.currency_detail_my_movements),
                        style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                        color = MultimoneyTheme.colors.labelText
                    )
                    TextButton(onClick = {
                        viewAllClick()
                    }) {
                        Text(
                            textAlign = TextAlign.End,
                            text = stringResource(id = R.string.currency_detail_see_all),
                            style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                            color = MultimoneyTheme.colors.textLink
                        )
                    }
                }
                Column(modifier = Modifier.fillMaxWidth()) {
                    movements.itemSnapshotList.items.take(3).forEach {
                        CryptoCurrencyMovementItem(cryptoCurrencyMovement = it)
                    }
                }
            }
        }
    }
}