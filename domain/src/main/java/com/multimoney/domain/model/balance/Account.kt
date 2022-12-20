package com.multimoney.domain.model.balance

import com.multimoney.domain.model.accountsmart.SmartMovement

data class Account(
    val totalBalance: Double?,
    val currencyCode: String?,
    val gainedInterest: Double?,
    val accountNumber: String?,
    val ibanAccountNumber: String?,
    val totalInterest: String?,
    val tokenNumber: String?,
    val movements: List<SmartMovement> = emptyList(),
    val idCurrencyAccount: Int?
)
