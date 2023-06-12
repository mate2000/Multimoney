package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency5
import com.multimoney.multimoney.presentation.util.toTwoChar

/**
 * CustomButton: Button to match design style across the whole app, in order to use it.
 *
 * Parameters:
 * @param modifier: Surface Modifier
 * @param name: Titular title.
 * @param color: Icon color.
 */

@Composable
fun CustomContactIcon(
    name: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val background: Color = if (isSystemInDarkTheme()) {
        Color.Transparent
    } else {
        Color.Transparent
    }

    Surface(
        shape = CircleShape,
        modifier = modifier.size(48.dp),
        border = BorderStroke(1.dp, MultimoneyTheme.colors.dividerWhite30),
        color = background
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = name.toTwoChar(),
                style = Typography.h6.copy(
                    fontWeight = FontWeight.Bold,
                    baselineShift = BaselineShift(-0.2f)
                ),
                color = color
            )
        }
    }
}
