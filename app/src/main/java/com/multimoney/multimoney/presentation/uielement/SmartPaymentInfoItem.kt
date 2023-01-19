package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography

/**
 * CustomDialog: This Dialog is used to match design system
 *
 * Parameters:
 * @param modifier: Modifier for the general row.
 * @param verticalAlignment: Vertical Alignment between icon and the title/subtitle
 * @param icon: Info icon resource
 * @param iconTint: Info icon tint color
 * @param iconModifier: icon modifier
 * @param title: Left side title string value
 * @param subtitle: Left side subtitle string value
 * @param rightTitle: Right side title string value
 * @param rightSubtitle: Right side subtitle string value
 * @param showVerticalDivision: Show vertical line to separate left from right content
 */

@Composable
fun SmartPaymentInfoItem(
    modifier: Modifier = Modifier,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    icon: Int? = null,
    iconTint: Color = Color.Unspecified,
    iconModifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    rightTitle: String? = null,
    rightSubtitle: String? = null,
    showVerticalDivision: Boolean = false
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .padding(start = 24.dp, top = 24.dp, end = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = verticalAlignment
        ) {
            icon?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = "",
                    tint = iconTint,
                    modifier = iconModifier
                        .height(24.dp)
                        .width(24.dp)
                )
            }
            Column(modifier = Modifier.padding(start = 14.dp)) {
                Text(
                    text = title,
                    style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.labelText
                )
                if (subtitle.isNullOrBlank().not()) {
                    Text(
                        text = subtitle.orEmpty(),
                        style = Typography.body2,
                        color = MultimoneyTheme.colors.labelText
                    )
                }
            }
        }
        if (showVerticalDivision) {
            Box(
                Modifier.fillMaxHeight()
                    .width(1.dp)
                    .background(color = MultimoneyTheme.colors.dividerWhite40)
            )
        }
        Column(modifier = Modifier.padding(end = 14.dp)) {
            if (rightTitle.isNullOrBlank().not()) {
                Text(
                    text = rightTitle.orEmpty(),
                    style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.labelText
                )
            }
            if (rightSubtitle.isNullOrBlank().not()) {
                Text(
                    text = rightSubtitle.orEmpty(),
                    style = Typography.body2,
                    color = MultimoneyTheme.colors.labelText
                )
            }
        }
    }
}
