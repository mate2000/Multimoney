package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency70

/**
 * CustomLabelDescColumn: Label description text to match design style across the whole app, in order to use it.
 *
 * Parameters:
 * @param modifier: Apply style.
 * @param labelText: Text to display label.
 * @param descriptionText: Text to display description.
 */

@Composable
@Preview
fun CustomLabelDescColumn(
    modifier: Modifier = Modifier,
    labelText: String = "",
    descriptionText: String = ""
) {
    Column(modifier = modifier) {
        Text(
            text = labelText,
            style = Typography.subtitle2.copy(color = WhiteTransparency70)
        )
        Text(
            text = descriptionText,
            style = Typography.subtitle2.copy(
                color = DefaultWhite,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
