package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
 * @param placeHolder: Hint for the textField.
 * @param keyboardOptions: Settings for textField input.
 * @param keyboardActions: Actions to take when ime button is click.
 * @param isRequired: Field is required.
 * @param isRequiredMessage: Message to be displayed for required text field
 * @param isError: Display error.
 * @param errorMessage: Error message to be displayed.
 * @param enabled: Enable or Disable field.
 * @param onValueChange: Function to handle input changes.
 * @param onDebounceValidation: Function to handle validations with a debounce of 0.5 seg.
 * **/

@OptIn(
    ExperimentalFoundationApi::class, kotlinx.coroutines.FlowPreview::class,
    kotlinx.coroutines.ExperimentalCoroutinesApi::class
)
@Composable
@Preview
fun CustomSlider(
    modifier: Modifier = Modifier,
    value: Float = 0F,
    valueRange: Float = 100F,
    minimumLabel: String? = "2,000,000",//null,
    maximumLabel: String? = "10,000,000",//null,
    onValueChange: (value: Float) -> Unit = {},
    onValueChangeFinished: (value: Float) -> Unit = {}
) {
    Column(modifier = modifier) {
        ColorfulIconSlider(
            value = value,
            onValueChange = { progressValue, offset ->
                onValueChange(progressValue)
            },
            onValueChangeFinished = {},
            trackHeight = 10.dp,
            colors = MaterialSliderDefaults.materialColors(
                inactiveTrackColor = SliderBrushColor(color = WhiteTransparency10),
                activeTrackColor = SliderBrushColor(
                    brush = Brush.linearGradient(
                        colors = listOf(Secondary500, Primary400),
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
                Text(text = it, color = Color.White, style = Typography.subtitle2)
            }
            maximumLabel?.let {
                Text(text = it, color = Color.White, style = Typography.subtitle2)
            }
        }
    }
}