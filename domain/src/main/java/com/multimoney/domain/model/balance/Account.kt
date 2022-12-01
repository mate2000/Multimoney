package com.multimoney.domain.model.balance

import com.multimoney.domain.model.accountsmart.SmartMovement

data class Account(
    val totalBalance: Double?,
    val currencyCode: String?,
    val gainedInterest: Double?,
    val tokenNumber: String?,
    val movements: List<SmartMovement> = emptyList()
)
