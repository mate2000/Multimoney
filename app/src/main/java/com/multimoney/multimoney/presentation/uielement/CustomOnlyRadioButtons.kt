package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.multimoney.multimoney.presentation.theme.GrayScale500
import com.multimoney.multimoney.presentation.theme.GrayScale800
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.WhiteTransparency70

@Composable
fun CustomOnlyRadioButtons(
    condition: Boolean?,
    optionsOne: Triple<String, Boolean, () -> Unit>,
    optionsTwo: Triple<String, Boolean, () -> Unit>
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

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = condition?.equals(optionsOne.second) ?: false,
                onClick = optionsOne.third,
                colors = RadioButtonDefaults.colors(
                    selectedColor = radioSelectedColor,
                    unselectedColor = radioUnSelectedColor
                )
            )
            Text(
                text = optionsOne.first,
                color = textColor
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = condition?.equals(optionsTwo.second) ?: false,
                onClick = optionsTwo.third,
                colors = RadioButtonDefaults.colors(
                    selectedColor = radioSelectedColor,
                    unselectedColor = radioUnSelectedColor
                )
            )
            Text(
                text = optionsTwo.first,
                color = textColor
            )
        }
    }
}