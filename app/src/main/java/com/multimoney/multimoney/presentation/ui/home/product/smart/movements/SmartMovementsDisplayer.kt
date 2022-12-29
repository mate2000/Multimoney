package com.multimoney.multimoney.presentation.ui.home.product.smart.movements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.accountsmart.SmartMovement
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.parseApiDateToCardDate

@Composable
fun SmartMovementDisplayer(
    move: SmartMovement
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        horizontalArrangement = SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val currencySymbol = if (move.currencyDescription == API_COLONES) {
            Brand.CostaRica.id.getCurrencySymbol()
        } else {
            Brand.ElSalvador.id.getCurrencySymbol()
        }

        Column(Modifier.weight(2f)) {
            Text(
                text = move.transactionCatalogueDescription ?: "",
                style = Typography.subtitle2.copy(
                    color = MultimoneyTheme.colors.labelText
                ),
                maxLines = 1
            )
            Text(
                text = parseApiDateToCardDate(move.creationDate),
                style = Typography.body2.copy(
                    color = MultimoneyTheme.colors.textSubhead
                ),
                maxLines = 1
            )
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(
                    if (move.amount > 0) R.drawable.ic_plus else R.drawable.ic_minus
                ),
                contentDescription = "",
                tint = Color.Unspecified,
                modifier = Modifier.padding(end = 4.dp, bottom = 6.dp)
            )
            Text(
                text = stringResource(currencySymbol) + move.amount.toString()
                    .removePrefix(MINUS_SIGN),
                style = Typography.subtitle1.copy(
                    textAlign = TextAlign.End,
                    color = MultimoneyTheme.colors.labelText,
                    fontWeight = FontWeight.W600
                ),
                maxLines = 1
            )
        }
    }
    Divider(
        color = MultimoneyTheme.colors.dividerWhite30,
        modifier = Modifier.padding(top = 8.dp)
    )
}

const val MINUS_SIGN = "-"
const val API_COLONES = "COLONES"
