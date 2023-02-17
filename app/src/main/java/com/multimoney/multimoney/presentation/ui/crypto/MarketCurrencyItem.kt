package com.multimoney.multimoney.presentation.ui.crypto

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
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
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.util.toCurrencyFormat

@Composable
fun MarketCurrencyItem(
    modifier: Modifier = Modifier,
    imageUrl: String,
    descriptionCurrency: String,
    asset: String,
    percentChange: String,
    currentPrice: Double,
    onCurrencyItemClick: () -> Unit,
    amountChange: String
) {
    val amountChangeValue = amountChange.toDouble()
    val gainOrLossColor =
        if (amountChangeValue < 0) MultimoneyTheme.colors.cryptoLossesColor else MultimoneyTheme.colors.cryptoGainsColor

    Column(
        modifier = modifier
            .fillMaxWidth()
            .paint(
                painterResource(id = R.drawable.bg_cryptocurrency_enabled),
                contentScale = ContentScale.FillBounds
            )
            .clickable { onCurrencyItemClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    modifier = Modifier
                        .size(40.dp),
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = null
                )
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(start = 16.dp),
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
            Column(
                modifier = Modifier.wrapContentWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = currentPrice.toCurrencyFormat(),
                    style = Typography.caption,
                    color = MultimoneyTheme.colors.text
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = percentChange,
                    style = Typography.caption,
                    color = gainOrLossColor
                )
            }
        }
    }
}
