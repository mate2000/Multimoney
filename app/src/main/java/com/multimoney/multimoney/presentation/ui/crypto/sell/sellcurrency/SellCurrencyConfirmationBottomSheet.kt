package com.multimoney.multimoney.presentation.ui.crypto.sell.sellcurrency

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.ConfirmationBottomSheetContent
import com.multimoney.multimoney.presentation.util.calculateConfirmationBaseAmount
import com.multimoney.multimoney.presentation.util.calculateConfirmationQuoteAmount
import com.multimoney.multimoney.presentation.util.calculateConvertedCurrencyBalance
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.toCurrencyFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SellConfirmationBottomSheet(
    modalBottomSheetState: ModalBottomSheetState,
    coroutineScope: CoroutineScope,
    viewModel: SellCurrencyScreenViewModel
) {
    Column(modifier = Modifier
        .wrapContentSize()
        .background(color = MultimoneyTheme.colors.creditDetailBackground)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.crypto_purchase_flow_confirmation_title),
                style = Typography.subtitle1.copy(
                    color = MultimoneyTheme.colors.text,
                    fontWeight = FontWeight.Bold
                )
            )
            Image(
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        viewModel.onUIEvent(SellCurrencyScreenViewModel.UIEvent.OnCloseSellConfirmationBottomSheet)
                        modalBottomSheetState.hide()
                    }
                },
                painter = painterResource(id = R.drawable.ic_close_bottom_sheet),
                contentDescription = null
            )
        }
        ConfirmationBottomSheetContent(
            quoteAmount = calculateConfirmationQuoteAmount(
                quoteAmount = viewModel.uiState.quoteAmount.value,
                baseAmount = viewModel.uiState.baseAmount.value,
                currencyPrice = viewModel.uiState.pricesQuoteAndCommissions?.price
            ),
            baseAmount = calculateConfirmationBaseAmount(
                quoteAmount = viewModel.uiState.quoteAmount.value,
                baseAmount = viewModel.uiState.baseAmount.value,
                currencyPrice = viewModel.uiState.pricesQuoteAndCommissions?.price
            ),
            asset = viewModel.asset,
            assetImageUrl = viewModel.assetImageUrl,
            secondsRemaining = viewModel.uiState.remainingTimeText,
            idCurrency = viewModel.idCurrencyAccount,
            isLoading = viewModel.uiState.isLoading,
            exchangeRate = viewModel.uiState.exchangeRate.toCurrencyFormat(
                symbol = CurrencyType.Colon.symbol
            ),
            convertedAmount = calculateConvertedCurrencyBalance(
                quoteAmount = viewModel.uiState.quoteAmount.value,
                baseAmount = viewModel.uiState.baseAmount.value,
                price = viewModel.uiState.pricesQuoteAndCommissions?.price,
                exchangeRate = viewModel.uiState.exchangeRate
            ),
            onConfirm = {
                coroutineScope.launch {
                    modalBottomSheetState.hide()
                }
                viewModel.onUIEvent(SellCurrencyScreenViewModel.UIEvent.OnSellCryptoCurrency)
            }
        )
    }
}