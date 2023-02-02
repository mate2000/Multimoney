package com.multimoney.multimoney.presentation.ui.crypto.purchase.listofcurrency

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.crypto.MarketCryptoCoin
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.LocalMultimoneyColors
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.CryptoCurrencyPurchaseItem
import com.multimoney.multimoney.presentation.ui.crypto.market.FilterBottomSheet
import com.multimoney.multimoney.presentation.ui.crypto.market.FilterSection
import com.multimoney.multimoney.presentation.ui.crypto.market.MarketFilter
import com.multimoney.multimoney.presentation.ui.crypto.market.MarketSkeleton
import com.multimoney.multimoney.presentation.ui.crypto.purchase.PurchaseCryptoSharedViewModel
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.formattedTwoDecimalsNumber
import com.multimoney.multimoney.presentation.util.transformation.formatWithComma
import kotlinx.coroutines.launch

@Composable
fun ListCryptoCurrenciesScreen(
    viewModel: ListCryptoPurchaseViewModel = hiltViewModel(),
    sharedViewModel: PurchaseCryptoSharedViewModel,
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(
            onPopBackStack = onPopBackStack
        )
        viewModel.onUIEvent(ListCryptoPurchaseViewModel.UIEvent.OnGetUserInfo(
            user = sharedViewModel.email,
            idBrand = sharedViewModel.idBrand
        ))
        viewModel.onUIEvent(ListCryptoPurchaseViewModel.UIEvent.OnGetAvailableListOfCryptoCoins)
    }

    ListCryptoContent(
        viewModel.uiState,
        itemClick = {
            sharedViewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnCryptoSelected(it))
            sharedViewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnNextStep)
        }
    )

    BackHandler {
        sharedViewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnPreviousStep)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListCryptoContent(
    uiState: ListCryptoPurchaseViewModel.UiState,
    itemClick: (MarketCryptoCoin) -> Unit
) {
    val searchQuery = remember { mutableStateOf("") }
    val selectedFilter = remember { mutableStateOf(MarketFilter.Price.value) }
    val state = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.background(MultimoneyTheme.colors.background)) {
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
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = stringResource(id = R.string.crypt_list_purchase_title),
                    style = Typography.h6.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = LocalMultimoneyColors.current.titleText
                    ),
                    textAlign = TextAlign.Left
                )
                if (uiState.idBrand == Brand.CostaRica.id) {
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
                    FilterSection(selectedFilter = selectedFilter, sheetState = state)
                }
                if (uiState.isLoading) {
                    MarketSkeleton()
                } else {
                    ListCryptoBody(
                        uiState.availableCryptoCoins?.availableCryptoCoins ?: listOf(),
                        searchQuery,
                        selectedFilter
                    ) {
                        itemClick(it)
                    }
                }
            }
        }
    }
}

@Composable
fun ListCryptoBody(
    coinsList: List<MarketCryptoCoin>,
    searchQuery: MutableState<String>,
    selectedFilter: MutableState<String>,
    itemClick: (MarketCryptoCoin) -> Unit
) {
    val filteredListByQuery =
        if (coinsList.isNotEmpty()) coinsList.filter { cryptoCoin ->
            cryptoCoin.description.contains(searchQuery.value, true)
                    || cryptoCoin.baseAsset.contains(searchQuery.value, true)
        } else emptyList()

    val filteredList = if (selectedFilter.value == MarketFilter.AZ.value) {
        filteredListByQuery.sortedBy { it.baseAsset }
    } else {
        filteredListByQuery.sortedByDescending { it.currentPrice.toString().toDouble() }
    }

    LazyColumn(content = {
        items(filteredList) {
            CryptoCurrencyPurchaseItem(
                imageUrl = it.url_image,
                descriptionCurrency = it.description,
                asset = it.baseAsset,
                priceOfTheDay = it.currentPrice.toString().toDouble().formatWithComma(),
                percentageInvestedCurrency = it.percentChange
            ) {
                itemClick(it)
            }
        }
    })
}