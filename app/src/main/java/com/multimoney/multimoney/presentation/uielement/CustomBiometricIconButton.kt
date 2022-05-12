package com.multimoney.multimoney.presentation.uielement

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

/**
 *
 * CustomBiometricIconButton: This IconButton is used to match design system
 *
 * Parameters
 * @param modifier: Apply style.
 * @param icon: Resource for the center Icon
 * @param tint: Color for the tint of the icon
 * @param onClick: Function to handle the click action
 *
 */
@Composable
fun BiometricIconButton(
    modifier: Modifier,
    icon: Int,
    tint: Color,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "",
            tint = tint,
            modifier = modifier
        )
    }
}