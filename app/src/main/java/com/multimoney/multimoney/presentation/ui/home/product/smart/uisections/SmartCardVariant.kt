package com.multimoney.multimoney.presentation.ui.home.product.smart.uisections

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.BlackTransparency16
import com.multimoney.multimoney.presentation.theme.BlackTransparency20
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency10
import com.multimoney.multimoney.presentation.uielement.BalanceTextView
import com.multimoney.multimoney.presentation.uielement.CustomInformativeChip
import com.multimoney.multimoney.presentation.util.SPACE
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.toCurrencyFormat

/**
 * Composable function to show the different statuses of an inactive smart account
 */
@Composable
@Preview
fun CardInactiveSmartProduct(
    textOne: String = "",
    textTwo: String = "",
    type: SmartProcessStarted? = SmartProcessStarted.SmartInitialProcess
) {
    val backgroundShip: Color = if (isSystemInDarkTheme()) {
        BlackTransparency20
    } else {
        WhiteTransparency10
    }
    val startIcon = getSmartCardStartIcon(type)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 12.dp, start = 24.dp, end = 24.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        CustomInformativeChip(
            text = stringResource(id = R.string.smart_card_smart_title),
            textStyle = Typography.body2.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            ),
            modifier = Modifier.padding(top = 12.dp),
            shape = RoundedCornerShape(12.dp),
            background = backgroundShip,
            startIcon = startIcon,
            startIconTint = MultimoneyTheme.colors.iconColor
        )
        Text(
            text = textOne,
            modifier = Modifier.padding(top = 14.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        Text(
            text = textTwo,
            modifier = Modifier.padding(top = 8.dp),
            style = Typography.caption,
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
    profitTotal: Double? = 0.0,
    profitMonthly: Double? = 0.0,
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
        BalanceTextView(
            modifier = Modifier.padding(bottom = 10.dp),
            balanceText = profitTotal?.toCurrencyFormat(
                stringResource(id = currency.getCurrencySymbol())
            ) ?: "",
            currencyStyle = Typography.h4.copy(
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.Bold
            ),
            currencyDecimalStyle = Typography.body2.copy(
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.Bold
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
                        painter = painterResource(id = R.drawable.ic_mm_add),
                        contentDescription = null,
                        tint = MultimoneyTheme.colors.smartCardPlus
                    )
                    Text(
                        text = stringResource(
                            id = R.string.smart_card_monthly_profit_label,
                            profitMonthly?.toCurrencyFormat(
                                stringResource(id = currency.getCurrencySymbol()).plus(
                                    SPACE
                                )
                            ) ?: "",
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
                painter = painterResource(id = R.drawable.ic_mm_trending),
                contentDescription = null
            )
        }
    }
}

@Composable
@Preview
fun CardSmartFirmedAndOnfidoPending() {
    val backgroundShip: Color = if (isSystemInDarkTheme()) {
        BlackTransparency20
    } else {
        WhiteTransparency10
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 12.dp, start = 24.dp, end = 24.dp)
    ) {
        CustomInformativeChip(
            text = stringResource(id = R.string.home_my_products_title_smart),
            textStyle = Typography.body2.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            ),
            modifier = Modifier.padding(top = 12.dp),
            shape = RoundedCornerShape(12.dp),
            background = backgroundShip,
            startIcon = R.drawable.ic_warning
        )
        Text(
            text = stringResource(id = R.string.smart_continue_validating_identity_title),
            modifier = Modifier.padding(top = 14.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        Text(
            text = stringResource(id = R.string.home_smart_product_process_accept_contract_description),
            modifier = Modifier.padding(top = 8.dp, bottom = 73.dp),
            style = Typography.caption,
            color = MultimoneyTheme.colors.text
        )
    }
}

fun getSmartCardStartIcon(type: SmartProcessStarted?) = when (type) {
    SmartProcessStarted.SmartInitialProcess -> 0
    SmartProcessStarted.SmartStartProcessIncomplete,
    SmartProcessStarted.SmartProcessFirmIncomplete -> R.drawable.ic_time
    SmartProcessStarted.SmartProcessOnFidoIncomplete,
    SmartProcessStarted.SmartProcessOnfidoReject -> R.drawable.ic_warning
    else -> R.drawable.ic_warning
}

sealed class SmartProcessStarted {
    object SmartInitialProcess : SmartProcessStarted()
    object SmartStartProcessIncomplete : SmartProcessStarted()
    object SmartProcessOnFidoIncomplete : SmartProcessStarted()
    object SmartProcessFirmIncomplete : SmartProcessStarted()
    object SmartProcessOnfidoReject : SmartProcessStarted()
}
