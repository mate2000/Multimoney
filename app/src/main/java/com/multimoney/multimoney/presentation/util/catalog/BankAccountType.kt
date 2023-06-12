package com.multimoney.multimoney.presentation.util.catalog

sealed class BankAccountType(val value: String) {
    object Credit : BankAccountType("CREDIT")
}
