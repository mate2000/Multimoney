package com.multimoney.multimoney.presentation.ui.home.product.smart.uisections

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.BlackTransparency16
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.getCurrencySymbolValue


/**
 * Composable function to show the different statuses of an inactive smart account
 */
@Composable
@Preview
fun CardInactiveSmartProduct(
    textOne : String = "",
    textTwo : String = "",
    cTA: String = "",
    onClick :() -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 12.dp)
            .clickable { onClick.invoke() }
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(BlackTransparency16),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            CustomImage(
                modifier = Modifier
                    .padding(start = 12.dp, end = 6.dp),
                drawableResource = R.drawable.ic_alert_outlined
            )
            Text(
                modifier = Modifier
                    .padding(end = 12.dp,top = 4.dp),
                text = stringResource(id = R.string.smart_card_smart_title),
                style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.text)
        }
        Text(
            text = textOne,
            modifier = Modifier.padding(top = 12.dp),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText
        )
        Text(
            text = textTwo,
            modifier = Modifier.padding(top = 4.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        CustomImage(
            modifier = Modifier
                .padding(top = 20.dp)
                .align(Alignment.CenterHorizontally),
            drawableResource = R.drawable.ic_chevron_up
        )
        Text(
            text = cTA,
            modifier = Modifier

                .align(Alignment.CenterHorizontally),
            style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
    }
}

/**
 * Composable function to show the option of an active smart product
 */
@Composable
@Preview
fun CardSmartProduct(
    currency: String = "",
    profitTotal: String = "",
    profitMonthly: String = "",
    currentMonth: String = ""
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 12.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.smart_card_balance),
            modifier = Modifier.padding(top = 14.dp),
            style = Typography.body1.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            )
        )
        Text(
            text = stringResource(id = currency.getCurrencySymbolValue(), profitTotal),
            modifier = Modifier.padding(bottom = 10.dp),
            style = Typography.h4.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(BlackTransparency16),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(
                    vertical = 8.dp,
                    horizontal = 12.dp
                )
            ) {
                Text(
                    text = stringResource(id = R.string.smart_card_monthly_profit),
                    modifier = Modifier.padding(top = 4.dp),
                    style = Typography.subtitle2.copy(color = MultimoneyTheme.colors.text)
                )
                Row {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = MultimoneyTheme.colors.smartCardPlus
                    )
                    Text(
                        text = stringResource(
                            id = R.string.smart_card_monthly_profit_label,
                            stringResource(id = currency.getCurrencySymbol()),
                            profitMonthly,
                            currentMonth
                        ),
                        modifier = Modifier.padding(start = 4.dp),
                        style = Typography.body2.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MultimoneyTheme.colors.text
                        )
                    )
                }
            }
            Icon(
                modifier = Modifier.padding(end = 12.dp),
                tint = MultimoneyTheme.colors.smartCardTrending,
                imageVector = Icons.Filled.TrendingUp,
                contentDescription = null
            )
        }
    }
}
