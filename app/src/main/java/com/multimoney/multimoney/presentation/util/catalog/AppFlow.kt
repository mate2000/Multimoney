package com.multimoney.multimoney.presentation.util.catalog

sealed class AppFlow {
    object CreditOriginationFlow : AppFlow()
    object SignUpFlow : AppFlow()
}
