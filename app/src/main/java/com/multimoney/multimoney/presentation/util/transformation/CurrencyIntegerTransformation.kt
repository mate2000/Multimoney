package com.multimoney.multimoney.presentation.util.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.core.text.isDigitsOnly

class CurrencyIntegerTransformation(val currency: String, val separator: Char) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {

        val originalText = text.text
        val formattedText = text.text

        val offsetMapping = object : OffsetMapping {

            override fun originalToTransformed(offset: Int): Int {
                if (originalText.isDigitsOnly()) {
                    val separatorFormatted = formattedText.count { it == separator }
                    return when {
                        offset <= 1 -> offset
                        offset <= 3 -> if (separatorFormatted >= 1) offset + 1 else offset
                        offset <= 5 -> if (separatorFormatted == 2) offset + 2 else offset + 1
                        else -> 8
                    }
                }
                return offset
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (originalText.isDigitsOnly()) {
                    val separatorFormatted = formattedText.count { it == separator }
                    return when (offset) {
                        8, 7 -> offset - 2
                        6 -> if (separatorFormatted == 1) 5 else 4
                        5 -> if (separatorFormatted == 1) 4 else if (separatorFormatted == 2) 3 else offset
                        4, 3 -> if (separatorFormatted >= 1) offset - 1 else offset
                        2 -> if (separatorFormatted == 2) 1 else offset
                        else -> offset
                    }
                }
                return offset
            }
        }

        return TransformedText(
            text = AnnotatedString("$formattedText"),
            offsetMapping = offsetMapping
        )
    }
}