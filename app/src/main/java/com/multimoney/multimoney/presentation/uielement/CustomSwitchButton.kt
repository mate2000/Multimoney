package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.Primary300
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.WhiteTransparency10

/**
 * CustomSwitchButton: Is a toggle button to show if something is turned on or off
 *
 * Parameters:
 * @param modifier: Apply style.
 * @param checked: Apply if is checked the switch or not.
 * @param onCheckedChange: It is to make something when user press the switch button and it pass the current value (True or False).
 */
@Composable
fun CustomSwitchButton(
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    val checkedThumbColor: Color
    val uncheckedThumbColor: Color
    val checkedTrackColor: Color
    val uncheckedTrackColor: Color
    if (isSystemInDarkTheme()) {
        checkedThumbColor = Primary500
        uncheckedThumbColor = DefaultWhite
        checkedTrackColor = Primary300
        uncheckedTrackColor = WhiteTransparency10
    } else {
        checkedThumbColor = Primary500
        uncheckedThumbColor = DefaultWhite
        checkedTrackColor = Primary300
        uncheckedTrackColor = WhiteTransparency10
    }
    Switch(
        modifier = modifier,
        checked = checked,
        onCheckedChange = {
            onCheckedChange(it)
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = checkedThumbColor,
            uncheckedThumbColor = uncheckedThumbColor,
            checkedTrackColor = checkedTrackColor,
            uncheckedTrackColor = uncheckedTrackColor
        )
    )
}
