package com.multimoney.multimoney.presentation.util.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.multimoney.multimoney.presentation.util.isValidAmount
import java.text.DecimalFormat

class CurrencyDoubleTransformation(val currency: String, val separator: Char) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        val symbols = DecimalFormat().decimalFormatSymbols
        val decimalSeparator = "."
        val zero = symbols.zeroDigit
        val formattedText = if (text.toString().isValidAmount() && text.toString().isNotEmpty()) {
            val numberOfDecimals =
                if (originalText.substringAfterLast(decimalSeparator).length == originalText.length) {
                    0
                } else {
                    originalText.substringAfterLast(decimalSeparator).length + 1
                }
            val intPart = originalText
                .dropLast(numberOfDecimals)
                .reversed()
                .chunked(3)
                .joinToString(separator.toString())
                .reversed()
                .ifEmpty {
                    zero.toString()
                }

            val fractionPart = originalText.takeLast(numberOfDecimals)
            "$currency$intPart$fractionPart"
        } else {
            text.toString()
        }

        val offsetMapping = object : OffsetMapping {

            override fun originalToTransformed(offset: Int): Int {
                if (originalText.isValidAmount()) {
                    return formattedText.length
                }
                return offset
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (originalText.isValidAmount()) {
                    return formattedText.length
                }
                return offset
            }
        }

        return TransformedText(
            text = AnnotatedString(formattedText),
            offsetMapping = offsetMapping
        )
    }
}
