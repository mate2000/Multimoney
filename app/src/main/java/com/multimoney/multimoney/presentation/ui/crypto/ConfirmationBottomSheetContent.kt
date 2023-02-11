package com.multimoney.multimoney.presentation.ui.crypto

import androidx.compose.foundation.Image
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
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
import com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency.DEFAULT_AMOUNT
import com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency.MANY_ASSET_LENGTH
import com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency.SECONDS_SUFFIX
import com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency.WHITE_SPACE
import com.multimoney.multimoney.presentation.uielement.CurrencyExchangeInfo
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType

/**
 * ConfirmationBottomSheetContent
 *
 * params:
 * @param amount - number to display in the title section
 * @param sellExchangeRate - boolean to determine if the exchange title rate is for sell or buy
 * @param evaluatedAmount - AnnotatedString to display amount exchange respect to the currency
 * @param showAssetImage - boolean to determine if the asset image should be displayed
 * @param showTotalToReceive - boolean to determine if the total to receive section should be displayed
 * @param showBottomExchangeInfo - boolean to determine if the bottom exchange info section should be displayed
 * @param amountToReceive - number to display in the total to receive section
 * @param ibanAccountNumber - number to display in the account info section
 * @param buttonText - text to display in the button
 * @param asset - text of the crypto currency asset
 * @param assetImageUrl - url of the crypto currency asset image
 * @param secondsRemaining - number of seconds to display in the time remaining section
 * @param idCurrency - id of the currency to determine if dollars or colones are being used
 * @param isLoading - boolean to determine if the loading skeleton should be displayed
 * @param exchangeRate - number to display the exchange rate of the currency
 * @param convertedAmount - number to display the converted currency amount
 * @param onConfirm - function to execute when the button is clicked
 *
 * **/

@Composable
@Preview
fun ConfirmationBottomSheetContent(
    amount: String = DEFAULT_AMOUNT,
    sellExchangeRate: Boolean = false,
    evaluatedAmount: AnnotatedString = AnnotatedString(""),
    showAssetImage: Boolean = true,
    showTotalToReceive: Boolean = false,
    showBottomExchangeInfo: Boolean = false,
    amountToReceive: String = DEFAULT_AMOUNT,
    ibanAccountNumber: String = "",
    buttonText: String = "",
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
        TitleSection(amount)
        InfoSection(
            evaluatedAmount = evaluatedAmount,
            assetImageUrl = assetImageUrl,
            showTotalToReceive = showTotalToReceive,
            amountToReceive = if (idCurrency == CurrencyType.Colon.id) {
                convertedAmount
            } else {
                amountToReceive
            },
            exchangeRate = exchangeRate,
            isLoading = isLoading,
            asset = asset,
            secondsRemaining = secondsRemaining,
            showAssetImage = showAssetImage,
            idCurrency = idCurrency
        )
        AccountInfoSection(
            idCurrency = idCurrency,
            ibanAccountNumber = ibanAccountNumber
        )
        if (showBottomExchangeInfo) {
            WhileLoadingSection(
                isLoading = isLoading,
                contentLoading = { VoucherCurrencyExchangeInfoSkeleton() },
                content = {
                    CurrencyExchangeInfo(
                        leftTitleResource = R.string.crypto_purchase_flow_exchange_type_title,
                        rightTitleResource = if(sellExchangeRate.not()) {
                            R.string.crypto_sell_flow_confirmation_sell_screen_amount_to_receive
                        } else {
                            R.string.crypto_purchase_flow_exchange_total_title
                        },
                        exchangeRateText = exchangeRate,
                        convertedAmountText = convertedAmount,
                        contentColumnAlignment = Alignment.Start,
                        rightColumnWithSpacing = false
                    )
                }
            )
        }
        CustomButton(
            text = buttonText,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 24.dp),
            enable = isLoading.not(),
            onClick = onConfirm
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun TitleSection(amount: String) {
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
            text = amount,
            style = Typography.h4.copy(
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun InfoSection(
    evaluatedAmount: AnnotatedString,
    assetImageUrl: String,
    isLoading: Boolean,
    asset: String,
    secondsRemaining: String,
    showAssetImage: Boolean = true,
    showTotalToReceive: Boolean = false,
    idCurrency: Int = CurrencyType.Dollar.id,
    amountToReceive: String,
    exchangeRate: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            if (showAssetImage) {
                Image(
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp),
                    painter = rememberAsyncImagePainter(model = assetImageUrl),
                    contentDescription = null
                )
            }
            WhileLoadingSection(
                isLoading = isLoading,
                contentLoading = { CurrencyTitleConfirmationSectionSkeleton() },
                content = {
                    Text(
                        text = evaluatedAmount,
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
        if (showTotalToReceive) {
            Spacer(modifier = Modifier.height(24.dp))
            if (idCurrency == CurrencyType.Colon.id) {
                Text(
                    modifier = Modifier.padding(vertical = 4.dp),
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.crypto_purchase_flow_exchange_type_title))
                        append(WHITE_SPACE)
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(exchangeRate)
                        }
                    },
                    style = Typography.body2.copy(color = MultimoneyTheme.colors.bodyTextColor),
                )
            }
            Text(
                modifier = Modifier.padding(vertical = 4.dp),
                text = buildAnnotatedString {
                    append(stringResource(id = R.string.crypto_sell_flow_confirmation_sell_screen_amount_to_receive))
                    append(WHITE_SPACE)
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(amountToReceive)
                    }
                },
                style = Typography.body2.copy(color = MultimoneyTheme.colors.bodyTextColor),
            )
        }
    }
}

@Composable
private fun AccountInfoSection(
    idCurrency: Int,
    ibanAccountNumber: String
) {
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
            subtitle = ibanAccountNumber,
            enable = false
        )
    }
}