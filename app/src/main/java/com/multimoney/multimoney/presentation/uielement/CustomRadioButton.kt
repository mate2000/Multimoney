package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
            radioSelectedColor = Primary500
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

/**
 * CustomRadioButtonsLayout: Layout to show radiobutton groups depending on the orientation
 *
 * @param modifier: Modifier applied to container
 * @param options: List of options to be displayed in the radio button
 * @param orientation: Orientation of the layout
 * @param optionSelected: Option chosen, this is used to mark an option as chosen on initial load
 * @param onOptionSelected: Function that return the selected value
 */
@Composable
fun CustomRadioButtonsLayout(
    modifier: Modifier = Modifier,
    options: List<String>,
    orientation: Orientation = Orientation.Vertical,
    optionSelected: String? = null,
    onOptionSelected: (String) -> Unit
) {
    val selectedOption = remember { mutableStateOf(optionSelected ?: "") }

    if (orientation == Orientation.Vertical) {
        Column(modifier = modifier) {
            options.forEach { text ->
                CustomRadioButton(
                    modifier = Modifier,
                    radioModifier = Modifier,
                    text = text,
                    selected = (text == selectedOption.value),
                    onOptionSelected = {
                        selectedOption.value = text
                        onOptionSelected(text)
                    }
                )
            }
        }
    } else {
        Row(modifier = modifier) {
            options.forEach { text ->
                CustomRadioButton(
                    modifier = Modifier,
                    radioModifier = Modifier,
                    text = text,
                    selected = (text == selectedOption.value),
                    onOptionSelected = {
                        selectedOption.value = text
                        onOptionSelected(text)
                    }
                )
            }
        }
    }
}
