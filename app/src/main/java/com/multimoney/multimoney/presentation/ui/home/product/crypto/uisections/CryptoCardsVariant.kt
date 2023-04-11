package com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.multimoney.domain.model.crypto.HistoricalBalanceClient
import com.multimoney.domain.model.security.Wording
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.BlackTransparency16
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.graphics.HomeCryptoGraphic
import com.multimoney.multimoney.presentation.uielement.BalanceTextView
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomInformativeChip
import com.multimoney.multimoney.presentation.util.calculateGainLoses
import com.multimoney.multimoney.presentation.util.toCurrencyFormat
import com.multimoney.multimoney.presentation.util.toCurrencyFormatWithoutNegatives

@Composable
fun CryptoCardDiscoverCrypto(
    wording: Wording?,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 12.dp, start = 24.dp, end = 24.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick.invoke() },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        CustomInformativeChip(
            text = stringResource(id = R.string.home_smart_in_process_crypto_card),
            textStyle = Typography.body2.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            ),
            modifier = Modifier.padding(top = 12.dp),
            shape = RoundedCornerShape(12.dp),
            background = MultimoneyTheme.colors.chipBackground
        )
        Text(
            text = wording?.textOne ?: "",
            modifier = Modifier.padding(top = 14.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.creditNotApprovedText
        )
        Text(
            text = wording?.textTwo ?: "",
            modifier = Modifier.padding(top = 8.dp),
            style = Typography.caption,
            color = MultimoneyTheme.colors.text
        )
    }
}

@Composable
fun CryptoCardSmartInProcess(
    wording: Wording?,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick.invoke() }
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 24.dp)
    ) {
        CustomInformativeChip(
            text = stringResource(id = R.string.home_smart_in_process_crypto_card),
            textStyle = Typography.body2.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            ),
            modifier = Modifier.padding(top = 24.dp),
            shape = RoundedCornerShape(12.dp),
            background = MultimoneyTheme.colors.chipBackground,
            startIcon = R.drawable.ic_time
        )
        Text(
            text = wording?.textOne ?: "",
            modifier = Modifier.padding(top = 14.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        Text(
            text = wording?.textTwo ?: "",
            modifier = Modifier.padding(top = 8.dp, bottom = 96.dp),
            style = Typography.caption,
            color = MultimoneyTheme.colors.text
        )
    }
}

@Composable
fun CryptoCardMaintenanceState(
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick.invoke() }
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 24.dp)
    ) {
        CustomInformativeChip(
            text = stringResource(id = R.string.home_smart_in_process_crypto_card),
            textStyle = Typography.body2.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            ),
            modifier = Modifier.padding(top = 24.dp),
            shape = RoundedCornerShape(12.dp),
            background = MultimoneyTheme.colors.chipBackground
        )
        Text(
            text = stringResource(id = R.string.home_maintenance_state_title),
            modifier = Modifier.padding(top = 14.dp),
            style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text
        )
        Text(
            text = stringResource(id = R.string.home_maintenance_state_description),
            modifier = Modifier.padding(top = 8.dp, bottom = 96.dp),
            style = Typography.caption,
            color = MultimoneyTheme.colors.text
        )
    }
}

@Composable
fun CryptoCardWithBalance(
    cryptoBalance: Double,
    clientCryptoBalanceHistory: List<HistoricalBalanceClient> = emptyList(),
    isEmptyStateDisable: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                top = 24.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = if (isEmptyStateDisable.not()) 136.dp else 0.dp
            )
    ) {
        Text(
            text = stringResource(id = R.string.home_crypto_card_with_balance_title),
            style = Typography.body1.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            )
        )
        BalanceTextView(
            modifier = Modifier,
            balanceText = cryptoBalance.toCurrencyFormat(),
            currencyStyle = Typography.h4.copy(
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.Bold
            ),
            currencyDecimalStyle = Typography.body2.copy(
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.Bold
            )
        )

        if (isEmptyStateDisable) {
            val isInGainOrLoss = calculateGainLoses(cryptoBalance, clientCryptoBalanceHistory) >= 0
            val graphicColor = if (isInGainOrLoss) {
                MultimoneyTheme.colors.cryptoGainsColor
            } else MultimoneyTheme.colors.cryptoLossesColor

            HomeCryptoGraphic(
                clientCryptoBalanceHistory = clientCryptoBalanceHistory,
                graphicColor = graphicColor
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                CustomInformativeChip(
                    text = stringResource(
                        id = R.string.currency_item_dollar_symbol,
                        calculateGainLoses(
                            cryptoBalance,
                            clientCryptoBalanceHistory
                        ).toCurrencyFormatWithoutNegatives()
                    ),
                    textStyle = Typography.body2.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MultimoneyTheme.colors.text
                    ),
                    startIconTint = graphicColor,
                    shape = RoundedCornerShape(12.dp),
                    background = BlackTransparency16,
                    startIcon = if (isInGainOrLoss) R.drawable.ic_gains_crypto else R.drawable.ic_crypto_subtract
                )
            }
        }
    }
}
