package com.multimoney.multimoney.presentation.ui.crypto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.util.parseApiDateToCardDate

@Composable
fun CryptoCurrencyMovementItem(
    cryptoCurrencyMovement: CryptoCurrencyMovement?
) {
    cryptoCurrencyMovement?.let { movement ->
        val icon = if (movement.side == MovementSide.BUY.side)
            R.drawable.ic_gains_crypto else R.drawable.ic_crypto_subtract

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MultimoneyTheme.colors.fullTransparency)
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = movement.descriptionMovement,
                    style = Typography.subtitle2.copy(color = MultimoneyTheme.colors.text),
                    textAlign = TextAlign.Start
                )
                Text(
                    text = parseApiDateToCardDate(movement.createdAt),
                    style = Typography.body2.copy(color = MultimoneyTheme.colors.textSubhead),
                    textAlign = TextAlign.Start
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        tint = if (movement.side == MovementSide.BUY.side) MultimoneyTheme.colors.cryptoGainsColor else
                            MultimoneyTheme.colors.cryptoLossesColor,
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(
                            id = R.string.currency_item_dollar_symbol,
                            movement.quoteAmount
                        ),
                        style = Typography.subtitle1
                            .copy(color = MultimoneyTheme.colors.text, fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.End
                    )
                }
                Text(
                    text = "${movement.amountFilled} ${movement.abbreviationCurrency}",
                    style = Typography.body2.copy(color = MultimoneyTheme.colors.textSubhead),
                    textAlign = TextAlign.End
                )
            }
        }
        Divider(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), color = MultimoneyTheme.colors.dividerWhite30)
    }
}

enum class MovementSide(val side: String) {
    BUY("BUY"),
    SELL("SELL")
}