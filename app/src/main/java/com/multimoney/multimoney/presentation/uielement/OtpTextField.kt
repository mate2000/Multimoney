package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.DefaultBlack
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.GrayScale300
import com.multimoney.multimoney.presentation.theme.GrayScale400
import com.multimoney.multimoney.presentation.theme.GrayScale500
import com.multimoney.multimoney.presentation.theme.GrayScale600
import com.multimoney.multimoney.presentation.theme.GrayScale800
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.SemanticNegative400
import com.multimoney.multimoney.presentation.theme.SemanticNegative500
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency10
import com.multimoney.multimoney.presentation.theme.WhiteTransparency30
import com.multimoney.multimoney.presentation.theme.WhiteTransparency60
import com.multimoney.multimoney.presentation.theme.WhiteTransparency70
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/**
 * CustomOutlinedTextField: This OutlinedTextField is used to match design system
 *
 * Parameters:
 * @param modifier: Apply style.
 * @param labelText: Text above the textField to describe its function.
 * @param value: Variable to store the input value.
 * @param placeHolder: Hint for the textField.
 * @param isValueFromSms: Value sent from SMS
 * @param digits: Total digits to be displayed.
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
    kotlinx.coroutines.ExperimentalCoroutinesApi::class,
    androidx.compose.ui.ExperimentalComposeUiApi::class
)
@Composable
@Preview
fun OtpTextField(
    modifier: Modifier = Modifier,
    labelText: String? = null,
    value: String = "",
    placeHolder: String = "",
    isValueFromSms: Boolean = false,
    digits: Int = 1,
    isRequired: Boolean = true,
    isRequiredMessage: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    onValueChange: (newText: String) -> Unit = {},
    onDebounceValidation: (newText: String) -> Unit = {}
) {
    val allowedDigitsRegex = "[0-9]+".toRegex()
    val emptyChar = ' '
    val maxWrittenDigit = 1
    val focusManager = LocalFocusManager.current
    var isFirstFocus by rememberSaveable { mutableStateOf(true) }
    var emptyError by rememberSaveable { mutableStateOf(false) }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    var valueCharArray by rememberSaveable { mutableStateOf(CharArray(digits)) }

    if (isValueFromSms) {
        valueCharArray = value.toCharArray()
        val newValue = String(valueCharArray)
        emptyError = newValue.trim().isEmpty()
        onValueChange(newValue)
        focusManager.clearFocus()
    }

    LaunchedEffect(true) {
        valueCharArray = CharArray(digits)
        (0 until digits).map { index ->
            valueCharArray[index] = emptyChar
        }
    }

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

    // Set colors depending on system theme
    val labelColor: Color
    var backgroundColor: Color
    val textColor: Color
    val placeholderColor: Color
    val focusedIndicatorColor: Color
    val unfocusedIndicatorColor: Color
    val errorIndicatorColor: Color

    if (isSystemInDarkTheme()) {
        labelColor = WhiteTransparency70
        backgroundColor = WhiteTransparency10
        placeholderColor = WhiteTransparency30
        unfocusedIndicatorColor = DefaultBlack
        errorIndicatorColor = SemanticNegative400
        when {
            isError -> {
                focusedIndicatorColor = SemanticNegative400
                textColor = WhiteTransparency90
            }
            enabled -> {
                focusedIndicatorColor = WhiteTransparency60
                textColor = WhiteTransparency90
            }
            else -> {
                focusedIndicatorColor = DefaultBlack
                backgroundColor = GrayScale500
                textColor = WhiteTransparency30
            }
        }
    } else {
        labelColor = GrayScale500
        backgroundColor = DefaultWhite
        placeholderColor = GrayScale400
        unfocusedIndicatorColor = GrayScale400
        errorIndicatorColor = SemanticNegative500
        when {
            isError -> {
                focusedIndicatorColor = SemanticNegative500
                textColor = GrayScale800
            }
            enabled -> {
                focusedIndicatorColor = Primary500
                textColor = GrayScale800
            }
            else -> {
                focusedIndicatorColor = GrayScale400
                backgroundColor = GrayScale300
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
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            (0 until digits).map { index ->
                OutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = 0.dp)
                        .size(56.dp)
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .onFocusChanged {
                            if (it.isFocused) {
                                valueCharArray[index] = emptyChar
                                val newText = String(valueCharArray)
                                if (isRequired && isFirstFocus.not()) {
                                    emptyError = newText
                                        .trim()
                                        .isEmpty()
                                }
                                onValueChange(newText)
                                isFirstFocus = false
                                coroutineScope.launch {
                                    // This sends a request to all parents that asks them to scroll so
                                    // that this item is brought into view.
                                    bringIntoViewRequester.bringIntoView()
                                }
                            }
                        }
                        .onKeyEvent { event: KeyEvent ->
                            // handle backspace key
                            if (event.type == KeyEventType.KeyUp &&
                                event.key == Key.Backspace
                            // also any additional checks of the "list" i.e isNotEmpty()
                            ) {
                                focusManager.moveFocus(FocusDirection.Previous)
                                return@onKeyEvent true
                            }
                            false
                        },
                    value = if (valueCharArray[index].toString() == emptyChar.toString())
                        ""
                    else
                        valueCharArray[index].toString(),
                    shape = RoundedCornerShape(25),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = if (index < digits - 1) {
                            ImeAction.Next
                        } else {
                            ImeAction.Done
                        }
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Next)
                    }, onDone = {
                        focusManager.clearFocus()
                    }),
                    maxLines = 1,
                    singleLine = true,
                    onValueChange = {
                        var newValue = value
                        if (it.matches(allowedDigitsRegex)) {
                            when (it.length) {
                                maxWrittenDigit -> valueCharArray[index] = it.toCharArray().first()
                                digits -> it.forEachIndexed { indexPasted, otpPasted ->
                                    valueCharArray[indexPasted] = otpPasted
                                }
                            }
                            newValue = String(valueCharArray)
                            onValueChange(newValue)
                            textDebounce.value = newValue
                            if (index < digits - 1 && it.length == 1 && valueCharArray[index + 1] == emptyChar) {
                                focusManager.moveFocus(FocusDirection.Next)
                            } else {
                                focusManager.clearFocus()
                            }
                        }
                        if (isRequired) emptyError = newValue.trim().isEmpty()
                    },
                    placeholder = {
                        Text(
                            modifier = Modifier.fillMaxSize(),
                            text = placeHolder,
                            color = GrayScale500,
                            style = Typography.body2,
                            textAlign = TextAlign.Center
                        )
                    },
                    isError = isError || emptyError,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = backgroundColor,
                        focusedIndicatorColor = focusedIndicatorColor,
                        unfocusedIndicatorColor = unfocusedIndicatorColor,
                        errorIndicatorColor = errorIndicatorColor,
                        textColor = textColor,
                        cursorColor = Color.Transparent,
                        errorCursorColor = Color.Transparent,
                        placeholderColor = placeholderColor
                    ),
                    enabled = true,
                    textStyle = Typography.body2.copy(textAlign = TextAlign.Center)
                )
            }
        }

        // This is required to execute the debounce
        val textDebounceFlowValue by textDebounceFlow.collectAsState("")

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
                    tint = errorIndicatorColor
                )
                Text(
                    text = if (emptyError && isRequiredMessage.isNullOrBlank().not()) {
                        isRequiredMessage ?: ""
                    } else if (emptyError) {
                        stringResource(id = R.string.error_empty_field)
                    } else {
                        errorMessage ?: ""
                    },
                    color = errorIndicatorColor,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .wrapContentSize(),
                    style = Typography.caption.copy(textAlign = TextAlign.Center)
                )
            }
        }
    }
}
