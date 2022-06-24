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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.DefaultBlack
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.GrayScale300
import com.multimoney.multimoney.presentation.theme.GrayScale400
import com.multimoney.multimoney.presentation.theme.GrayScale500
import com.multimoney.multimoney.presentation.theme.GrayScale800
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.SemanticNegative400
import com.multimoney.multimoney.presentation.theme.SemanticNegative500
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency10
import com.multimoney.multimoney.presentation.theme.WhiteTransparency60
import com.multimoney.multimoney.presentation.util.transformation.PhoneNumberTransformation
import com.togitech.ccp.data.CountryData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

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

@OptIn(
    ExperimentalFoundationApi::class, kotlinx.coroutines.FlowPreview::class,
    kotlinx.coroutines.ExperimentalCoroutinesApi::class
)
@Composable
fun PhoneTextField(
    modifier: Modifier = Modifier,
    labelText: String? = null,
    value: String? = null,
    defaultCountry: CountryData,
    keyboardActions: KeyboardActions,
    isRequired: Boolean = true,
    isRequiredMessage: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    pickedCountry: (CountryData) -> Unit,
    dialogAppBarColor: Color = MultimoneyTheme.colors.primary,
    dialogAppBarTextColor: Color = Color.White,
    dialogFocusedBorderColorSearch: Color = Color.Transparent,
    dialogUnFocusedBorderColorSearch: Color = Color.Transparent,
    dialogCursorColorSearch: Color = MultimoneyTheme.colors.primary,
    showCountryCode: Boolean = true,
    showCountryFlag: Boolean = true,
    onValueChange: (newText: String) -> Unit = {},
    onDebounceValidation: (newText: String) -> Unit = {}
) {
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value ?: "")) }
    val textFieldValue = textFieldValueState.copy(text = value ?: "")
    val phoneNumberTransformation =
        PhoneNumberTransformation(defaultCountry.countryCode.uppercase())

    var emptyError by rememberSaveable { mutableStateOf(false) }
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

    // Set colors depending on system theme
    val labelColor: Color
    var backgroundColor: Color
    val textColor: Color
    val placeholderColor: Color
    val focusedIndicatorColor: Color
    val unfocusedIndicatorColor: Color
    val errorIndicatorColor: Color

    if (isSystemInDarkTheme()) {
        labelColor = GrayScale300
        backgroundColor = WhiteTransparency10
        placeholderColor = WhiteTransparency60
        unfocusedIndicatorColor = DefaultBlack
        errorIndicatorColor = SemanticNegative400
        when {
            isError -> {
                focusedIndicatorColor = SemanticNegative400
                textColor = DefaultWhite
            }
            enabled -> {
                focusedIndicatorColor = WhiteTransparency60
                textColor = DefaultWhite
            }
            else -> {
                focusedIndicatorColor = DefaultBlack
                backgroundColor = GrayScale500
                textColor = GrayScale400
            }
        }
    } else {
        labelColor = GrayScale800
        backgroundColor = DefaultWhite
        placeholderColor = GrayScale500
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
            value = textFieldValue,
            shape = RoundedCornerShape(50),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Phone,
                autoCorrect = true,
            ),
            keyboardActions = keyboardActions,
            onValueChange = {
                if (it.text.length <= phoneNumberTransformation.mobileMaxLength) {
                    textFieldValueState = it
                    onValueChange(it.text)
                    textDebounce.value = it.text
                    if (isRequired) emptyError = it.text.isEmpty()
                }
            },
            placeholder = {
                Text(
                    text = phoneNumberTransformation.mobileTextExample.text,
                    color = placeholderColor,
                    style = Typography.body2
                )
            },
            visualTransformation = phoneNumberTransformation,
            leadingIcon = {
                PhoneCountryDialog(
                    padding = 12.dp,
                    pickedCountry = pickedCountry,
                    defaultSelectedCountry = defaultCountry,
                    dialogAppBarColor = dialogAppBarColor,
                    dialogAppBarTextColor = dialogAppBarTextColor,
                    showCountryCode = showCountryCode,
                    showCountryFlag = showCountryFlag,
                    dialogFocusedBorderColorSearch = dialogFocusedBorderColorSearch,
                    dialogUnFocusedBorderColorSearch = dialogUnFocusedBorderColorSearch,
                    dialogCursorColorSearch = dialogCursorColorSearch,
                    countryCodeTextColor = textColor
                )
            },
            singleLine = true,
            isError = isError || emptyError,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = backgroundColor,
                focusedIndicatorColor = focusedIndicatorColor,
                unfocusedIndicatorColor = unfocusedIndicatorColor,
                errorIndicatorColor = errorIndicatorColor,
                textColor = textColor,
                cursorColor = textColor
            ),
            textStyle = Typography.body2
        )

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