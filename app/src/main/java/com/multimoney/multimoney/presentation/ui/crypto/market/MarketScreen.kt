package com.multimoney.multimoney.presentation.ui.crypto.market

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.domain.model.crypto.MarketCryptoCoin
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.MarketCurrencyItem
import com.multimoney.multimoney.presentation.ui.crypto.market.MarketScreenViewModel.UIEvent.OnGetUserInfo
import com.multimoney.multimoney.presentation.ui.crypto.market.MarketScreenViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.crypto.market.MarketScreenViewModel.UIEvent.OnGetAvailableListOfCryptoCoins
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomInformativeChip
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.Size
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun MarketScreen(
    marketViewModel: MarketScreenViewModel = hiltViewModel(),
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
) {

    LaunchedEffect(key1 = true) {
        marketViewModel.executeNavigation(
            onPopBackStack = onPopBackStack,
            onNavigate = onNavigate,
            onPopAndNavigate = onPopAndNavigate
        )

        marketViewModel.onUIEvent(OnGetUserInfo)
        marketViewModel.onUIEvent(OnGetAvailableListOfCryptoCoins)
    }

    BackHandler { marketViewModel.onUIEvent(OnNavigateBack) }
    MarketScreenContent()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MarketScreenContent(
    marketViewModel: MarketScreenViewModel = hiltViewModel()
) {

    val searchQuery = remember { mutableStateOf("") }

    ModalBottomSheetLayout(sheetContent = { FilterBottomSheet() }) {
        Column(modifier = Modifier.fillMaxSize().background(MultimoneyTheme.colors.background)) {
            TopNavBar(
                isRightButtonVisible = false,
                leftButtonIcon = R.drawable.ic_arrow_left,
                onLeftButtonClick = { marketViewModel.onUIEvent(OnNavigateBack) }
            )
            MarketHeader()
            CustomOutlinedTextField(
                modifier = Modifier.padding(
                    top = 8.dp,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                value = searchQuery.value,
                keyboardActions = KeyboardActions.Default,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                leadingIcon = R.drawable.ic_search,
                placeHolder = stringResource(id = R.string.crypto_wallet_search_crypto_currency),
            )
            FilterSection()
            ListOfCoinsSection(
                availableCryptoCoins =
                marketViewModel.uiState.availableCryptoCoins?.availableCryptoCoins ?: emptyList(),
            )
            /*if (marketViewModel.uiState.isLoading) {
                MarketSkeleton()
            } else {

            }*/
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

@Composable
private fun FilterSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.End
    ) {
        CoinFilter(remember {mutableStateOf("Price") })
    }
}

@Composable
fun CoinFilter(
    filterQuery: MutableState<String>,
    onFilterClick: () -> Unit = {}
) {

    CustomInformativeChip(
        text = filterQuery.value,
        textStyle = Typography.body2,
        shape = CircleShape,
        startIcon = R.drawable.ic_dropdown_open,
        size = Size.Small,
        onClick = onFilterClick
    )
}

@Composable
fun FilterBottomSheet(
    onSelectFilterClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            CustomButton(
                text = "Cancelar",
                onClick = onCancelClick,
            )
            CustomButton(
                text = "Seleccionar",
                onClick = onSelectFilterClick,
            )
        }
        Divider()
        Box(contentAlignment = Alignment.Center) {
            //select filter section
        }
    }
}

@Composable
fun ListOfCoinsSection(
    availableCryptoCoins: List<MarketCryptoCoin> = emptyList()
) {

    LazyColumn {
        items(availableCryptoCoins) { cryptoCoin ->
            MarketCurrencyItem(
                imageUrl = cryptoCoin.url_image,
                descriptionCurrency = cryptoCoin.description,
                asset = cryptoCoin.baseAsset,
                percentChange = cryptoCoin.percentChange.toDouble(),
                currentPrice = cryptoCoin.currentPrice.toString().toDouble()
            )
        }
    }
}
