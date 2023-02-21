package com.multimoney.multimoney.presentation.ui.crypto

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency70

@Composable
fun CryptoCurrencyPurchaseItem(
    modifier: Modifier = Modifier,
    imageUrl: String,
    descriptionCurrency: String,
    asset: String,
    priceOfTheDay: String,
    percentageInvestedCurrency: String,
    onClick: () -> Unit
) {
    val gainOrLossColor =
        if (percentageInvestedCurrency.toDouble() < 0) MultimoneyTheme.colors.cryptoLossesColor else MultimoneyTheme.colors.tipActionColor

    Column(modifier = modifier
        .fillMaxWidth()
        .paint(
            painterResource(id = R.drawable.bg_cryptocurrency_enabled),
            contentScale = ContentScale.FillBounds
        )
        .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
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
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(
                        id = R.string.currency_item_description,
                        descriptionCurrency,
                        asset
                    ),
                    style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.titleText
                )

                Row(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Text(
                            text = stringResource(
                                id = R.string.dollar_symbol_value,
                                priceOfTheDay
                            ),
                            style = Typography.caption.copy(fontWeight = FontWeight.SemiBold),
                            color = WhiteTransparency70
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = stringResource(
                                id = R.string.currency_item_percent_invested,
                                percentageInvestedCurrency
                            ),
                            style = Typography.caption,
                            color = gainOrLossColor
                        )
                    }
                }
            }
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_right_chevron),
                contentDescription = null
            )
        }
    }
}
