package com.multimoney.multimoney.presentation.util.catalog

enum class ValidationSecurityPassword(val actionSecurity: Int) {
    None(0),
    OnlyValidate(1),
    OnlySave(2)
}