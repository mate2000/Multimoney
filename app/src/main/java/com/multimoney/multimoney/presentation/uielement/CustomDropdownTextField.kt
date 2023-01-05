package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.extension.findActivity
import com.multimoney.multimoney.presentation.theme.GrayScale500
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency60
import com.multimoney.multimoney.presentation.util.gesture.detectTapAndPressUnconsumed

/**
 * It's a dropdown text field that can be used in place of the standard text field
 *
 * @param modifier: Apply style
 * @param labelText: Label Text Resource
 * @param value: The value of the text field
 * @param placeHolder: The value of the placeHolder
 * @param isError: Display error
 * @param errorMessage: Error message to be displayed
 * @param enabled: Enable or Disable field.
 * @param onValueChange: Function to handle input changes.
 * @param onSelectionChange: Function to handle Dropdown selection changes.
 * @param optionList: List Value to use in Dropdown
 * @param optionSelected: The selected item from the dropdown list
 * @param customTransformation: VisualTransformation mask to apply in input
 * @param onDebounceValidation: Function to handle validations with a debounce of 0.5 seg.
 */
@Preview
@Composable
fun CustomDropdownTextField(
    modifier: Modifier = Modifier,
    labelText: String? = null,
    value: String? = null,
    placeHolder: String = "",
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    onValueChange: (newText: String) -> Unit = {},
    onSelectionChange: (newSelection: String, index: Int) -> Unit = { _: String, _: Int -> },
    optionList: List<String> = listOf(),
    optionSelected: String = "",
    customTransformation: VisualTransformation? = null,
    onDebounceValidation: (newText: String) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val activity = context.findActivity()

    val iconTintColor: Color
    val selectedItemColor: Color

    if (isSystemInDarkTheme()) {
        selectedItemColor = WhiteTransparency60
        iconTintColor = WhiteTransparency60
    } else {
        selectedItemColor = GrayScale500
        iconTintColor = GrayScale500
    }

    Column(
        modifier = modifier
    ) {
        CustomOutlinedTextField(
            modifier = modifier.onGloballyPositioned { coordinates ->
                textFieldSize = coordinates.size.toSize()
            },
            labelText = labelText,
            value = value,
            leadingIconComposable = {
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(end = 15.dp)
                        .clickable { expanded = !expanded }) {
                    Text(text = optionSelected,
                        modifier = Modifier.padding(start = 16.dp, end = 4.dp),
                        color = selectedItemColor,
                        style = Typography.body2)
                    Icon(
                        painter = painterResource(id = R.drawable.ic_down_arrow_menu),
                        contentDescription = optionSelected,
                        tint = iconTintColor
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            placeHolder = placeHolder,
            errorMessage = errorMessage,
            customTransformation = customTransformation,
            onDebounceValidation = onDebounceValidation,
            onValueChange = onValueChange,
            isError = isError,
            enabled = enabled
        )

        CustomHighlightDropdown(
            expanded = expanded,
            items = optionList,
            onValueChange = onSelectionChange,
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                .pointerInput(Unit) {
                    detectTapAndPressUnconsumed(onTap = {
                        activity?.onUserInteraction()
                    })
                },
            onDismissRequest = { expanded = false }
        )
    }
}
