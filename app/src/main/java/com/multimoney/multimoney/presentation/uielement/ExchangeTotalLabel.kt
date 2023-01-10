package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.Primary400
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90
import com.multimoney.multimoney.presentation.util.addTextStyleToTextPortion

/**
 * ExchangeTotalLabel: is used for displaying the total converted amount for a transaction to different currencies accounts
 *
 * Parameters
 * @param totalConverted: the already converted amount to display
 */

@Composable
fun ExchangeTotalLabel(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    totalConverted: String,
    showIcon: Boolean = true
) {
    val labelColor: Color
    val iconColor: Color

    if (isSystemInDarkTheme()) {
        labelColor = WhiteTransparency90
        iconColor = Primary400
    } else {
        labelColor = WhiteTransparency90
        iconColor = Primary400
    }

    val fullText = stringResource(
        id = R.string.smart_payment_exchange_equivalent,
        totalConverted
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showIcon) {
            Icon(
                painter = painterResource(id = R.drawable.ic_renew),
                contentDescription = "",
                tint = iconColor
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = fullText.addTextStyleToTextPortion(
                style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                textToStyle = totalConverted
            ),
            style = Typography.body2,
            color = labelColor
        )
    }
}
