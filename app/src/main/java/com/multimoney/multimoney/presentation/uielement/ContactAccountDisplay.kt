package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType

/**
 * Params:
 * @param currency: CurrencyType of the account
 * @param maskedAccountNumber: String of the account number masked
 * @param onClick: Action to call on click
 */

@Composable
fun ContactAccountDisplay(
    currency: CurrencyType,
    maskedAccountNumber: String,
    onClick: () -> Unit
) {
    Row(
        Modifier.padding(top = 24.dp)
            .clickable { onClick() }
    ) {
        Icon(
            painter = painterResource(currency.feeIcon),
            tint = Color.Unspecified,
            contentDescription = ""
        )
        Column(Modifier.padding(start = 16.dp)) {
            Text(
                text = stringResource(currency.myAccountSmartName),
                style = Typography.body1.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MultimoneyTheme.colors.text
                )
            )
            Text(
                text = maskedAccountNumber,
                style = Typography.body1.copy(
                    color = MultimoneyTheme.colors.subTitleText
                )
            )
        }
    }
}
