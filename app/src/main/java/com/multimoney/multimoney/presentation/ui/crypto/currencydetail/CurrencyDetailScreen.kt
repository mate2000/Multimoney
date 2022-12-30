package com.multimoney.multimoney.presentation.ui.crypto.currencydetail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import coil.compose.rememberAsyncImagePainter
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency60
import com.multimoney.multimoney.presentation.ui.crypto.graphics.DateFilterDWMYSection
import com.multimoney.multimoney.presentation.ui.crypto.graphics.WalletCryptoGraphic
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.FilterDateByDays
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun CurrencyDetailScreen(
    viewModel: CryptoCurrencyDetailViewModel = hiltViewModel(),
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {}
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(
            onPopBackStack = onPopBackStack,
            onNavigate = onNavigate,
            onPopAndNavigate = onPopAndNavigate
        )
        viewModel.onUIEvent(CryptoCurrencyDetailViewModel.UIEvent.OnSetDateRange(FilterDateByDays.YESTERDAY.days))
        viewModel.onUIEvent(CryptoCurrencyDetailViewModel.UIEvent.OnGetUserInfo)
        viewModel.onUIEvent(CryptoCurrencyDetailViewModel.UIEvent.OnGetAssetHistory)
        viewModel.onUIEvent(CryptoCurrencyDetailViewModel.UIEvent.OnGetMovements)
    }

    BackHandler { viewModel.onUIEvent(CryptoCurrencyDetailViewModel.UIEvent.OnNavigateBack) }

    var selectedDateRange by remember { mutableStateOf(FilterDateByDays.YESTERDAY.days) }
    val graphicColor =
        if (viewModel.uiState.balanceItem?.investedBalanceCurrency?.contains("+") == true)
            MultimoneyTheme.colors.cryptoWalletGainsColor else MultimoneyTheme.colors.cryptoLossesColor

    Scaffold(
        topBar = {
            TopNavBar(
                isRightButtonVisible = false,
                onLeftButtonClick = {
                    viewModel.onUIEvent(CryptoCurrencyDetailViewModel.UIEvent.OnNavigateBack)
                },
            )
        },
        modifier = Modifier.fillMaxSize(),
        backgroundColor = MultimoneyTheme.colors.background,
    ) {
        Box(modifier = Modifier.padding(it)) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = rememberAsyncImagePainter(viewModel.uiState.balanceItem?.url_image),
                        contentDescription = ""
                    )
                    Text(
                        modifier = Modifier.padding(start = 12.dp),
                        text = stringResource(
                            id = R.string.currency_detail_title,
                            viewModel.uiState.balanceItem?.asset ?: "",
                        ),
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MultimoneyTheme.colors.text
                        )
                    )
                }
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = stringResource(
                        id = R.string.dollar_symbol_value,
                        viewModel.uiState.balanceItem?.balanceDollars.toString()
                    ),
                    style = Typography.h4.copy(color = MultimoneyTheme.colors.text)
                )
                Text(
                    text = "${viewModel.uiState.balanceItem?.available} ${viewModel.uiState.balanceItem?.asset}",
                    style = Typography.body2,
                    color = WhiteTransparency60
                )
                Text(
                    text = stringResource(
                        id = R.string.currency_item_description,
                        viewModel.uiState.balanceItem?.investedBalanceCurrency ?: "",
                        viewModel.uiState.balanceItem?.percentageInvestedCurrency ?: ""
                    ),
                    style = Typography.body2,
                    color = MultimoneyTheme.colors.labelText
                )
                WalletCryptoGraphic(
                    clientCryptoBalanceHistory = viewModel.uiState.historicalBalance,
                    graphicColor = graphicColor,
                )
                DateFilterDWMYSection(
                    selectedDateFilter = selectedDateRange,
                    onDateFilterSelected = { dateSelected ->
                        selectedDateRange = dateSelected
                        viewModel.onUIEvent(
                            CryptoCurrencyDetailViewModel.UIEvent.OnSetDateRange(
                                dateSelected
                            )
                        )
                    }
                )
                Row(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
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
                    TextButton(onClick = { }) {
                        Text(
                            textAlign = TextAlign.End,
                            text = stringResource(id = R.string.currency_detail_see_all),
                            style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                            color = MultimoneyTheme.colors.textLink
                        )
                    }
                }
                LazyColumn(content = {
                    viewModel.uiState.cryptoMovement?.items?.let {
                        items(it) { cryptoMovement ->
                            CryptoMovementItem(cryptoMovement)
                        }
                    }
                })
            }
        }
    }
}