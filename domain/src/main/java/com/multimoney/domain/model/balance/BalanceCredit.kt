package com.multimoney.domain.model.balance

data class BalanceCredit(
    val summary: List<Summary>?,
    val creditNumber: String,
    val creditLimitLabel: String,
    val term: String
)