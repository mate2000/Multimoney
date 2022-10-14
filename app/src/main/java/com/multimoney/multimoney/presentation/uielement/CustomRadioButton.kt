package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.GrayScale400
import com.multimoney.multimoney.presentation.theme.GrayScale500
import com.multimoney.multimoney.presentation.theme.GrayScale800
import com.multimoney.multimoney.presentation.theme.Primary500

/**
 * CustomRadioButton: Selector that forces the user to only pick one option
 *
 * Parameters
 * @param modifier: Dimensions for the row of the radio button.
 * @param radioModifier: Dimensions for radio button.
 * @param text: Header for radio button.
 * @param onOptionSelected: Function that handles selected status.
 * @param selected: Selected button status.
 * */
@Composable
fun CustomRadioButton(
    modifier: Modifier,
    radioModifier: Modifier,
    text: String,
    onOptionSelected: () -> Unit = {},
    selected: Boolean
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var radioSelectedColor = Primary500
        var radioUnSelectedColor = GrayScale500
        var textColor = GrayScale800
        if (isSystemInDarkTheme()) {
            radioSelectedColor = DefaultWhite
            radioUnSelectedColor = GrayScale400
            textColor = DefaultWhite
        }
        RadioButton(
            colors = RadioButtonDefaults.colors(radioSelectedColor, radioUnSelectedColor),
            selected = selected,
            modifier = radioModifier,
            onClick = {
                onOptionSelected()
            }
        )
        Text(
            text = text,
            color = textColor
        )
    }
}