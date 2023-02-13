package com.multimoney.multimoney.presentation.ui.crypto.sell.listofcurrencies

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.domain.model.balance.BalanceCryptoAccountItems
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.LocalMultimoneyColors
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.CryptoCurrencySendItem
import com.multimoney.multimoney.presentation.ui.crypto.sell.SellCryptoSharedViewModel
import com.multimoney.multimoney.presentation.ui.crypto.send.listofcurrencies.CryptoSendCurrenciesListViewModel
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SellCurrenciesListScreen(
    viewModel: CryptoSendCurrenciesListViewModel = hiltViewModel(),
    sharedViewModel: SellCryptoSharedViewModel,
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
        viewModel.onUIEvent(CryptoSendCurrenciesListViewModel.UIEvent.OnGetBalanceCrypto)
    }

    CryptoSellContent(
        sharedViewModel,
        itemClick = {
            sharedViewModel.onUIEvent(SellCryptoSharedViewModel.UIEvent.OnCryptoSelected(it))
            sharedViewModel.onUIEvent(SellCryptoSharedViewModel.UIEvent.OnNextStep)
        },
    )

    BackHandler {
        sharedViewModel.onUIEvent(SellCryptoSharedViewModel.UIEvent.OnPreviousStep)
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
fun CryptoSellContent(
    sharedViewModel: SellCryptoSharedViewModel,
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
            text = stringResource(id = R.string.crypt_list_sell_title),
            style = Typography.h6.copy(
                fontWeight = FontWeight.SemiBold,
                color = LocalMultimoneyColors.current.titleText
            ),
            textAlign = TextAlign.Left
        )

            CryptoSellList(
                cryptoAccounts = sharedViewModel.uiState.userCryptoBalances,
                searchQuery = searchQuery,
                itemClick = itemClick
            )

    }
}

@Composable
fun CryptoSellList(
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
