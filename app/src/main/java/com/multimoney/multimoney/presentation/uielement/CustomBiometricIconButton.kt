package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.Primary200
import com.multimoney.multimoney.presentation.theme.Primary300
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.WhiteTransparency10

/**
 *
 * CustomBiometricIconButton: This IconButton is used to match design system
 *
 * Parameters
 * @param modifier: Apply style.
 * @param padding: Value for the inner padding
 * @param icon: Resource for the center Icon
 * @param onClick: Function to handle the click action
 *
 */
@Composable
fun CustomBiometricIconButton(
    modifier: Modifier,
    padding: Dp,
    icon: Int,
    onClick: () -> Unit
) {

    val tintColor: Color
    val backgroundColor: Color
    val borderColor: Color
    if (isSystemInDarkTheme()) {
        tintColor = Primary200
        backgroundColor = WhiteTransparency10
        borderColor = Transparent
    } else {
        tintColor = DefaultWhite
        backgroundColor = Primary500
        borderColor = Primary300
    }

    IconButton(
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "",
            tint = tintColor,
            modifier = modifier
                .clip(CircleShape)
                .background(backgroundColor)
                .border(1.dp, borderColor, CircleShape)
                .padding(padding)
        )
    }
}