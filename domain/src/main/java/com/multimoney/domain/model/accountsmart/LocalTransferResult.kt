package com.multimoney.domain.model.accountsmart

data class LocalTransferResult(
    val authorization: String,
    val currentBalance: Double,
    val destinationTitularName: String,
    val originAccountNumber: String
)
