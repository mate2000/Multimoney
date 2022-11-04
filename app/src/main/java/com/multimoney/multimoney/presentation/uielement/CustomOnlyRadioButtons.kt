package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.multimoney.multimoney.presentation.theme.GrayScale500
import com.multimoney.multimoney.presentation.theme.GrayScale800
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.WhiteTransparency70

/**
 * CustomButton: Button to match design style across the whole app, in order to use it.
 *
 * Parameters:
 * @param modifier: Apply style.
 * @param condition: The condition that the buttons are going to change.
 * @param buttonOne: The options for the first button
 *        First value: Label text next to button
 *        Second value: Condition indicating if this button is currently selected
 *        Third value: OnClick lambda action
 * @param buttonTwo: The options for the first button
 *        First value: Label text next to button
 *        Second value: Condition indicating if this button is currently selected
 *        Third value: OnClick lambda action
 */

@Composable
fun CustomOnlyRadioButtons(
    condition: Boolean?,
    buttonOne: Triple<String, Boolean, () -> Unit>,
    buttonTwo: Triple<String, Boolean, () -> Unit>,
    modifier: Modifier = Modifier
) {
    val radioSelectedColor: Color
    val radioUnSelectedColor: Color
    val textColor: Color

    if (isSystemInDarkTheme()) {
        radioSelectedColor = WhiteTransparency70
        radioUnSelectedColor = GrayScale500
        textColor = WhiteTransparency70
    } else {
        radioSelectedColor = Primary500
        radioUnSelectedColor = GrayScale500
        textColor = GrayScale800
    }

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = condition?.equals(buttonOne.second) ?: false,
                onClick = buttonOne.third,
                colors = RadioButtonDefaults.colors(
                    selectedColor = radioSelectedColor,
                    unselectedColor = radioUnSelectedColor
                )
            )
            Text(
                text = buttonOne.first,
                color = textColor
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = condition?.equals(buttonTwo.second) ?: false,
                onClick = buttonTwo.third,
                colors = RadioButtonDefaults.colors(
                    selectedColor = radioSelectedColor,
                    unselectedColor = radioUnSelectedColor
                )
            )
            Text(
                text = buttonTwo.first,
                color = textColor
            )
        }
    }
}