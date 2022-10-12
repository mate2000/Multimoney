package com.multimoney.domain.model.balance

data class BalanceCredit(
    val summary: List<Summary>?,
    val creditLimit: String?,
    val creditLimitLabel: String?,
)