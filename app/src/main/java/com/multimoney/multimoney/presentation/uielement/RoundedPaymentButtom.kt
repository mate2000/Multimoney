package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.Typography

/**
 * PhoneTextField: This PhoneTextField is used to handle phone
 *
 * Parameters:
 * @param modifier: Apply style.
 * @param labelText: Text above the textField to describe its function.
 * @param value: Variable to store the input value.
 * @param defaultCountry: Default country to start phone text field.
 * @param keyboardActions: Actions to take when ime button is click.
 * @param isRequired: Field is required.
 * @param isRequiredMessage: Message to be displayed for required text field
 * @param isError: Display error.
 * @param errorMessage: Error message to be displayed.
 * @param enabled: Enable or Disable field.
 * @param pickedCountry: Function to handle picked country selected.
 * @param dialogAppBarColor: Change select country dialog AppBarColor.
 * @param dialogAppBarTextColor: Change select country dialog AppBarTextColor.
 * @param dialogFocusedBorderColorSearch: Change select country dialog FocusedBorderColorSearch.
 * @param dialogUnFocusedBorderColorSearch: Change select country dialog UnFocusedBorderColorSearch.
 * @param dialogCursorColorSearch: Change select country dialog CursorColorSearch.
 * @param showCountryCode: Show country code.
 * @param showCountryFlag: Show country flag.
 * @param onValueChange: Function to handle input changes.
 * @param onDebounceValidation: Function to handle validations with a debounce of 0.5 seg.
 * **/

@Composable
fun RoundedPaymentButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    strokeBrush: Brush,
    strokeWidth: Dp,
    roundedShapeDp: Dp,
    backgroundColor: Color,
    mainText: String,
    secondaryText: String,
    mainTextColor: Color,
    secondaryTextColor: Color
) {
    Card(
        modifier = modifier
            .clickable {
                onClick()
            },
        border = BorderStroke(
            width = strokeWidth,
            brush = strokeBrush
        ),
        shape = RoundedCornerShape(roundedShapeDp),
        backgroundColor = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = mainText,
                style = Typography.subtitle1.copy(fontWeight = FontWeight.W700),
                color = mainTextColor,
                textAlign = TextAlign.Left

            )
            Text(
                text = secondaryText,
                style = Typography.caption,
                color = secondaryTextColor,
                textAlign = TextAlign.Left
            )
        }
    }
}
