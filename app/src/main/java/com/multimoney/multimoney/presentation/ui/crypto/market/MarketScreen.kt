package com.multimoney.multimoney.presentation.ui.crypto.market

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.crypto.MarketCryptoCoin
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.MarketCurrencyItem
import com.multimoney.multimoney.presentation.ui.crypto.market.MarketScreenViewModel.UIEvent
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomInformativeChip
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.CustomSelector
import com.multimoney.multimoney.presentation.uielement.Size
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.roundToTwoDecimalPlacesWithoutNegatives
import kotlinx.coroutines.launch

@Composable
fun MarketScreen(
    viewModel: MarketScreenViewModel = hiltViewModel(),
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
) {
    LaunchedEffect(key1 = true) {
        viewModel.executeNavigation(
            onPopBackStack = onPopBackStack,
            onNavigate = onNavigate,
            onPopAndNavigate = onPopAndNavigate
        )

        viewModel.onUIEvent(UIEvent.OnGetUserInfo)
        viewModel.onUIEvent(UIEvent.OnGetAvailableListOfCryptoCoins)
    }

    BackHandler { viewModel.onUIEvent(UIEvent.OnNavigateBack) }
    MarketScreenContent()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MarketScreenContent(
    viewModel: MarketScreenViewModel = hiltViewModel()
) {

    val state = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()
    val searchQuery = remember { mutableStateOf("") }
    val selectedFilter = remember { mutableStateOf(MarketFilter.Price.value) }

    val crListOfCryptoCoin =
        viewModel.uiState.availableCryptoCoins?.availableCryptoCoins ?: emptyList()
    val svListOfCryptoCoins = viewModel.uiState.availableCryptoCoins
        ?.availableCryptoCoins?.filter { it.baseAsset == SV_DEFAULT_BASE_ASSET } ?: emptyList()

    Column(modifier = Modifier.background(MultimoneyTheme.colors.background)) {
        TopNavBar(
            isRightButtonVisible = false,
            onLeftButtonClick = { viewModel.onUIEvent(UIEvent.OnNavigateBack) }
        )
        ModalBottomSheetLayout(
            sheetState = state,
            sheetContent = {
                FilterBottomSheet(
                    filterQuery = selectedFilter,
                    onSelectFilterClick = {
                        selectedFilter.value = it
                        coroutineScope.launch {
                            state.hide()
                        }
                    },
                    onCancelClick = {
                        coroutineScope.launch {
                            state.hide()
                        }
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MultimoneyTheme.colors.background)
                    .padding(16.dp)
            ) {
                MarketHeader()
                if (viewModel.uiState.idBrand == Brand.CostaRica.id) {
                    CustomOutlinedTextField(
                        modifier = Modifier,
                        value = searchQuery.value,
                        isRequired = false,
                        onValueChange = { searchQuery.value = it },
                        keyboardActions = KeyboardActions.Default,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        leadingIcon = R.drawable.ic_search,
                        placeHolder = stringResource(id = R.string.crypto_wallet_search_crypto_currency),
                    )
                }
                if (viewModel.uiState.isLoading) {
                    MarketSkeleton()
                } else {
                    ListOfCoinsSection(
                        availableCryptoCoins = if (viewModel.uiState.idBrand == Brand.CostaRica.id)
                            crListOfCryptoCoin else svListOfCryptoCoins,
                        searchQuery = searchQuery,
                        selectedFilter = selectedFilter,
                        sheetState = state,
                        showFilterChip = viewModel.uiState.idBrand == Brand.CostaRica.id,
                        onCurrencyItemClick = { cryptoCurrency ->
                            viewModel.onUIEvent(
                                UIEvent.OnSetAssetBeforeNavigation(
                                    cryptoCurrency
                                )
                            )
                            viewModel.onUIEvent(UIEvent.OnNavigateToCurrencyDetails)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MarketHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = stringResource(id = R.string.market_header_title),
            style = Typography.h5,
            color = MultimoneyTheme.colors.text
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FilterSection(
    selectedFilter: MutableState<String>,
    sheetState: ModalBottomSheetState
) {

    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.End
    ) {
        CoinFilter(
            filterQuery = selectedFilter,
            onFilterClick = {
                coroutineScope.launch { sheetState.show() }
            }
        )
    }
}

@Composable
fun CoinFilter(
    filterQuery: MutableState<String>,
    onFilterClick: () -> Unit = {}
) {

    CustomInformativeChip(
        modifier = Modifier.padding(top = 16.dp),
        text = filterQuery.value,
        textStyle = Typography.body2.copy(color = MultimoneyTheme.colors.text),
        shape = CircleShape,
        background = MultimoneyTheme.colors.creditDetailBackground,
        startIcon = R.drawable.ic_dropdown_open,
        startIconTint = MultimoneyTheme.colors.textLink,
        size = Size.Small,
        onClick = onFilterClick
    )
}

@Composable
fun FilterBottomSheet(
    onSelectFilterClick: (String) -> Unit = {},
    filterQuery: MutableState<String>,
    onCancelClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MultimoneyTheme.colors.background)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CustomButton(
                buttonType = CustomButtonType.PrimaryQuaternaryUnderLined,
                text = stringResource(id = R.string.market_cancel_filter),
                onClick = onCancelClick,
            )
            CustomButton(
                buttonType = CustomButtonType.PrimaryQuaternaryUnderLined,
                text = stringResource(id = R.string.market_select_filter),
                onClick = {
                    onSelectFilterClick(filterQuery.value)
                },
            )
        }
        Divider(color = MultimoneyTheme.colors.dividerWhite30)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(MarketFilter.values()) { filter ->
                CustomSelector(
                    text = filter.value,
                    selected = filter.value == filterQuery.value,
                    onOptionSelected = {
                        filterQuery.value = filter.value
                    }
                )
            }
        }
    }
}

enum class MarketFilter(val value: String) {
    Price(PRICE_FILTER_VALUE),
    AZ(AZ_FILTER_VALUE)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListOfCoinsSection(
    availableCryptoCoins: List<MarketCryptoCoin> = emptyList(),
    searchQuery: MutableState<String>,
    selectedFilter: MutableState<String>,
    sheetState: ModalBottomSheetState,
    showFilterChip: Boolean = false,
    onCurrencyItemClick: (MarketCryptoCoin) -> Unit
) {

    val filteredListByQuery =
        if (availableCryptoCoins.isNotEmpty()) availableCryptoCoins.filter { cryptoCoin ->
            cryptoCoin.description.contains(searchQuery.value, true)
                    || cryptoCoin.baseAsset.contains(searchQuery.value, true)
        } else emptyList()

    val filteredList = if (selectedFilter.value == MarketFilter.AZ.value) {
        filteredListByQuery.sortedBy { it.description }
    } else {
        filteredListByQuery.sortedByDescending { it.currentPrice.toString().toDouble() }
    }

    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        if (showFilterChip) {
            item {
                FilterSection(selectedFilter = selectedFilter, sheetState = sheetState)
            }
        }
        items(filteredList) { cryptoCoin ->
            MarketCurrencyItem(
                imageUrl = cryptoCoin.url_image,
                descriptionCurrency = cryptoCoin.description,
                asset = cryptoCoin.baseAsset,
                amountChange = cryptoCoin.amountchange,
                percentChange = stringResource(
                    id = R.string.currency_item_percent_invested_with_symbol,
                    if (cryptoCoin.percentChange.contains(NEGATIVE_SYMBOL)) NEGATIVE_SYMBOL else POSITIVE_SYMBOL,
                    cryptoCoin.percentChange.toDouble().roundToTwoDecimalPlacesWithoutNegatives()
                ),
                currentPrice = cryptoCoin.currentPrice.toString().toDouble(),
                onCurrencyItemClick = { onCurrencyItemClick(cryptoCoin) }
            )
        }
    }
}

const val SV_DEFAULT_BASE_ASSET = "BTC"
const val PRICE_FILTER_VALUE = "Precio"
const val AZ_FILTER_VALUE = "A-Z"
const val NEGATIVE_SYMBOL = "-"
const val POSITIVE_SYMBOL = "+"