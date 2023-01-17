package com.multimoney.multimoney.presentation.ui.credit.movements

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multimoney.domain.model.credit.CreditMovement
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.util.BAR_DIVIDER_FORMAT_YEAR_TWO_DIGITS
import com.multimoney.multimoney.presentation.util.getCardDateFormat

@Composable
fun CreditMovementItem(
    move: CreditMovement
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        horizontalArrangement = SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(2f)) {
            Text(
                text = move.description ?: "",
                style = Typography.body1.copy(
                    color = MultimoneyTheme.colors.labelText, lineHeight = 24.sp
                ),
                maxLines = 1
            )
            Text(
                text = getCardDateFormat(move.date, BAR_DIVIDER_FORMAT_YEAR_TWO_DIGITS),
                style = Typography.body2.copy(
                    color = MultimoneyTheme.colors.labelText.copy(alpha = 0.5f), lineHeight = 24.sp
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
                    if (move.amount?.contains(MINUS_SIGN) == true) R.drawable.ic_minus else R.drawable.ic_plus
                ),
                contentDescription = "",
                tint = Color.Unspecified,
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                text = move.amountLabel?.removePrefix(MINUS_SIGN) ?: "",
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
