package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.GrayScale300
import com.multimoney.multimoney.presentation.theme.GrayScale400
import com.multimoney.multimoney.presentation.theme.GrayScale500
import com.multimoney.multimoney.presentation.theme.GrayScale600
import com.multimoney.multimoney.presentation.theme.GrayScale800
import com.multimoney.multimoney.presentation.theme.Primary400
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.SemanticNegative500
import com.multimoney.multimoney.presentation.theme.Typography

/**
 * CustomOutlinedTextField: This OutlinedTextField is used to match design system
 *
 * Parameters:
 * @param modifier: Apply style.
 * @param labelText: Text above the textField to describe its function.
 * @param value: Variable to store the input value.
 * @param leadingIcon: Landing icon to display, by default there is no icon.
 * @param trailingIcon: Trailing icon to display, by default there is no icon.
 * @param placeHolder: Hint for the textField.
 * @param keyboardOptions: Settings for textField input.
 * @param keyboardActions: Actions to take when ime button is click.
 * @param isRequired: Field is required.
 * @param isError: Display error.
 * @param errorMessage: Error message to be displayed.
 * @param enabled: Enable or Disable field.
 * @param isPassword: Enable password behavior.
 * @param onValueChange: Function to handle input changes.
 * **/
@Composable
fun CustomOutlinedTextField(
    modifier: Modifier = Modifier,
    labelText: String? = null,
    value: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    placeHolder: String = "",
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    isRequired: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    isPassword: Boolean = false,
    onValueChange: (newText: String) -> Unit = {}
) {
    var emptyError by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // Set colors depending on system theme
    val labelColor: Color
    var backgroundColor: Color
    val iconTintColor: Color
    val textColor: Color

    if (isSystemInDarkTheme()) {
        labelColor = GrayScale300
        backgroundColor = GrayScale600
        when {
            isError -> {
                iconTintColor = SemanticNegative500
                textColor = DefaultWhite
            }
            enabled -> {
                iconTintColor = Primary400
                textColor = DefaultWhite
            }
            else -> {
                backgroundColor = GrayScale500
                iconTintColor = GrayScale400
                textColor = GrayScale400
            }
        }
    } else {
        labelColor = GrayScale500
        backgroundColor = DefaultWhite
        when {
            isError -> {
                iconTintColor = SemanticNegative500
                textColor = GrayScale800
            }
            enabled -> {
                iconTintColor = Primary400
                textColor = GrayScale800
            }
            else -> {
                backgroundColor = GrayScale300
                iconTintColor = GrayScale500
                textColor = GrayScale500
            }
        }
    }

    Column(modifier = modifier) {

        // Display label is it isn't null
        labelText?.let {
            Text(
                text = labelText,
                color = labelColor,
                style = Typography.body2
            )
        }

        // Display textField
        OutlinedTextField(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            value = value ?: "",
            shape = RoundedCornerShape(25),
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = "",
                        tint = iconTintColor
                    )
                }
            },
            trailingIcon = if (isPassword) {
                {
                    val image = if (passwordVisible) {
                        painterResource(id = R.drawable.ic_view_off)
                    } else {
                        painterResource(id = R.drawable.ic_view)
                    }

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = image,
                            contentDescription = "",
                            tint = iconTintColor
                        )
                    }
                }
            } else {
                trailingIcon?.let {
                    {
                        Icon(
                            imageVector = it,
                            contentDescription = "",
                            tint = iconTintColor
                        )
                    }
                }
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            onValueChange = {
                onValueChange(it)
                if (isRequired) emptyError = it.isEmpty()
            },
            placeholder = {
                Text(
                    text = placeHolder,
                    color = GrayScale500,
                    style = Typography.body2
                )
            },
            isError = isError,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = backgroundColor,
                focusedIndicatorColor = Primary500,
                unfocusedIndicatorColor = GrayScale400,
                errorIndicatorColor = SemanticNegative500,
                textColor = textColor
            ),
            enabled = enabled,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            textStyle = Typography.body2
        )

        // Display error message
        errorMessage?.let {
            if (isError || emptyError) {
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_exclamation_mark),
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        contentDescription = "",
                        tint = SemanticNegative500
                    )
                    Text(
                        text = if (emptyError) stringResource(id = R.string.error_empty_field) else errorMessage,
                        color = SemanticNegative500,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                        style = Typography.caption
                    )
                }
            }
        }
    }
}