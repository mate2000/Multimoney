package com.multimoney.domain.model.balance

data class BalanceCredit(
    val summary: List<Summary>?,
    val creditNumber: String,
    val creditLimit: String,
    val term: String
)