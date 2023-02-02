package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * @param modifier - modifier only for BalanceText
 * @param balanceText - text to display with the balance
 * @param currencyStyle - style for currency main text
 * @param currencyDecimalStyle - style for currency decimal text
 *
 * **/

@Composable
fun BalanceTextView(
    modifier: Modifier = Modifier,
    balanceText: String,
    currencyStyle: TextStyle,
    currencyDecimalStyle: TextStyle
) {
    val decimalSeparator = DecimalFormatSymbols(Locale.getDefault()).decimalSeparator
    val splitText = balanceText.split(decimalSeparator)
    val localDensity = LocalDensity.current
    var fontPadding by remember { mutableStateOf(0.dp) }

    Row(
        modifier = Modifier.wrapContentSize()
    ) {
        Text(
            modifier = modifier,
            text = splitText[0],
            style = currencyStyle,
            onTextLayout = {
                val totalTextHeight = with(localDensity) {
                    it.size.height.toDp()
                }
                val fontHeight = with(localDensity) {
                    currencyStyle.fontSize.toDp()
                }
                val fontPaddingDifference = (totalTextHeight - fontHeight) / 2
                with(localDensity) {
                    fontPadding =
                        if (fontPaddingDifference <= currencyDecimalStyle.fontSize.toDp()) {
                            fontPaddingDifference
                        } else {
                            currencyDecimalStyle.fontSize.toDp()
                        }
                }
            }
        )
        Box(contentAlignment = Alignment.Center) {
            Text(
                modifier = Modifier.offset(y = fontPadding),
                text = buildAnnotatedString {
                    append(decimalSeparator)
                    append(splitText[1])
                },
                style = currencyDecimalStyle
            )
        }
    }
}
