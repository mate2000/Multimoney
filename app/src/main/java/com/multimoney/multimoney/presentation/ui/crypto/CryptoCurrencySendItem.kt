package com.multimoney.multimoney.presentation.ui.crypto

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency60
import com.multimoney.multimoney.presentation.theme.WhiteTransparency70
import com.multimoney.multimoney.presentation.util.toCurrencyFormat

@Preview(showSystemUi = false)
@Composable
fun CryptoCurrencySendItemPreview() {
    MultimoneyTheme {
        CryptoCurrencySendItem(
            imageUrl = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png?1547033579",
            descriptionCurrency = "Bitcoin",
            asset = "BTC",
            balanceDollars = 100.0,
            available = 100.0,
            onClick = {}
        )
    }
}

@Composable
fun CryptoCurrencySendItem(
    modifier: Modifier = Modifier,
    imageUrl: String,
    descriptionCurrency: String,
    asset: String,
    balanceDollars: Double,
    available: Double,
    onClick: () -> Unit = {}
) {
    Column(
        modifier
            .paint(
                painterResource(id = R.drawable.bg_cryptocurrency_enabled),
                contentScale = ContentScale.FillBounds
            )
            .clickable { onClick() }) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
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
                    .weight(1f)
                    .padding(start = 16.dp)
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
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = balanceDollars.toCurrencyFormat(),
                        style = Typography.caption.copy(fontWeight = FontWeight.SemiBold),
                        color = WhiteTransparency70
                    )
                    Text(
                        text = "$available $asset",
                        style = Typography.caption,
                        color = WhiteTransparency60
                    )
                }
            }
            Image(
                painter = painterResource(id = R.drawable.ic_right_chevron),
                contentDescription = null
            )
        }
    }
}