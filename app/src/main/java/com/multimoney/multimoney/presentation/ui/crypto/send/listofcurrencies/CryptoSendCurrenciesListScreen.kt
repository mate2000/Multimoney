package com.multimoney.multimoney.presentation.ui.crypto.send.listofcurrencies

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.balance.BalanceCryptoAccountItems
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.LocalMultimoneyColors
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.CryptoCurrencySendItem
import com.multimoney.multimoney.presentation.ui.crypto.market.MarketSkeleton
import com.multimoney.multimoney.presentation.ui.crypto.send.CryptoSendSharedViewModel
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun CryptoSendListOfCurrenciesScreen(
    viewModel: CryptoSendCurrenciesListViewModel = hiltViewModel(),
    sharedViewModel: CryptoSendSharedViewModel,
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(
            onPopBackStack = onPopBackStack
        )
        viewModel.onUIEvent(
            CryptoSendCurrenciesListViewModel.UIEvent.OnGetUserInfo(
                user = sharedViewModel.email,
                idBrand = sharedViewModel.idBrand,
                identification = sharedViewModel.identification
            )
        )
        viewModel.onUIEvent(
            CryptoSendCurrenciesListViewModel.UIEvent.OnSetOpenMaintenanceAction(
            action = {
                sharedViewModel.onUIEvent(
                    CryptoSendSharedViewModel.BaseEvent.OnShowMaintenance
                )
            }
        ))
        viewModel.onUIEvent(CryptoSendCurrenciesListViewModel.UIEvent.OnGetBalanceCrypto)
    }

    CryptoSendContent(
        viewModel.uiState,
        itemClick = {
            sharedViewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnCryptoSelected(it))
            sharedViewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnNextStep)
        },
    )

    BackHandler {
        sharedViewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnPreviousStep)
    }

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = viewModel.uiState.openDialog.description,
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction,
            onDismissAction = viewModel.uiState.openDialog.dismissAction
        )
    }
}

@Composable
fun CryptoSendContent(
    uiState: CryptoSendCurrenciesListViewModel.UiState,
    itemClick: (BalanceCryptoAccountItems) -> Unit
) {
    val searchQuery = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = stringResource(id = R.string.crypt_list_send_title),
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
        }
        if (uiState.isLoading) {
            MarketSkeleton()
        } else {
            CryptoSendList(
                cryptoAccounts = uiState.cryptoAccounts,
                searchQuery = searchQuery,
                itemClick = itemClick
            )
        }
    }
}

@Composable
fun CryptoSendList(
    cryptoAccounts: List<BalanceCryptoAccountItems>,
    searchQuery: MutableState<String>,
    itemClick: (BalanceCryptoAccountItems) -> Unit
) {
    val filteredListByQuery =
        if (cryptoAccounts.isNotEmpty()) cryptoAccounts.filter { cryptoCoin ->
            cryptoCoin.descriptionCurrency.contains(searchQuery.value, true)
                    || cryptoCoin.asset.contains(searchQuery.value, true)
        } else emptyList()

    LazyColumn(modifier = Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(filteredListByQuery) { item ->
            CryptoCurrencySendItem(
                imageUrl = item.url_image,
                descriptionCurrency = item.descriptionCurrency,
                asset = item.asset,
                balanceDollars = item.balanceDollars,
                available = item.available,
                onClick = { itemClick(item) }
            )
        }
    }
}
