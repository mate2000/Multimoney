package com.multimoney.multimoney.presentation.ui.crypto

import androidx.compose.foundation.Image
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
import com.multimoney.multimoney.presentation.theme.SemanticPositive400
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency60

@Composable
fun CurrencyItem(
    modifier: Modifier = Modifier,
    imageUrl: String,
    descriptionCurrency: String,
    asset: String,
    balanceDollars: Double,
    priceOfTheDay: Double,
    percentageInvestedCurrency: String,
    available: Double
) {
    Column {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(72.dp)
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
                            text = stringResource(
                                id = R.string.currency_item_dollar_symbol,
                                balanceDollars
                            ),
                            style = Typography.body2,
                            color = MultimoneyTheme.colors.labelText
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            Text(
                                text = stringResource(
                                    id = R.string.currency_item_dollar_symbol,
                                    priceOfTheDay
                                ),
                                style = Typography.caption,
                                color = WhiteTransparency60
                            )
                            Text(
                                modifier = Modifier.padding(start = 8.dp),
                                text = stringResource(
                                    id = R.string.currency_item_percent_invested,
                                    percentageInvestedCurrency
                                ),
                                style = Typography.caption,
                                color = SemanticPositive400
                            )
                        }
                        Text(
                            text = "$available $asset",
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
