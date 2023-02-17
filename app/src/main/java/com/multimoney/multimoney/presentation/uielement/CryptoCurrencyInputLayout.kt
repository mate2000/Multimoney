package com.multimoney.multimoney.presentation.uielement

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.util.DECIMAL_AND_NUMBER_REGEX
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.transformation.CryptoAssetMaskTransformation
import com.multimoney.multimoney.presentation.util.transformation.CurrencyDoubleTransformation

/**
 * CryptoCurrencyInputLayout: Custom layout to display a currency input with button to change
 * between dollars and currency, this can handle errors
 *
 * Parameters:
 * @param modifier Modifier to be applied to the layout
 * @param value MutableState of the value to be displayed
 * @param iconCurrency String of the icon to be displayed
 * @param isTransformationCurrency MutableState of the transformation to be applied to the value
 * @param focusRequester FocusRequester to be applied to the layout
 * @param isError Boolean to indicate if the layout has an error
 * @param errorText String to be displayed in case of error
 * @param onValueChanged Function to be called when the value is changed
 * @param onImeClick Function to be called when the IME is clicked
 *
 * **/

@ExperimentalAnimationApi
@Composable
fun CryptoCurrencyInputLayout(
    modifier: Modifier = Modifier,
    value: MutableState<String>,
    iconCurrency: String,
    isTransformationCurrency: MutableState<Boolean>,
    focusRequester: FocusRequester,
    isError: Boolean = false,
    errorText: String? = null,
    onValueChanged: (String) -> Unit,
    onImeClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomTextField(
                value = value,
                iconCurrency = iconCurrency,
                isTransformationCurrency = isTransformationCurrency,
                focusRequester = focusRequester,
                onSearchClick = onImeClick,
                isError = isError,
                onValueChanged = onValueChanged
            )
            AnimatedVisibility(visible = isError && errorText?.isNotEmpty() == true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Image(
                        modifier = Modifier
                            .size(24.dp, 24.dp)
                            .padding(end = 8.dp),
                        painter = painterResource(id = R.drawable.ic_alert_text_error),
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.wrapContentWidth(),
                        text = errorText ?: "",
                        style = Typography.subtitle1.copy(
                            color = MultimoneyTheme.colors.textAlertColor
                        )
                    )
                }

            }
        }
    }
}

@Composable
@Preview
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: MutableState<String> = mutableStateOf(""),
    iconCurrency: String = "",
    isTransformationCurrency: MutableState<Boolean> = mutableStateOf(false),
    focusRequester: FocusRequester = FocusRequester(),
    isError: Boolean = false,
    onValueChanged: (String) -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        OutlinedTextField(
            modifier = modifier
                .fillMaxWidth()
                .height(80.dp)
                .focusRequester(focusRequester),
            value = value.value,
            textStyle = Typography.h4.copy(
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.Bold,
                fontSize = getCorrectAmountOfCharacters(
                    amount = value.value,
                    isTransformationCurrency = isTransformationCurrency.value
                )
            ),
            onValueChange = { newValue ->
                if (newValue.length <= LOT_OF_CHARACTERS && newValue
                        .matches(Regex(DECIMAL_AND_NUMBER_REGEX))
                ) {
                    value.value = validateTextFormat(
                        newValue = newValue,
                        onValueChanged = onValueChanged,
                    )
                }
            },
            placeholder = {
                Text(
                    text = if (isTransformationCurrency.value.not()) {
                        CURRENCY_DEFAULT_PLACEHOLDER
                    } else {
                        stringResource(
                            id = R.string.crypto_purchase_flow_amount_asset_placeholder,
                            iconCurrency
                        )
                    },
                    style = Typography.h4.copy(
                        color = MultimoneyTheme.colors.bodyTextColor,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            visualTransformation = if (isTransformationCurrency.value.not()) {
                CurrencyDoubleTransformation(currency = CurrencyType.Dollar.symbol, separator = SIMPLE_COMMA)
            } else {
                CryptoAssetMaskTransformation(asset = iconCurrency)
            },
            shape = RoundedCornerShape(50.dp),
            singleLine = true,
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            keyboardActions = KeyboardActions { onSearchClick() },
            colors = TextFieldDefaults.textFieldColors(
                textColor = MultimoneyTheme.colors.text,
                cursorColor = MultimoneyTheme.colors.text,
                errorLabelColor = MultimoneyTheme.colors.textInputErrorLabelColor,
                disabledTextColor = MultimoneyTheme.colors.fullTransparency,
                backgroundColor = MultimoneyTheme.colors.backgroundInformativeChip,
                focusedIndicatorColor = MultimoneyTheme.colors.bodyTextColor,
                unfocusedIndicatorColor = MultimoneyTheme.colors.fullTransparency,
                disabledIndicatorColor = MultimoneyTheme.colors.fullTransparency
            )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 24.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            IconButton(
                modifier = Modifier.wrapContentSize(),
                onClick = {
                    isTransformationCurrency.value = !isTransformationCurrency.value
                    value.value = ""
                }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.padding(bottom = 8.dp),
                        painter = painterResource(id = R.drawable.ic_currency_invert_arrows),
                        tint = MultimoneyTheme.colors.textLink,
                        contentDescription = null
                    )
                    Text(
                        text = if (isTransformationCurrency.value) CurrencyType.Dollar.disbursementValue else iconCurrency,
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.textLink,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

fun validateTextFormat(
    newValue: String,
    onValueChanged: (String) -> Unit
): String {
    return when {
        newValue.isEmpty() -> {
            onValueChanged("")
            ""
        }
        newValue.startsWith(SIMPLE_DOT) -> {
            onValueChanged("")
            ""
        }
        newValue.count { it.toString() == SIMPLE_DOT } > ONE_LENGTH
                && newValue.endsWith(SIMPLE_DOT) -> {
            onValueChanged(newValue.dropLast(ONE_LENGTH))
            newValue.dropLast(ONE_LENGTH)
        }
        else -> {
            onValueChanged(newValue)
            newValue
        }
    }
}

fun getCorrectAmountOfCharacters(
    amount: String,
    isTransformationCurrency: Boolean
): TextUnit {

    return if (isTransformationCurrency.not()) {
        when {
            amount.length <= FEW_CHARACTERS -> 34.sp
            amount.length <= MANY_CHARACTERS -> 24.sp
            amount.length <= TOO_MANY_CHARACTERS -> 16.sp
            else -> 12.sp
        }
    } else {
        when {
            amount.length <= FEW_CHARACTERS.minus(ASSET_EQUIVALENT_SUBTRACTION) -> 34.sp
            amount.length <= MANY_CHARACTERS.minus(ASSET_EQUIVALENT_SUBTRACTION) -> 24.sp
            amount.length <= TOO_MANY_CHARACTERS.minus(ASSET_EQUIVALENT_SUBTRACTION) -> 16.sp
            else -> 12.sp
        }
    }
}

const val FEW_CHARACTERS = 10
const val MANY_CHARACTERS = 14
const val TOO_MANY_CHARACTERS = 23
const val LOT_OF_CHARACTERS = 32
const val ASSET_EQUIVALENT_SUBTRACTION = 2
const val ONE_LENGTH = 1
const val SIMPLE_DOT = "."
const val SIMPLE_COMMA = ','
const val CURRENCY_DEFAULT_PLACEHOLDER = "$0"
