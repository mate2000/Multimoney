package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.BlackTransparency10
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.GrayScale700
import com.multimoney.multimoney.presentation.theme.Primary400
import com.multimoney.multimoney.presentation.theme.Secondary500
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency10
import com.smarttoolfactory.slider.ColorfulIconSlider
import com.smarttoolfactory.slider.MaterialSliderDefaults
import com.smarttoolfactory.slider.SliderBrushColor

/**
 * CustomOutlinedTextField: This OutlinedTextField is used to match design system
 *
 * Parameters:
 * @param modifier: Apply style.
 * @param value: Variable to store the input value.
 * @param valueRangeInitial: Initial value to start slider.
 * @param valueRangeFinal: Final value to end slider.
 * @param minimumLabel: Label to be displayed down start.
 * @param maximumLabel: Label to be displayed down end.
 * @param onValueChange: Execute on value change.
 * @param onValueChangeFinished: Execute on value change finished.
 * **/

@OptIn(
    ExperimentalFoundationApi::class, kotlinx.coroutines.FlowPreview::class,
    kotlinx.coroutines.ExperimentalCoroutinesApi::class
)
@Composable
@Preview
fun CustomSlider(
    modifier: Modifier = Modifier,
    value: Float = 100F,
    valueRangeInitial: Float = 1f,
    valueRangeFinal: Float = 1f,
    minimumLabel: String? = null,
    maximumLabel: String? = null,
    onValueChange: (value: Float) -> Unit = {},
    onValueChangeFinished: () -> Unit = {}
) {

    // Set colors depending on system theme
    val textColor: Color
    val inactiveTrackColor: Color

    if (isSystemInDarkTheme()) {
        textColor = DefaultWhite
        inactiveTrackColor = WhiteTransparency10
    } else {
        textColor = DefaultWhite
        inactiveTrackColor = WhiteTransparency10
    }

    Column(modifier = modifier) {
        ColorfulIconSlider(
            value = value,
            onValueChange = { progressValue, offset ->
                onValueChange(progressValue)
            },
            onValueChangeFinished = { onValueChangeFinished() },
            valueRange = valueRangeInitial..valueRangeFinal,
            trackHeight = 10.dp,
            colors = MaterialSliderDefaults.materialColors(
                inactiveTrackColor = SliderBrushColor(color = inactiveTrackColor),
                activeTrackColor = SliderBrushColor(
                    brush = Brush.linearGradient(
                        colors = listOf(Primary400, Secondary500),
                        start = Offset(0f, Float.POSITIVE_INFINITY),
                        end = Offset(Float.POSITIVE_INFINITY, 0f)
                    ),
                )
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_slider_thumb),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            minimumLabel?.let {
                Text(text = it, color = textColor, style = Typography.subtitle2)
            }
            maximumLabel?.let {
                Text(text = it, color = textColor, style = Typography.subtitle2)
            }
        }
    }
}