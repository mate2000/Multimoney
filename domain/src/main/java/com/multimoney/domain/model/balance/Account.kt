package com.multimoney.domain.model.balance

data class Account(
    val currency: String?,
    val totalBalance: Double?,
    val gainedInterest: Double?
)