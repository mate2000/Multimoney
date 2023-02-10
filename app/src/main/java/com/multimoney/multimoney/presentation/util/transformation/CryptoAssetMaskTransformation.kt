package com.multimoney.multimoney.presentation.util.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import kotlin.math.max

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
                    return if (offset >= transformedText.length.minus(
                            asset.length + 1
                        )
                    ) originalLength else max(0, offset - (asset.length + 1))
                }
            }
        )
    }
}