package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency50
import com.multimoney.multimoney.presentation.theme.WhiteTransparency70

/**
 * CustomLabelDescRow: Label description text to match design style across the whole app, in order to use it.
 *
 * Parameters:
 * @param modifier: Apply style.
 * @param labelText: Text to display label.
 * @param descriptionText: Text to display description.
 * @param endIcon: Icon at the end of the row.
 * @param endIconClick: Action to execute when user clicks end icon.
 */

@Composable
@Preview
fun CustomLabelDescRow(
    modifier: Modifier = Modifier,
    labelText: String = "",
    descriptionText: String = "",
    endIcon: Int = 0,
    endIconClick: () -> Unit = {}
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(0.85f).fillMaxWidth()) {
                Text(
                    text = labelText,
                    style = Typography.subtitle2.copy(color = WhiteTransparency70)
                )
                Text(
                    text = descriptionText,
                    style = Typography.subtitle2.copy(
                        color = DefaultWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            if (endIcon != 0) {
                Row(
                    modifier = Modifier.weight(0.15f).fillMaxWidth().align(Alignment.CenterVertically).clickable {
                        endIconClick()
                    },
                    horizontalArrangement = Arrangement.End
                ) {
                    Image(
                        modifier = Modifier.wrapContentSize().padding(end = 12.dp),
                        painter = painterResource(endIcon),
                        contentDescription = "",
                        contentScale = ContentScale.Inside
                    )
                }
            }
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(1.dp),
            color = WhiteTransparency50
        )
    }
}
