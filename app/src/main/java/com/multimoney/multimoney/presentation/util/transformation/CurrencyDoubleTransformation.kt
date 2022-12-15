package com.multimoney.multimoney.presentation.util.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.multimoney.multimoney.presentation.util.isValidAmount
import com.multimoney.multimoney.presentation.util.stringToDoubleFormat
import com.multimoney.multimoney.presentation.util.stringToIntegerFormat

class CurrencyDoubleTransformation(val currency: String, val separator: Char) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        val formattedText = if (text.toString().isValidAmount()) {
            "$currency${text.toString().stringToDoubleFormat(separator.toString())}"
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
