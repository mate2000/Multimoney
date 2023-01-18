package com.multimoney.multimoney.presentation.ui.crypto

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.util.toCurrencyFormat

@Composable
fun MarketCurrencyItem(
    modifier: Modifier = Modifier,
    imageUrl: String,
    descriptionCurrency: String,
    asset: String,
    percentChange: Double,
    currentPrice: Double,
    onCurrencyItemClick: () -> Unit,
    amountChange: String
) {
    val amountChangeValue = amountChange.toDouble()
    val gainOrLoss = if (amountChangeValue < 0) {
        stringResource(id = R.string.crypto_losses_symbol)
    } else {
        stringResource(id = R.string.crypto_gains_symbol)
    }
    val gainOrLossColor =
        if (amountChangeValue < 0) MultimoneyTheme.colors.cryptoLossesColor else MultimoneyTheme.colors.cryptoGainsColor

    Column {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(72.dp)
                .clickable(onClick = onCurrencyItemClick)
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.bg_cryptocurrency_enabled),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Image(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.CenterVertically),
                        painter = rememberAsyncImagePainter(model = imageUrl),
                        contentDescription = null
                    )
                    Column(
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = descriptionCurrency,
                            style = Typography.body2,
                            color = MultimoneyTheme.colors.labelText
                        )
                        Text(
                            text = stringResource(
                                id = R.string.currency_item_inside_parenthesis,
                                asset
                            ),
                            style = Typography.body2,
                            color = MultimoneyTheme.colors.textSubhead
                        )
                    }
                }
                Row {
                    Column(
                        modifier = Modifier.wrapContentWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = currentPrice.toCurrencyFormat(),
                            style = Typography.caption,
                            color = MultimoneyTheme.colors.text
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = stringResource(
                                id = R.string.currency_item_percent_invested_with_symbol,
                                gainOrLoss,
                                percentChange
                            ),
                            style = Typography.caption,
                            color = gainOrLossColor
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
    }
}