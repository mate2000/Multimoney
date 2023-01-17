package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.DefaultBlack
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.GrayScale400
import com.multimoney.multimoney.presentation.theme.GrayScale500
import com.multimoney.multimoney.presentation.theme.GrayScale800
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency30

/**
 * CustomCheckBox: This CheckBox is used to match design system
 *
 * Parameters:
 * @param modifier: Apply style.
 * @param text: Text next to checkbox.
 * @param checked: Variable to store the checked value.
 * @param enabled: Enable or Disable field.
 * @param onCheckedChange: Function to handle input changes.
 * **/
@Composable
@Preview
fun CustomCheckBox(
    modifier: Modifier = Modifier,
    text: String? = null,
    isTextStart: Boolean = false,
    checked: Boolean = false,
    enabled: Boolean = true,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    onCheckedChange: (newValue: Boolean) -> Unit = {}
) {

    // Set colors depending on system theme
    val textColor: Color
    val checkedColor: Color
    val uncheckedColor: Color
    val checkmarkColor: Color
    val disabledColor: Color
    val disabledIndeterminateColor: Color

    if (isSystemInDarkTheme()) {
        textColor = DefaultWhite
        checkedColor = Primary500
        uncheckedColor = GrayScale400
        checkmarkColor = DefaultBlack
        disabledColor = WhiteTransparency30
        disabledIndeterminateColor = DefaultWhite
    } else {
        textColor = GrayScale800
        checkedColor = Primary500
        uncheckedColor = GrayScale500
        checkmarkColor = DefaultWhite
        disabledColor = GrayScale400
        disabledIndeterminateColor = Primary500
    }

    Row(modifier = modifier, horizontalArrangement = horizontalArrangement) {
        if (isTextStart) {
            // Add text next to checkbox
            text?.let {
                Text(
                    text = text,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 11.dp),
                    style = Typography.subtitle2,
                    color = textColor
                )
            }
        }
        Checkbox(
            checked = checked,
            enabled = enabled,
            onCheckedChange = { onCheckedChange(it) },
            colors = CheckboxDefaults.colors(
                checkedColor = checkedColor,
                uncheckedColor = uncheckedColor,
                checkmarkColor = checkmarkColor,
                disabledColor = disabledColor,
                disabledIndeterminateColor = disabledIndeterminateColor
            ),
            modifier = Modifier
                .wrapContentSize()
                .padding(0.dp)
        )
        if (!isTextStart) {
            // Add text next to checkbox
            text?.let {
                ClickableText(
                    text = AnnotatedString(text),
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    style = Typography.subtitle2.copy(
                        color = textColor
                    ),
                    onClick = {
                        onCheckedChange(!checked)
                    }
                )
            }
        }
    }
}