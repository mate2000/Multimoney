package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.ComplementaryBlack
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency50
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90

/**
 * ScheduleAutomaticPaymentTextInfo: This component is used to show scheduled payment info in home*
 * Parameters:
 * @param titleResource: Title component resource
 * @param subtitleResource: Subtitle component resource
 * @param dateText: Next payment date
 * @param chipLeadingIconResource: This icon depends on the type of the chip
 * @param amountText: Next payment amount
 * @param threePointsOnClick: This will happen when user perform click on three points icon
 * @param chipOnClick: This will happen when user perform click on chip
 */

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScheduleAutomaticPaymentTextInfo(
    titleResource: Int = R.string.schedule_automatic_payment_credit_subtitle,
    subtitleResource: Int = R.string.schedule_automatic_payment_credit_next_payment,
    dateText: String = "",
    chipLeadingIconResource: Int? = null,
    amountText: String = "",
    threePointsOnClick: () -> Unit = {},
    chipOnClick: () -> Unit = {}
) {
    val titleColor: Color
    val nextPaymentTextColor: Color
    val chipBackgroundColor: Color
    val contentChipColor: Color
    val dateColor: Color
    if (isSystemInDarkTheme()) {
        titleColor = WhiteTransparency90
        nextPaymentTextColor = WhiteTransparency50
        chipBackgroundColor = ComplementaryBlack
        contentChipColor = DefaultWhite
        dateColor = DefaultWhite
    } else {
        titleColor = WhiteTransparency90
        nextPaymentTextColor = WhiteTransparency50
        chipBackgroundColor = ComplementaryBlack
        contentChipColor = DefaultWhite
        dateColor = DefaultWhite
    }
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = titleResource),
                style = Typography.subtitle2.copy(fontWeight = FontWeight.SemiBold),
                color = titleColor
            )
            Image(
                modifier = Modifier.clickable {
                    threePointsOnClick()
                },
                painter = painterResource(id = R.drawable.ic_option_points),
                contentDescription = ""
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.padding(top = 4.dp)) {
                Text(
                    text = stringResource(id = subtitleResource),
                    style = Typography.body2,
                    color = nextPaymentTextColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = dateText,
                    style = Typography.body2.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = dateColor
                )
            }
            Row {
                Chip(
                    enabled = true,
                    colors = ChipDefaults.chipColors(
                        backgroundColor = chipBackgroundColor
                    ),
                    modifier = Modifier
                        .height(36.dp),
                    leadingIcon =
                    if (chipLeadingIconResource != null) {
                        {
                            Image(
                                modifier = Modifier
                                    .size(18.dp),
                                painter = painterResource(id = chipLeadingIconResource),
                                contentDescription = ""
                            )
                        }
                    } else {
                        null
                    },
                    onClick = chipOnClick,
                    content = {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = amountText,
                                style = Typography.body2.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = contentChipColor
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                )
            }
        }
    }
}
