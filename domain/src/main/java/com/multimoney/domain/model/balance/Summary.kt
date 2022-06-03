package com.multimoney.domain.model.balance

data class Summary(
    val currentBalanceLabel: String?,
    val availableBalanceLabel: String?,
    val paymentDateLabel: String?,
    val monthlyQuotaLabel: String?,
)