package com.multimoney.multimoney.presentation.ui.crypto.currencydetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.multimoney.domain.model.crypto.Item
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency30
import com.multimoney.multimoney.presentation.util.parseApiDateToCardDate
import com.multimoney.multimoney.presentation.util.roundToTwoDecimalPlaces

@Composable
fun CryptoMovementItem(item: Item) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = item.descriptionMovement,
                style = Typography.body2.copy(color = MultimoneyTheme.colors.labelText)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                val numberSymbolRes =
                    if (item.base_amount.contains('-')) R.drawable.ic_plus else R.drawable.ic_minus
                Icon(
                    modifier = Modifier.size(14.dp),
                    imageVector = ImageVector.vectorResource(id = numberSymbolRes),
                    contentDescription = null
                )
                Text(
                    text = stringResource(
                        id = R.string.currency_item_dollar_symbol,
                        item.base_amount
                    ),
                    style = Typography.body2.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MultimoneyTheme.colors.labelText
                    )
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = parseApiDateToCardDate(item.created_at),
                style = Typography.body2,
                color = MultimoneyTheme.colors.labelText.copy(alpha = 0.5f)
            )
            Text(
                text = "${
                    item.amount_filled.toDouble().roundToTwoDecimalPlaces()
                } ${item.abbreviationCurrency}",
                style = Typography.body2,
                color = MultimoneyTheme.colors.labelText.copy(alpha = 0.5f)
            )
        }
        Divider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = WhiteTransparency30
        )
    }
}
