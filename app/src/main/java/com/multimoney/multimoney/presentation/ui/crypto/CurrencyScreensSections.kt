package com.multimoney.multimoney.presentation.ui.crypto

import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency.BuyCurrencyTitleSectionSkeleton
import com.multimoney.multimoney.presentation.ui.crypto.sell.sellcurrency.TIMER_UNIT_INDICATOR
import com.multimoney.multimoney.presentation.ui.crypto.sell.sellcurrency.WHITE_SPACE
import com.multimoney.multimoney.presentation.uielement.CryptoCurrencyInputLayout
import com.multimoney.multimoney.presentation.util.calculateAssetEstimated
import com.multimoney.multimoney.presentation.util.calculateDollarEstimated
import com.multimoney.multimoney.presentation.util.toCurrencyFormat

@Composable
fun SellDisclaimerSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            modifier = Modifier.padding(end = 8.dp),
            painter = painterResource(id = R.drawable.ic_information),
            contentDescription = null
        )
        Text(
            text = stringResource(id = R.string.crypto_sell_flow_sell_screen_disclaimer),
            style = Typography.body2.copy(
                color = MultimoneyTheme.colors.text
            ),
        )
    }
}

@Composable
fun WhileLoadingSection(
    isLoading: Boolean,
    contentLoading: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    if (isLoading) {
        contentLoading()
    } else {
        content()
    }
}

@Composable
fun CounterSection(
    modifier: Modifier = Modifier,
    downCounter: String,
    smartAccountAvailableBalance: Double,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MultimoneyTheme.colors.bodyTextColor,
                        )
                    ) {
                        append(stringResource(id = R.string.crypto_purchase_flow_available_smart_amount))
                    }
                    append(WHITE_SPACE)
                    withStyle(
                        style = SpanStyle(
                            color = MultimoneyTheme.colors.bodyTextColor,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(smartAccountAvailableBalance.toCurrencyFormat())
                    }
                },
                style = Typography.body2
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MultimoneyTheme.colors.bodyTextColor,
                        )
                    ) {
                        append(stringResource(id = R.string.crypto_purchase_flow_price_expires_in))
                    }
                    append(WHITE_SPACE)
                    withStyle(
                        style = SpanStyle(
                            color = MultimoneyTheme.colors.bodyTextColor,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(downCounter)
                        append(TIMER_UNIT_INDICATOR)
                    }
                },
                style = Typography.body2
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun AmountInputSection(
    modifier: Modifier = Modifier,
    asset: String,
    currencyPrice: Double,
    isError: Boolean = false,
    @StringRes errorText: Int,
    textArg: Any? = null,
    quoteAmount: MutableState<String>,
    baseAmount: MutableState<String>,
    isTransformationCurrency: MutableState<Boolean>,
    keyboardController: SoftwareKeyboardController?,
    focusRequester: FocusRequester,
    onAmountChanged: (String) -> Unit
) {
    val quoteAmountText = remember { quoteAmount }
    val baseAmountText = remember { baseAmount }
    val isTransformationCurrencyValue = remember { isTransformationCurrency }
    val baseOrQuote =
        if (isTransformationCurrencyValue.value.not()) quoteAmountText else baseAmountText

    LaunchedEffect(key1 = true) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CryptoCurrencyInputLayout(
            value = baseOrQuote,
            iconCurrency = asset,
            focusRequester = focusRequester,
            isError = isError,
            errorText = getTextFromStringRes(textRes = errorText, arg = textArg),
            isTransformationCurrency = isTransformationCurrencyValue,
            onValueChanged = onAmountChanged,
            onImeClick = { keyboardController?.hide() }
        )
        Text(
            text = if (isTransformationCurrencyValue.value.not()) {
                stringResource(
                    id = R.string.crypto_purchase_flow_exchange_reference_edittext,
                    calculateAssetEstimated(
                        quoteAmount = quoteAmountText.value,
                        currencyPrice = currencyPrice
                    ),
                    asset
                )
            } else {
                stringResource(
                    id = R.string.crypto_purchase_flow_exchange_reference_edittext_dollars,
                    calculateDollarEstimated(
                        baseAmount = baseAmountText.value,
                        currencyPrice = currencyPrice
                    )
                )
            },
            style = Typography.body2.copy(
                color = MultimoneyTheme.colors.bodyTextColor
            )
        )
    }
}

@Composable
private fun getTextFromStringRes(@StringRes textRes: Int, arg: Any? = null): String {
    return if (arg != null) {
        stringResource(id = textRes, arg)
    } else {
        stringResource(id = textRes)
    }
}

@Composable
fun TitleSection(
    @StringRes title: Int,
    cryptoAssetExchange: String,
    modifier: Modifier = Modifier,
    imageUrl: String,
    isLoading: Boolean,
    showSellDisclaimer: Boolean = false
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = stringResource(id = title),
                style = Typography.h6.copy(
                    color = MultimoneyTheme.colors.text,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        if (showSellDisclaimer) {
            SellDisclaimerSection()
        }
        WhileLoadingSection(
            isLoading = isLoading,
            contentLoading = { BuyCurrencyTitleSectionSkeleton() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = cryptoAssetExchange,
                    style = Typography.subtitle1.copy(
                        color = MultimoneyTheme.colors.text
                    )
                )
            }
        }
    }
}