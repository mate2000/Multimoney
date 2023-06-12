package com.multimoney.multimoney.presentation.util.catalog

sealed class QuickActionsProductType(val value: String) {
    object Credit : QuickActionsProductType("Credit")
    object Smart : QuickActionsProductType("AccountSmart")
    object Crypto : QuickActionsProductType("Crypto")
}