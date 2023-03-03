package com.multimoney.multimoney.presentation.ui.crypto.wallet.currencydetail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.LocalMultimoneyColors
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.CryptoCurrencyMovementItem
import com.multimoney.multimoney.presentation.ui.crypto.wallet.currencydetail.WalletCryptoCurrencyDetailsViewModel.Companion.TODAY_TEXT
import com.multimoney.multimoney.presentation.ui.crypto.graphics.DateFilterDWMYSection
import com.multimoney.multimoney.presentation.ui.crypto.graphics.MarketCurrencyDetailsGraphic
import com.multimoney.multimoney.presentation.ui.crypto.purchase.selectaccount.ConfirmationBottomSheet
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoActionsSection
import com.multimoney.multimoney.presentation.ui.crypto.wallet.currencydetail.WalletCryptoCurrencyDetailsViewModel.UIEvent.OnNavigateToReleaseTransaction
import com.multimoney.multimoney.presentation.uielement.BalanceTextView
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.FilterDateByDays
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.addTextStyleToTextPortion
import com.multimoney.multimoney.presentation.util.toCurrencyFormat

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WalletCurrencyDetailsScreen(
    viewModel: WalletCryptoCurrencyDetailsViewModel = hiltViewModel(),
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.executeNavigation(
            onPopBackStack = onPopBackStack,
            onNavigate = onNavigate,
            onPopAndNavigate = onPopAndNavigate
        )
        viewModel.onUIEvent(WalletCryptoCurrencyDetailsViewModel.UIEvent.OnGetUserInfo)
        viewModel.onUIEvent(
            WalletCryptoCurrencyDetailsViewModel.UIEvent.OnSetDateRange(
                FilterDateByDays.YESTERDAY.time
            )
        )
        viewModel.onUIEvent(WalletCryptoCurrencyDetailsViewModel.UIEvent.OnGetAssetHistory)
        viewModel.onUIEvent(WalletCryptoCurrencyDetailsViewModel.UIEvent.OnGetMovements)
    }

    BackHandler { viewModel.onUIEvent(WalletCryptoCurrencyDetailsViewModel.UIEvent.OnNavigateBack) }

    CurrencyDetailContent(
        uiState = viewModel.uiState,
        backPressed = { viewModel.onUIEvent(WalletCryptoCurrencyDetailsViewModel.UIEvent.OnNavigateBack) },
        onDateChanged = { dateSelected ->
            viewModel.onUIEvent(
                WalletCryptoCurrencyDetailsViewModel.UIEvent.OnSetDateRange(
                    dateSelected
                )
            )
        },
        viewAllClick = { viewModel.onUIEvent(WalletCryptoCurrencyDetailsViewModel.UIEvent.OnViewAllMovements) },
        buyCryptoClick = {
            viewModel.onUIEvent(
                WalletCryptoCurrencyDetailsViewModel.UIEvent.OnRegisterAdjustPressPurchaseFirstTime
            )
            if (viewModel.uiState.idBrand == Brand.ElSalvador.id) {
                if (viewModel.uiState.shouldDisplayDisclaimer) {
                    viewModel.onUIEvent(WalletCryptoCurrencyDetailsViewModel.UIEvent.OnShowDisclaimer)
                } else {
                    viewModel.onUIEvent(WalletCryptoCurrencyDetailsViewModel.UIEvent.OnNavigateToSelectAccount)
                }
            } else {
                viewModel.onUIEvent(WalletCryptoCurrencyDetailsViewModel.UIEvent.OnNavigateToSelectAccount)
            }
        },
        sellCryptoClick = {
            viewModel.onUIEvent(WalletCryptoCurrencyDetailsViewModel.UIEvent.OnNavigateToSellCrypto)
            viewModel.onUIEvent(
                WalletCryptoCurrencyDetailsViewModel.UIEvent.OnRegisterAdjustPressSellFirstTime
            )
        },
        sendCryptoClick = {
            viewModel.onUIEvent(WalletCryptoCurrencyDetailsViewModel.UIEvent.OnNavigateToSendCrypto)
            viewModel.onUIEvent(
                WalletCryptoCurrencyDetailsViewModel.UIEvent.OnRegisterAdjustPressSendFirstTime
            )
        },
        giveCryptoClick = {
            viewModel.onUIEvent(WalletCryptoCurrencyDetailsViewModel.UIEvent.OnNavigateToReceiveCrypto)
            viewModel.onUIEvent(
                WalletCryptoCurrencyDetailsViewModel.UIEvent.OnRegisterAdjustPressReceiveFirstTime
            )
        },
        onReleaseTransactionClick = {
            viewModel.onUIEvent(
                OnNavigateToReleaseTransaction(
                    it
                )
            )
        }
    )

    ConfirmationBottomSheet(
        modalBottomSheetState = viewModel.uiState.bottomSheetVisibleState,
        coroutineScope = coroutineScope,
        onCheckedChange = {
            viewModel.onUIEvent(WalletCryptoCurrencyDetailsViewModel.UIEvent.OnDisclaimerChecked(it))
        },
        onContinueClicked = {
            viewModel.onUIEvent(
                WalletCryptoCurrencyDetailsViewModel.UIEvent.OnUpdateShouldShowDisclaimer(
                    viewModel.uiState.dontShowAgainChecked
                )
            )
            viewModel.onUIEvent(WalletCryptoCurrencyDetailsViewModel.UIEvent.OnHideDisclaimer)
            viewModel.onUIEvent(WalletCryptoCurrencyDetailsViewModel.UIEvent.OnNavigateToSelectAccount)

        },
        checked = viewModel.uiState.dontShowAgainChecked
    )
}

@Composable
fun CurrencyDetailContent(
    uiState: WalletCryptoCurrencyDetailsViewModel.UiState,
    backPressed: () -> Unit,
    onDateChanged: (Long) -> Unit,
    viewAllClick: () -> Unit,
    buyCryptoClick: () -> Unit,
    sendCryptoClick: () -> Unit,
    sellCryptoClick: () -> Unit,
    giveCryptoClick: () -> Unit,
    onReleaseTransactionClick: (CryptoCurrencyMovement?) -> Unit = {}
) {

    val movements = uiState.cryptoMovements.collectAsLazyPagingItems()

    var selectedDateRange by remember { mutableStateOf(FilterDateByDays.YESTERDAY.time) }
    val graphicColor =
        if (uiState.cryptoItem?.investedBalanceCurrency?.contains("+") == true)
            MultimoneyTheme.colors.cryptoWalletGainsColor else MultimoneyTheme.colors.cryptoLossesColor
    val gainOrLossColor =
        if (uiState.cryptoItem?.investedBalanceCurrency?.contains('-') == true) MultimoneyTheme.colors.cryptoLossesColor
        else MultimoneyTheme.colors.cryptoGainsColor
    Scaffold(
        topBar = {
            TopNavBar(
                isRightButtonVisible = false,
                onLeftButtonClick = backPressed
            )
        },
        bottomBar = {
            CryptoActionsSection(
                hasSmartBalance = true,
                enableCryptoActions = true,
                enableSendAndGive = uiState.isCryptoTransferEnabled,
                hasBalanceAction = { buyCryptoClick() },
                sellAction = { sellCryptoClick() },
                sendAction = sendCryptoClick,
                giveAction = { giveCryptoClick() }
            )
        },
        modifier = Modifier.fillMaxSize(),
        backgroundColor = MultimoneyTheme.colors.background,
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
                uiState.cryptoItem?.balanceDollars?.let { balance ->
                    BalanceTextView(
                        modifier = Modifier,
                        balanceText = balance.toCurrencyFormat(),
                        currencyStyle = Typography.h4.copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.Bold
                        ),
                        currencyDecimalStyle = Typography.body2.copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
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
                        CryptoCurrencyMovementItem(
                            cryptoCurrencyMovement = it,
                            onReleaseTransactionClick = { onReleaseTransactionClick(it) }
                        )
                    }
                }
            }
        }
    }
}
