package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString

/**
 * @param modifier - modifier only for BalanceText
 * @param balanceText - text to display with the balance
 * @param splitWith - string separator, default is "."
 * @param currencyStyle - style for currency main text
 * @param currencyDecimalStyle - style for currency decimal text
 *
 * **/

const val DEFAULT_SPLIT_WITH = "."

@Composable
fun BalanceTextView(
    modifier: Modifier = Modifier,
    balanceText: String,
    splitWith: String = DEFAULT_SPLIT_WITH,
    currencyStyle: TextStyle,
    currencyDecimalStyle: TextStyle
) {
    val splitText = balanceText.split(splitWith)

    Row(
        modifier = Modifier.wrapContentSize()
    ) {
        Text(
            modifier = modifier,
            text = splitText[0],
            style = currencyStyle,
        )
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = buildAnnotatedString {
                    append(splitWith)
                    append(splitText[1])
                },
                style = currencyDecimalStyle
            )
        }
    }
}