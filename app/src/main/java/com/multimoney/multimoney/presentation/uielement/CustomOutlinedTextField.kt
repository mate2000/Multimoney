package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
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
import com.multimoney.multimoney.presentation.theme.WhiteTransparency60
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

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
 * @param isRequiredMessage: Message to be displayed for required text field
 * @param isError: Display error.
 * @param errorMessage: Error message to be displayed.
 * @param enabled: Enable or Disable field.
 * @param isPassword: Enable password behavior.
 * @param onValueChange: Function to handle input changes.
 * @param onDebounceValidation: Function to handle validations with a debounce of 0.5 seg.
 * **/

@OptIn(
    ExperimentalFoundationApi::class, kotlinx.coroutines.FlowPreview::class,
    kotlinx.coroutines.ExperimentalCoroutinesApi::class
)
@Composable
fun CustomOutlinedTextField(
    modifier: Modifier = Modifier,
    labelText: String? = null,
    value: String? = null,
    leadingIcon: Int? = null,
    trailingIcon: Int? = null,
    placeHolder: String = "",
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    isRequired: Boolean = true,
    isRequiredMessage: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    isPassword: Boolean = false,
    onValueChange: (newText: String) -> Unit = {},
    customTransformation: VisualTransformation? = null,
    onDebounceValidation: (newText: String) -> Unit = {}
) {
    var emptyError by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    val textDebounce = remember { MutableStateFlow("") }
    val textDebounceFlow: Flow<String> = remember {
        textDebounce.debounce(500)
            .distinctUntilChanged()
            .flatMapLatest {
                if (it.isNotEmpty()) {
                    onDebounceValidation(it)
                }
                flowOf(it)
            }
    }

    val passwordVisibleDebounce = remember { MutableStateFlow(false) }
    val passwordVisibleFlow: Flow<Boolean> = remember {
        passwordVisibleDebounce.debounce((1000 * 10))
            .onEach { status ->
                if (status) {
                    passwordVisible = false
                    passwordVisibleDebounce.value = false
                }
                flowOf(status)
            }
    }

    // Set colors depending on system theme
    val labelColor: Color
    var backgroundColor: Color
    val iconTintColor: Color
    val textColor: Color
    val placeholderColor: Color

    if (isSystemInDarkTheme()) {
        labelColor = GrayScale300
        backgroundColor = GrayScale600
        placeholderColor = WhiteTransparency60
        when {
            isError -> {
                iconTintColor = Primary400
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
        labelColor = GrayScale800
        backgroundColor = DefaultWhite
        placeholderColor = GrayScale500
        when {
            isError -> {
                iconTintColor = Primary400
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
                .fillMaxWidth()
                .bringIntoViewRequester(bringIntoViewRequester)
                .onFocusChanged {
                    if (it.isFocused) {
                        coroutineScope.launch {
                            // This sends a request to all parents that asks them to scroll so
                            // that this item is brought into view.
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                },
            value = value ?: "",
            shape = RoundedCornerShape(25),
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        painter = painterResource(id = it),
                        contentDescription = "",
                        tint = iconTintColor
                    )
                }
            },
            trailingIcon = if (isPassword) {
                {
                    val image = if (passwordVisible) {
                        passwordVisibleDebounce.value = true
                        painterResource(id = R.drawable.ic_view_off)
                    } else {
                        painterResource(id = R.drawable.ic_view)
                    }

                    IconButton(onClick = {
                        passwordVisible = !passwordVisible
                    }) {
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
                            painter = painterResource(id = it),
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
                textDebounce.value = it
                if (isRequired) emptyError = it.isEmpty()
            },
            placeholder = {
                Text(
                    text = placeHolder,
                    color = placeholderColor,
                    style = Typography.body2
                )
            },
            isError = isError || emptyError,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = backgroundColor,
                focusedIndicatorColor = Primary500,
                unfocusedIndicatorColor = GrayScale400,
                errorIndicatorColor = SemanticNegative500,
                textColor = textColor,
                cursorColor = textColor
            ),
            enabled = enabled,
            visualTransformation = customTransformation
                ?: if (passwordVisible || !isPassword) {
                    VisualTransformation.None
                } else PasswordVisualTransformation(),
            textStyle = Typography.body2
        )

        // This is required to execute the debounce
        val textDebounceFlowValue by textDebounceFlow.collectAsState("")
        val passwordDebounceFlowValue by passwordVisibleFlow.collectAsState(false)

        // Display error message
        if (isError && errorMessage.isNullOrBlank().not() || emptyError) {
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_exclamation_mark),
                    modifier = Modifier
                        .size(width = 11.dp, height = 11.dp),
                    contentDescription = "",
                    tint = SemanticNegative500
                )
                Text(
                    text =
                    if (isError && errorMessage.isNullOrBlank().not()) {
                        errorMessage ?: ""
                    } else if (emptyError && isRequiredMessage.isNullOrBlank().not()) {
                        isRequiredMessage ?: ""
                    } else if (emptyError) {
                        stringResource(id = R.string.error_empty_field)
                    } else {
                        ""
                    },
                    color = SemanticNegative500,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .wrapContentSize(),
                    style = Typography.caption
                )
            }
        }
    }
}