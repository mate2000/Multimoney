package com.multimoney.multimoney.presentation.ui.crypto.market.currencydetails

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.crypto.CryptoNewsFeed
import com.multimoney.domain.model.crypto.CurrencyHistoricPrice
import com.multimoney.domain.model.crypto.New
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.graphics.FullDateFilterSection
import com.multimoney.multimoney.presentation.ui.crypto.graphics.MarketCurrencyDetailsGraphic
import com.multimoney.multimoney.presentation.ui.crypto.market.currencydetails.MarketCurrencyDetailsViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.crypto.market.currencydetails.MarketCurrencyDetailsViewModel.UIEvent.OnGetCurrencyHistoricalPrices
import com.multimoney.multimoney.presentation.ui.crypto.market.currencydetails.MarketCurrencyDetailsViewModel.UIEvent.OnGetCurrencyNews
import com.multimoney.multimoney.presentation.ui.crypto.market.currencydetails.MarketCurrencyDetailsViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.crypto.market.currencydetails.MarketCurrencyDetailsViewModel.UIEvent.OnOpenCryptoNew
import com.multimoney.multimoney.presentation.ui.crypto.market.currencydetails.MarketCurrencyDetailsViewModel.UIEvent.OnSetPreviousInfo
import com.multimoney.multimoney.presentation.ui.crypto.purchase.selectaccount.ConfirmationBottomSheet
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoActionsSection
import com.multimoney.multimoney.presentation.uielement.BalanceTextView
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.FilterDateByDays
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.calculateGainLosesMarketDetails
import com.multimoney.multimoney.presentation.util.calculatePercentageMarketDetails
import com.multimoney.multimoney.presentation.util.decodeURLFromUTF
import com.multimoney.multimoney.presentation.util.openIntent
import com.multimoney.multimoney.presentation.util.roundToTwoDecimalPlacesWithoutNegatives
import com.multimoney.multimoney.presentation.util.toCurrencyFormat
import com.multimoney.multimoney.presentation.util.toCurrencyFormatWithoutNegatives

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MarketCurrencyDetailsScreen(
    viewModel: MarketCurrencyDetailsViewModel = hiltViewModel(),
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.executeNavigation(
            onPopBackStack = onPopBackStack,
            onNavigate = onNavigate,
            onPopAndNavigate = onPopAndNavigate
        )

        viewModel.onUIEvent(OnSetPreviousInfo)
        viewModel.onUIEvent(OnGetCurrencyHistoricalPrices())
        viewModel.onUIEvent(OnGetCurrencyNews)
    }

    BackHandler {
        viewModel.onUIEvent(OnNavigateBack)
    }
    MarketCurrencyDetailsScreenContent(
        currencyHistoricalPrices = viewModel.uiState.getHistoricalCurrencyPrices,
        currencyNews = viewModel.uiState.currencyNews,
        idBrand = viewModel.uiState.idBrand ?: 0,
        description = viewModel.uiState.selectedCryptoCoin?.description ?: "",
        currentPrice = viewModel.uiState.selectedCryptoCoin?.currentPrice ?: 0.0,
        urlImage = viewModel.uiState.selectedCryptoCoin?.url_image ?: "",
        onDateFilterSelected = { dateFilter ->
            viewModel.onUIEvent(
                OnGetCurrencyHistoricalPrices(daysToSubtract = dateFilter)
            )
        },
        onOpenCryptoNew = { link ->
            viewModel.onUIEvent(OnOpenCryptoNew(
                link = link,
                openCryptoNew = { openCryptoNew ->
                    context.openIntent(openCryptoNew) {
                        viewModel.onUIEvent(
                            OnFailureWithDialog(
                                isLoading = true,
                                dialogParameters = viewModel.defaultDialogParameters.copy(
                                    isActive = mutableStateOf(true)
                                )
                            )
                        )
                    }
                },
                onFailureWithDialog = { isLoading, dialogParameters ->
                    viewModel.onUIEvent(
                        OnFailureWithDialog(
                            isLoading = isLoading,
                            dialogParameters = dialogParameters
                        )
                    )
                }
            ))
        },

        onNavigateToBuyCrypto = {
            if(viewModel.uiState.idBrand == Brand.ElSalvador.id){
                if (viewModel.uiState.shouldDisplayDisclaimer) {
                    viewModel.onUIEvent(MarketCurrencyDetailsViewModel.UIEvent.OnShowDisclaimer)
                } else {
                    viewModel.onUIEvent(MarketCurrencyDetailsViewModel.UIEvent.OnNavigateToSelectAccount)
                }
            }
            else{
                viewModel.onUIEvent(MarketCurrencyDetailsViewModel.UIEvent.OnNavigateToSelectAccount)
            }
        },
        onBackPressed = { viewModel.onUIEvent(OnNavigateBack) },
        onNavigateToSendCrypto = { viewModel.onUIEvent(MarketCurrencyDetailsViewModel.UIEvent.OnNavigateToCryptoSendFlow) },
    )
    ConfirmationBottomSheet(
        modalBottomSheetState = viewModel.uiState.bottomSheetVisibleState,
        coroutineScope = coroutineScope,
        onCheckedChange = {
            viewModel.onUIEvent(MarketCurrencyDetailsViewModel.UIEvent.OnDisclaimerChecked(it))
        },
        onContinueClicked = {
            viewModel.onUIEvent(MarketCurrencyDetailsViewModel.UIEvent.OnUpdateShouldShowDisclaimer(viewModel.uiState.dontShowAgainChecked))
            viewModel.onUIEvent(MarketCurrencyDetailsViewModel.UIEvent.OnHideDisclaimer)
            viewModel.onUIEvent(MarketCurrencyDetailsViewModel.UIEvent.OnNavigateToSelectAccount)
        },
        checked = viewModel.uiState.dontShowAgainChecked
    )
}

@Composable
fun MarketCurrencyDetailsScreenContent(
    currencyHistoricalPrices: List<CurrencyHistoricPrice>,
    currencyNews: CryptoNewsFeed?,
    idBrand: Int,
    description: String,
    currentPrice: Double,
    urlImage: String,
    onDateFilterSelected: (Long) -> Unit = {},
    onOpenCryptoNew: (String) -> Unit = {},
    onNavigateToBuyCrypto: () -> Unit = {},
    onBackPressed: () -> Unit = {},
    onNavigateToSendCrypto: () -> Unit = {},
) {
    val selected = remember { mutableStateOf(true) }
    var selectedDateRange by remember { mutableStateOf(FilterDateByDays.YESTERDAY.time) }
    val amountChangeValue = calculateGainLosesMarketDetails(currentPrice, currencyHistoricalPrices)
    val percentage = calculatePercentageMarketDetails(currentPrice, currencyHistoricalPrices)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            CryptoActionsSection(
                modifier = Modifier.background(color = MultimoneyTheme.colors.background),
                hasSmartBalance = true,
                enableCryptoActions = true,
                enableSendAndGive = idBrand == Brand.CostaRica.id,
                hasBalanceAction = { onNavigateToBuyCrypto() },
                sellAction = { /* todo: go to sell crypto flow */ },
                giveAction = { /* todo: go to receive crypto flow */ },
                sendAction = { onNavigateToSendCrypto() },
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .background(color = MultimoneyTheme.colors.background)
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TopNavBar(
                isRightButtonVisible = false,
                onLeftButtonClick = onBackPressed
            )
            MarketDetailsHeaderSection(
                descriptionCurrency = description,
                imageResource = urlImage.decodeURLFromUTF(),
            )
            BalanceSection(
                currentPrice = currentPrice,
                amountChangeValue = amountChangeValue,
                percentage = percentage
            )
            MarketCurrencyDetailsGraphic(
                currencyHistory = currencyHistoricalPrices,
                graphicColor = if (amountChangeValue < 0) {
                    MultimoneyTheme.colors.cryptoLossesColor
                } else {
                    MultimoneyTheme.colors.cryptoGainsColor
                }
            )
            FullDateFilterSection(
                selectedDateFilter = selectedDateRange,
                onDateFilterSelected = {
                    selectedDateRange = it
                    onDateFilterSelected(it)
                }
            )
            MarketDetailsButtonsSection(
                onHistoryClick = {
                    selected.value = true
                },
                onNewsClick = {
                    selected.value = false
                },
                selected = selected
            )
            if (selected.value) {
                HistorySection(information = currencyNews?.information ?: "")
            } else {
                NewsSection(
                    currencyNews = currencyNews?.result ?: emptyList(),
                    onOpenCryptoNew = onOpenCryptoNew
                )
            }
        }
    }
}

@Composable
fun MarketDetailsHeaderSection(
    descriptionCurrency: String,
    imageResource: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .size(32.dp)
                .align(Alignment.CenterVertically),
            painter = rememberAsyncImagePainter(model = imageResource),
            contentDescription = null
        )
        Text(
            text = descriptionCurrency,
            style = Typography.h6.copy(
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun BalanceSection(
    currentPrice: Double,
    amountChangeValue: Double,
    percentage: Double
) {

    val gainOrLoss = if (amountChangeValue < 0.0) stringResource(id = R.string.crypto_losses_symbol)
    else stringResource(id = R.string.crypto_gains_symbol)
    val gainOrLossColor = if (amountChangeValue < 0.0) MultimoneyTheme.colors.cryptoLossesColor
    else MultimoneyTheme.colors.cryptoGainsColor

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {

        BalanceTextView(
            balanceText = currentPrice.toCurrencyFormat(),
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
            text = stringResource(
                id = R.string.currency_item_gain_or_losses_description,
                gainOrLoss,
                amountChangeValue.toCurrencyFormatWithoutNegatives(),
                percentage.roundToTwoDecimalPlacesWithoutNegatives()
            ),
            style = Typography.body2.copy(color = gainOrLossColor)
        )
    }
}

@Composable
fun MarketDetailsButtonsSection(
    selected: MutableState<Boolean>,
    onHistoryClick: () -> Unit,
    onNewsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        CustomButton(
            modifier = Modifier
                .width(164.dp)
                .wrapContentHeight()
                .padding(horizontal = 8.dp),
            buttonType = if (selected.value) CustomButtonType.PrimaryQuinary else CustomButtonType.PrimaryQuaternary,
            text = stringResource(R.string.market_details_button_history),
            onClick = onHistoryClick
        )
        CustomButton(
            modifier = Modifier
                .width(164.dp)
                .wrapContentHeight()
                .padding(horizontal = 8.dp),
            buttonType = if (selected.value.not()) CustomButtonType.PrimaryQuinary else CustomButtonType.PrimaryQuaternary,
            text = stringResource(R.string.market_details_button_news),
            onClick = onNewsClick
        )
    }
}

@Composable
fun NewsSection(
    currencyNews: List<New>,
    onOpenCryptoNew: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
    ) {
        items(currencyNews) { new ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 16.dp),
                    text = new.title,
                    style = Typography.subtitle1.copy(
                        color = MultimoneyTheme.colors.text,
                        fontWeight = FontWeight.Bold
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { onOpenCryptoNew(new.link) }) {
                        Text(
                            text = stringResource(id = R.string.market_details_button_read_more),
                            style = Typography.subtitle1.copy(color = MultimoneyTheme.colors.textLink),
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
                Divider(color = MultimoneyTheme.colors.dividerWhite30)
            }
        }
    }
}

@Composable
fun HistorySection(information: String) {

    val maxLines = remember { mutableStateOf(DEFAULT_CRYPTO_HISTORY_LINES) }
    val textExpanded = remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            text = information,
            style = Typography.body2.copy(color = MultimoneyTheme.colors.bodyTextColor),
            maxLines = maxLines.value,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Start,
        ) {
            TextButton(
                onClick = {
                    maxLines.value = if (textExpanded.value) {
                        textExpanded.value = false
                        Int.MAX_VALUE
                    } else {
                        textExpanded.value = true
                        DEFAULT_CRYPTO_HISTORY_LINES
                    }
                }
            ) {
                Text(
                    text = if (textExpanded.value) stringResource(
                        id = R.string.market_details_button_show_more
                    ) else stringResource(
                        id = R.string.market_details_button_show_less
                    ),
                    style = Typography.body2.copy(
                        color = MultimoneyTheme.colors.textLink,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

const val DEFAULT_CRYPTO_HISTORY_LINES = 4
