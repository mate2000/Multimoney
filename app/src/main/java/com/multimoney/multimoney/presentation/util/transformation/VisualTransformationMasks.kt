package com.multimoney.multimoney.presentation.util.transformation

enum class VisualTransformationMasks(val mask: String, val maskChar: Char) {
    PHONE_TRANSFORMATION_MASK("#### ####", '#'),
    IBAN_TRANSFORMATION_MASK("#### #### #### #### ####", '#')
}
