package com.multimoney.multimoney.presentation.ui.crypto

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.SemanticNegative400
import com.multimoney.multimoney.presentation.theme.SemanticPositive400
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency60
import com.multimoney.multimoney.presentation.util.roundToEightDecimalPlaces
import com.multimoney.multimoney.presentation.util.toCurrencyFormat

@Composable
fun CurrencyItem(
    modifier: Modifier = Modifier,
    imageUrl: String,
    descriptionCurrency: String,
    asset: String,
    balanceDollars: Double,
    priceOfTheDay: Double,
    percentageInvestedCurrency: String,
    available: Double,
    onClick: () -> Unit = {}
) {
    Column {
        Column(
            modifier
                .paint(
                    painterResource(id = R.drawable.bg_cryptocurrency_enabled),
                    contentScale = ContentScale.FillBounds
                )
                .clickable { onClick() }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.CenterVertically),
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = null
                )
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.currency_item_description,
                                descriptionCurrency,
                                asset
                            ),
                            style = Typography.body2,
                            color = MultimoneyTheme.colors.labelText
                        )
                        Text(
                            text = balanceDollars.toCurrencyFormat(),
                            style = Typography.body2,
                            color = MultimoneyTheme.colors.labelText
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val gainOrLoss = percentageInvestedCurrency.contains(stringResource(id = R.string.crypto_losses_symbol))
                        val symbol = if (gainOrLoss) "" else stringResource(id = R.string.crypto_gains_symbol)
                        Row {
                            Text(
                                text = priceOfTheDay.toCurrencyFormat(),
                                style = Typography.caption,
                                color = WhiteTransparency60
                            )
                            Text(
                                modifier = Modifier.padding(start = 8.dp),
                                text = stringResource(
                                    id = R.string.currency_item_percent_invested,
                                    "${symbol}${percentageInvestedCurrency}"
                                ),
                                style = Typography.caption,
                                color = if (gainOrLoss) SemanticNegative400 else SemanticPositive400
                            )
                        }
                        Text(
                            text = "${available.roundToEightDecimalPlaces()} $asset",
                            style = Typography.caption,
                            color = WhiteTransparency60
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
    }
}
