package com.multimoney.multimoney.presentation.ui.home.product.smart.uisections

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.security.Wording
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.BlackTransparency16
import com.multimoney.multimoney.presentation.theme.BlackTransparency20
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency10
import com.multimoney.multimoney.presentation.uielement.BalanceTextView
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomInformativeChip
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
    cTA: String = "",
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 12.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick.invoke() }
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(BlackTransparency16),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomImage(
                modifier = Modifier
                    .padding(start = 12.dp, end = 6.dp),
                drawableResource = R.drawable.ic_alert_outlined
            )
            Text(
                modifier = Modifier
                    .padding(end = 12.dp, top = 4.dp),
                text = stringResource(id = R.string.smart_card_smart_title),
                style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.text
            )
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
    profitTotal: Double? = 0.0,
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

@Composable
@Preview
fun CardWithSmartInProcess(
    type: SmartProcessStarted = SmartProcessStarted.SmartProcessOnfidoReject,
    idBrand: Int = Brand.ElSalvador.id,
    action: () -> Unit = {},
    wording: Wording? = Wording("", "", "")
) {
    val notDefinedValue = stringResource(id = R.string.not_defined)
    var chipText = R.string.home_product_process_smart_label
    val title: String = wording?.textOne?.filter { wording.textOne != notDefinedValue } ?: ""
    val description: String = wording?.textTwo?.filter { wording.textTwo != notDefinedValue } ?: ""
    val actionText: String? = wording?.cTA?.filter { wording.textTwo != notDefinedValue }
    var startIcon = R.drawable.ic_time

    val backgroundShip: Color = if (isSystemInDarkTheme()) {
        BlackTransparency20
    } else {
        WhiteTransparency10
    }
    when (type) {
        SmartProcessStarted.SmartStartProcessIncomplete -> {
            chipText = R.string.smart_card_smart_title
            startIcon = R.drawable.ic_warning
        }
        SmartProcessStarted.SmartProcessOnFidoIncomplete -> {
            startIcon = R.drawable.ic_warning
        }
        SmartProcessStarted.SmartProcessOnfidoReject -> {
            startIcon = R.drawable.ic_warning
        }
        SmartProcessStarted.CreditProcessCreateAccountFailure -> {
            startIcon = R.drawable.ic_warning
        }
        else -> Unit
    }

    Column(
        modifier = Modifier
            .padding(top = 12.dp, start = 24.dp, end = 24.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                action.invoke()
            }
    ) {
        CustomInformativeChip(
            text = stringResource(id = chipText),
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
            text = title,
            modifier = Modifier.padding(top = 14.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        Text(
            text = description,
            modifier = Modifier.padding(top = 8.dp),
            style = Typography.caption,
            color = MultimoneyTheme.colors.text
        )
        actionText?.let {
            if (it.isNotBlank()) {
                CustomImage(
                    modifier = Modifier
                        .padding(top = 21.dp)
                        .align(Alignment.CenterHorizontally),
                    drawableResource = R.drawable.ic_chevron_up
                )

                Text(
                    text = actionText,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .align(Alignment.CenterHorizontally),
                    style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.text,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

sealed class SmartProcessStarted {
    object SmartStartProcessIncomplete : SmartProcessStarted()
    object SmartProcessOnFidoIncomplete : SmartProcessStarted()
    object CreditProcessFirmIncomplete : SmartProcessStarted()
    object CreditProcessFirmReject : SmartProcessStarted()
    object CreditProcessFirmMaxAttempts : SmartProcessStarted()
    object SmartProcessOnfidoReject : SmartProcessStarted()
    object SmartProcessOnfidoMaxAttempts : SmartProcessStarted()
    object CreditProcessCreateAccountFailure : SmartProcessStarted()
}
