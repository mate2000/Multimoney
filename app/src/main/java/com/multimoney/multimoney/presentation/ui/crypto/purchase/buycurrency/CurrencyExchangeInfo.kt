package com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90

@Composable
fun CurrencyExchangeInfo(
    leftTitleResource: Int = R.string.empty,
    rightTitleResource: Int = R.string.empty,
    exchangeRateText: String = "",
    convertedAmountText: String = "",
    contentColumnAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    rightColumnWithSpacing: Boolean = true
) {
    val titleColor: Color
    val subtitleColor: Color

    if (isSystemInDarkTheme()) {
        titleColor = WhiteTransparency90
        subtitleColor = DefaultWhite
    } else {
        titleColor = WhiteTransparency90
        subtitleColor = DefaultWhite
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            horizontalAlignment = contentColumnAlignment
        ) {
            Text(
                text = stringResource(id = leftTitleResource),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = titleColor,
                textAlign = TextAlign.Start
            )
            Text(
                text = exchangeRateText,
                style = Typography.body2,
                color = subtitleColor,
                textAlign = TextAlign.Start
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Divider(
            modifier = Modifier
                .height(44.dp)
                .width(1.dp),
            color = MultimoneyTheme.colors.bottomNavigationDividerColor
        )
        if (rightColumnWithSpacing) Spacer(modifier = Modifier.width(8.dp))
        Column(
            horizontalAlignment = contentColumnAlignment
        ) {
            Text(
                text = stringResource(id = rightTitleResource),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = titleColor,
                textAlign = TextAlign.Start
            )
            Text(
                text = convertedAmountText,
                style = Typography.body2,
                color = subtitleColor,
                textAlign = TextAlign.Start
            )
        }
    }
}