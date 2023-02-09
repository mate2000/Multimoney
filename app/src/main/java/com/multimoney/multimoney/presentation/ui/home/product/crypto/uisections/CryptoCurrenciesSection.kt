package com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.domain.model.balance.BalanceCryptoAccountItems
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.CurrencyItem
import com.multimoney.multimoney.presentation.util.MAX_CRYPTO_ITEMS

@Composable
fun CryptoCurrencies(
    items: List<BalanceCryptoAccountItems>?,
    itemClick: (BalanceCryptoAccountItems) -> Unit,
    viewAllClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                textAlign = TextAlign.Start,
                text = stringResource(id = R.string.crypto_currencies),
                style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText
            )
            TextButton(onClick = { viewAllClick() }) {
                Text(
                    textAlign = TextAlign.End,
                    text = stringResource(id = R.string.crypto_currencies_see_all),
                    style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.textLink
                )
            }
        }
        items?.take(MAX_CRYPTO_ITEMS)?.forEach { item ->
            CurrencyItem(
                imageUrl = item.url_image ?: "",
                descriptionCurrency = item.descriptionCurrency ?: "",
                asset = item.asset ?: "",
                balanceDollars = item.balanceDollars ?: 0.0,
                priceOfTheDay = item.priceOfTheDay ?: 0.0,
                percentageInvestedCurrency = item.percentageInvestedCurrency ?: "",
                available = item.available ?: 0.0,
                onClick = { itemClick(item) }
            )
        }
    }
}
