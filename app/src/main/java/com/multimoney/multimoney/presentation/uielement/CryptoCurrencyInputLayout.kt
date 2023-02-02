package com.multimoney.multimoney.presentation.uielement

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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

@ExperimentalAnimationApi
@Composable
fun CryptoCurrencyInputLayout(
    modifier: Modifier = Modifier,
    value: MutableState<String>,
    iconCurrency: String,
    isTransformationCurrency: MutableState<Boolean>,
    focusRequester: FocusRequester,
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
                onSearchClick = onImeClick
            )
            // textView with error
            AnimatedVisibility(visible = true) {
                Text(text = "")
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
            onValueChange = {
                if (it.length <= LOT_OF_CHARACTERS && it.matches(Regex(DECIMAL_AND_NUMBER_REGEX))) {
                    value.value = when {
                        it.isEmpty() -> EMPTY_STRING
                        it.length == ONE_LENGTH && it.last().toString() == SIMPLE_DOT -> EMPTY_STRING
                        else -> it
                    }
                }
            },
            placeholder = {
                if (isTransformationCurrency.value.not()) {
                    Text(
                        text = CURRENCY_DEFAULT_PLACEHOLDER,
                        style = Typography.h4.copy(
                            color = MultimoneyTheme.colors.bodyTextColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                } else {
                    Text(
                        text = stringResource(
                            id = R.string.crypto_purchase_flow_amount_asset_placeholder,
                            iconCurrency
                        ),
                        style = Typography.h4.copy(
                            color = MultimoneyTheme.colors.bodyTextColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

            },
            visualTransformation = if (isTransformationCurrency.value.not()) {
                VisualTransformation.None
            } else {
                VisualTransformation.None
            },
            shape = RoundedCornerShape(50.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            keyboardActions = KeyboardActions { onSearchClick() },
            colors = TextFieldDefaults.textFieldColors(
                textColor = MultimoneyTheme.colors.text,
                cursorColor = MultimoneyTheme.colors.text,
                disabledTextColor = MultimoneyTheme.colors.fullTransparency,
                backgroundColor = MultimoneyTheme.colors.backgroundInformativeChip,
                focusedIndicatorColor = MultimoneyTheme.colors.fullTransparency,
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
                    value.value = EMPTY_STRING
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
const val EMPTY_STRING = ""

class CurrencyMaskTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText = maskFilter(text)

    private fun maskFilter(text: AnnotatedString): TransformedText {
        var out = ""
        for (i in text.text.indices) {
            if (i == 0) out += ""
            out += text.text[i]
        }

        val numberOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return offset
                return text.text.length + 2
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                return offset -2
            }
        }

        return TransformedText(AnnotatedString(out), numberOffsetTranslator)
    }
}

class CryptoAssetMaskTransformation(val asset: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText = maskFilter(text)

    private fun maskFilter(text: AnnotatedString): TransformedText {
        var out = ""
        for (i in text.text.indices) {
            if (i > 0) out += " $asset"
            out += text.text[i]
        }

        val numberOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return offset
                return text.text.length + if (asset.length <= 3) asset.length + 1 else asset.length + 2
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 5) return offset
                return offset - if (asset.length <= 3) asset.length + 1 else asset.length + 2
            }
        }

        return TransformedText(AnnotatedString(out), numberOffsetTranslator)
    }
}