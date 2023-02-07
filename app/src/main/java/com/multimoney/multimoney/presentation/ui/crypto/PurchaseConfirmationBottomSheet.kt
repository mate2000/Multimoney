package com.multimoney.multimoney.presentation.ui.crypto

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency.BuyCurrencyScreenViewModel
import com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency.BuyCurrencyTitleConfirmationSectionSkeleton
import com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency.VoucherCurrencyExchangeInfoSkeleton
import com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency.WhileLoadingSection
import com.multimoney.multimoney.presentation.uielement.CurrencyExchangeInfo
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.util.calculateConfirmationBaseAmount
import com.multimoney.multimoney.presentation.util.calculateConfirmationQuoteAmount
import com.multimoney.multimoney.presentation.util.calculateConvertedCurrencyBalance
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.toCurrencyFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

const val WHITE_SPACE = " "
const val SECONDS_SUFFIX = "seg"
const val DEFAULT_AMOUNT = "0.0"
const val MANY_ASSET_LENGTH = 4

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PurchaseConfirmationBottomSheet(
    modalBottomSheetState: ModalBottomSheetState,
    coroutineScope: CoroutineScope,
    viewModel: BuyCurrencyScreenViewModel,
    onConfirm: () -> Unit
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
                        viewModel.onUIEvent(BuyCurrencyScreenViewModel.UIEvent.OnClosePurchaseConfirmationBottomSheet)
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
                viewModel.onUIEvent(BuyCurrencyScreenViewModel.UIEvent.OnPurchaseCryptoCurrency)
                onConfirm()
            }
        )
    }
}

@Composable
@Preview
fun ConfirmationBottomSheetContent(
    quoteAmount: String = DEFAULT_AMOUNT,
    baseAmount: String = DEFAULT_AMOUNT,
    asset: String = "",
    assetImageUrl: String = "",
    secondsRemaining: String = "",
    idCurrency: Int = CurrencyType.Dollar.id,
    isLoading: Boolean = false,
    exchangeRate: String = DEFAULT_AMOUNT,
    convertedAmount: String = DEFAULT_AMOUNT,
    onConfirm: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TitleSection(quoteAmount)
        PurchaseInfoSection(
            assetImageUrl = assetImageUrl,
            isLoading = isLoading,
            asset = asset,
            baseAmount = baseAmount,
            secondsRemaining = secondsRemaining
        )
        AccountInfoSection(idCurrency)
        if (idCurrency == CurrencyType.Colon.id) {
            WhileLoadingSection(
                isLoading = isLoading,
                contentLoading = { VoucherCurrencyExchangeInfoSkeleton() },
                content = {
                    CurrencyExchangeInfo(
                        leftTitleResource = R.string.crypto_purchase_flow_exchange_type_title,
                        rightTitleResource = R.string.crypto_purchase_flow_exchange_total_title,
                        exchangeRateText = exchangeRate,
                        convertedAmountText = convertedAmount,
                        contentColumnAlignment = Alignment.Start,
                        rightColumnWithSpacing = false
                    )
                }
            )
        }
        CustomButton(
            text = stringResource(id = R.string.crypto_purchase_flow_confirmation_btn_buy),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 24.dp),
            enable = true,
            onClick = onConfirm
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun TitleSection(quoteAmount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = quoteAmount,
            style = Typography.h4.copy(
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun PurchaseInfoSection(
    assetImageUrl: String,
    isLoading: Boolean,
    asset: String,
    baseAmount: String,
    secondsRemaining: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Image(
                modifier = Modifier
                    .size(16.dp)
                    .padding(end = 4.dp),
                painter = rememberAsyncImagePainter(model = assetImageUrl),
                contentDescription = null
            )
            WhileLoadingSection(
                isLoading = isLoading,
                contentLoading = { BuyCurrencyTitleConfirmationSectionSkeleton() },
                content = {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.crypto_purchase_flow_confirmation_estimated_amount))
                            append(WHITE_SPACE)
                            append(asset)
                            append(WHITE_SPACE)
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(baseAmount)
                                append(WHITE_SPACE)
                                append(asset)
                            }
                        },
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.bodyTextColor,
                            fontSize = if (asset.length > MANY_ASSET_LENGTH) 13.sp else 14.sp
                        )
                    )
                }
            )
        }
        Text(
            modifier = Modifier.padding(vertical = 4.dp),
            text = buildAnnotatedString {
                append(stringResource(id = R.string.crypto_purchase_flow_price_expires_in))
                append(WHITE_SPACE)
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(secondsRemaining)
                    append(WHITE_SPACE)
                    append(SECONDS_SUFFIX)
                }
            },
            style = Typography.body2.copy(color = MultimoneyTheme.colors.bodyTextColor),
        )
    }
}

@Composable
private fun AccountInfoSection(idCurrency: Int) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.crypto_purchase_flow_confirmation_from_account_title),
            style = Typography.body2.copy(
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.Bold
            )
        )
        CustomInfoButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            startIcon = R.drawable.ic_multimoney_smart,
            endIcon = null,
            title = stringResource(
                id = R.string.crypto_purchase_flow_confirmation_from_account,
                if (idCurrency == CurrencyType.Colon.id) {
                    CurrencyType.Colon.symbol
                } else {
                    CurrencyType.Dollar.symbol
                }
            ),
            subtitle = if (idCurrency == CurrencyType.Colon.id) {
                CurrencyType.Colon.stringName
            } else {
                CurrencyType.Dollar.stringName
            },
            enable = false
        )
    }
}