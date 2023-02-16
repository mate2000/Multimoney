package com.multimoney.multimoney.presentation.ui.crypto

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Notice
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.util.parseApiDateToCardDate

@Composable
@Preview
fun CryptoCurrencyMovementItem(
    cryptoCurrencyMovement: CryptoCurrencyMovement? = CryptoCurrencyMovement(),
    onReleaseTransactionClick: () -> Unit = {},
    isHomeParentView: Boolean = false
) {
    cryptoCurrencyMovement?.let { movement ->
        val icon = if (movement.side == MovementSide.BUY.side)
            R.drawable.ic_gains_crypto else R.drawable.ic_crypto_subtract

        Column {
            if(cryptoCurrencyMovement.held && isHomeParentView){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Notice)
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        style = Typography.body2.copy(color = Notice),
                        text = stringResource(id = R.string.home_product_movement_pending)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MultimoneyTheme.colors.fullTransparency)
                    .height(70.dp)
                    .padding(top = 8.dp, bottom = 1.dp),
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
                                .copy(
                                    color = MultimoneyTheme.colors.text,
                                    fontWeight = FontWeight.Bold
                                ),
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
            if (cryptoCurrencyMovement.held && isHomeParentView){
                OutlinedButton(
                    modifier = Modifier.padding(top = 8.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = Color.Transparent
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MultimoneyTheme.colors.cryptoActionButtonEnable
                    ),
                    onClick = {  }) {
                    Text(
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        style = Typography.button.copy(
                            color = MultimoneyTheme.colors.text
                        ),
                        text = stringResource(id = R.string.home_product_movement_release_button)
                    )
                }
            }
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp), color = MultimoneyTheme.colors.dividerWhite30
        )
    }
}

enum class MovementSide(val side: String) {
    BUY("BUY"),
    SELL("SELL")
}