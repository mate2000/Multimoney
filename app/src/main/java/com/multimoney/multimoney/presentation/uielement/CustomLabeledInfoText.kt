package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme

@Composable
fun CustomLabeledInfoText(
    modifier: Modifier = Modifier,
    icon: Int? = null,
    iconColor: Color = MultimoneyTheme.colors.iconColor,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    label: String,
    text: String,
    labelStyle: TextStyle,
    textStyle: TextStyle,
    labelColor: Color,
    textColor: Color
) {
    Row(
        modifier = modifier,
        verticalAlignment = verticalAlignment
    ) {
        icon?.let {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
        ) {
            Text(
                text = label,
                style = labelStyle,
                color = labelColor
            )
            Text(
                text = text,
                style = textStyle,
                color = textColor
            )
        }
    }
}