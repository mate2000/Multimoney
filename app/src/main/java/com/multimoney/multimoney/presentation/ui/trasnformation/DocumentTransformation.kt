package com.multimoney.multimoney.presentation.ui.trasnformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

fun formatId(): VisualTransformation =
    object : VisualTransformation {
        override fun filter(text: AnnotatedString): TransformedText {
            val offset = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    if (offset <= 1) return offset
                    if (offset <= 4) return offset + 1
                    if (offset <= 15) return offset + 2
                    return 11
                }

                override fun transformedToOriginal(offset: Int): Int {
                    if (offset <= 0) return offset
                    if (offset <= 3) return offset - 1
                    if (offset <= 14) return offset - 2
                    return 10
                }
            }
            var formattedText = ""
            val trimmed = if (text.text.length >= 15) text.text.substring(0..14) else text.text

            for (i in trimmed.indices) {
                formattedText += trimmed[i]
                if (i == 0 || i == 4) formattedText += " "
            }
            return TransformedText(AnnotatedString(formattedText), offset)
        }
    }

fun formatDui(): VisualTransformation =
    object : VisualTransformation {
        override fun filter(text: AnnotatedString): TransformedText {
            val offset = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    if (offset <= 7) return offset
                    if (offset <= 8) return offset + 1
                    return 10
                }

                override fun transformedToOriginal(offset: Int): Int {
                    if (offset <= 7) return offset
                    if (offset <= 8) return offset - 1
                    return 9
                }
            }
            return if (text.text.length >= 8) {
                val formattedText =
                    text.substring(0..7) + "-" + text.substring(8 until text.text.length)
                TransformedText(AnnotatedString(formattedText), offset)
            } else {
                TransformedText(AnnotatedString(text.text), offset)
            }
        }
    }

fun formatDpi(): VisualTransformation =
    object : VisualTransformation {
        override fun filter(text: AnnotatedString): TransformedText {
            val offset = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    if (offset <= 4) return offset
                    if (offset <= 9) return offset + 1
                    if (offset <= 15) return offset + 2
                    return 15
                }

                override fun transformedToOriginal(offset: Int): Int {
                    if (offset <= 3) return offset
                    if (offset <= 10) return offset - 1
                    if (offset <= 14) return offset - 2
                    return 14
                }
            }
            var formattedText = ""
            val trimmed = if (text.text.length >= 15) text.text.substring(0..14) else text.text

            for (i in trimmed.indices) {
                formattedText += trimmed[i]
                if (i == 3 || i == 8) formattedText += " "
            }
            return TransformedText(AnnotatedString(formattedText), offset)
        }
    }