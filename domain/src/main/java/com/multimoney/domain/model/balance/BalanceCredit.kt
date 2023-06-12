package com.multimoney.domain.model.balance

data class BalanceCredit(
    val summary: List<Summary>?,
    val creditLimit: String?,
    val creditNumber: String?,
    val creditLimitLabel: String?,
    val term: String?,
    val applyAutomaticDebit: Boolean?,
    val automaticDebitEnabled: Boolean?,
    val expiredAutomaticDebitCard: Boolean?
)
