package com.multimoney.domain.model.balance

data class Account(
    val totalBalance: Double?,
    val currencyCode: String?,
    val gainedInterest: Double?,
    val accountNumber: String?,
    val ibanAccountNumber: String?,
    val totalInterest: String?,
)
