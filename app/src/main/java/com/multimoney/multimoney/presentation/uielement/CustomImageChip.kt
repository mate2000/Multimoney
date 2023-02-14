package com.multimoney.multimoney.presentation.uielement

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.Shapes

@Composable
fun CustomImageChip(
    @DrawableRes iconId: Int,
    isSelected: Boolean,
    onChecked: (Boolean) -> Unit,
    background: Color,
    tintColor: Color
) {
    Box(
        modifier = Modifier.padding(top = 16.dp)
            .clip(CircleShape)
            .background(background)
            .clickable { }
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    vertical = 0.dp,
                    horizontal = 2.dp
                )
                .background(
                    color = if (isSelected) Transparent else Transparent,
                    shape = Shapes.small
                )
                .clip(shape = Shapes.small)
                .clickable {
                    onChecked(!isSelected)
                }
                .padding(start = 5.dp, end = 5.dp)
        ) {
            Icon(
                painter = painterResource(id = iconId),
                tint = if (isSelected) tintColor else tintColor,
                contentDescription = null
            )
        }
    }
}