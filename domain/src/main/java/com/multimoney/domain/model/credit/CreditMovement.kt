package com.multimoney.domain.model.credit

data class CreditMovement(
    val description: String?,
    val amount: String?,
    val amountLabel: String?,
    val transactionType: String?,
    val date: String?
)
