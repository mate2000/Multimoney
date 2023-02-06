package com.multimoney.multimoney.presentation.uielement

import android.icu.text.NumberFormat
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.util.DECIMAL_AND_NUMBER_REGEX
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import java.util.*
import kotlin.math.max

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
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: MutableState<String>,
    iconCurrency: String,
    isTransformationCurrency: MutableState<Boolean>,
    focusRequester: FocusRequester,
    isError: Boolean,
    onValueChanged: (String) -> Unit = {},
    onSearchClick: () -> Unit
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
                fontSize = when {
                    value.value.length <= FEW_CHARACTERS -> 34.sp
                    value.value.length <= MANY_CHARACTERS -> 24.sp
                    value.value.length <= TOO_MANY_CHARACTERS -> 16.sp
                    else -> 12.sp
                }
            ),
            onValueChange = { newValue ->
                if (newValue.length <= LOT_OF_CHARACTERS && newValue.matches(Regex(DECIMAL_AND_NUMBER_REGEX))) {
                    value.value = when {
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
                VisualTransformation.None
                //todo use when is completed: CurrencyMaskTransformation()
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

const val FEW_CHARACTERS = 10
const val MANY_CHARACTERS = 17
const val TOO_MANY_CHARACTERS = 26
const val LOT_OF_CHARACTERS = 32
const val ONE_LENGTH = 1
const val SIMPLE_DOT = "."
const val CURRENCY_DEFAULT_PLACEHOLDER = "$0"

class CurrencyMaskTransformation : VisualTransformation {

    private val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        val formattedText = numberFormat.format(original.ifEmpty { "0" }.toDouble())

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var originalIndex = 0
                var newTextIndex = 0

                while (originalIndex < offset && originalIndex < original.length) {
                    if (original[originalIndex] == '.') {
                        break
                    }
                    originalIndex++
                    newTextIndex++
                }

                while (newTextIndex < formattedText.length && formattedText[newTextIndex] != '.') {
                    newTextIndex++
                }

                return newTextIndex
            }

            override fun transformedToOriginal(offset: Int): Int {
                var originalIndex = 0
                var newTextIndex = 0

                while (newTextIndex < offset && originalIndex < original.length) {
                    if (formattedText[newTextIndex] == ',') {
                        break
                    }
                    originalIndex++
                    newTextIndex++
                }

                return originalIndex
            }
        }

        return TransformedText(
            text = AnnotatedString(formattedText),
            offsetMapping = offsetMapping
        )
    }
}

class CryptoAssetMaskTransformation(val asset: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val transformedText = if (text.isEmpty()) "" else text.text.plus(" $asset")
        val originalLength = text.text.length
        return TransformedText(
            text = AnnotatedString(transformedText),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return if (offset <= text.length - (asset.length + 1)) 0 else offset
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return if (offset >= transformedText.length - (asset.length + 1)) originalLength else max(0, offset - (asset.length + 1))
                }
            }
        )
    }
}
