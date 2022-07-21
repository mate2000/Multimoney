package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.DefaultWhite

@OptIn(ExperimentalTextApi::class)
@Composable
fun CustomInformativeChip(
    text: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    background: Color = Color.Transparent,
    startIcon: Int = 0,
    startIconTint: Color = DefaultWhite,
    endIcon: Int = 0,
    endIconTint: Color = DefaultWhite
) {

    val startPadding: Dp = calculatePadding(startIcon)
    val endPadding: Dp = calculatePadding(endIcon)

    Box(
        modifier = modifier
            .clip(shape)
            .background(background),
    ) {
        Row(
            modifier = Modifier.padding(start = startPadding, end = endPadding, top = 3.dp, bottom = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (startIcon != 0) {
                Icon(
                    painter = painterResource(id = startIcon),
                    contentDescription = "",
                    tint = startIconTint
                )
            }
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 8.dp),
                style = textStyle.copy(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
            )
            if (endIcon != 0) {
                Icon(
                    painter = painterResource(id = endIcon),
                    contentDescription = "",
                    tint = endIconTint
                )
            }
        }
    }
}

fun calculatePadding(icon: Int): Dp {
    return if (icon > 0) {
        12.dp
    } else {
        4.dp
    }
}