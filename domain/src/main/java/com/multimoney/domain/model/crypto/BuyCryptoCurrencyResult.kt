package com.multimoney.domain.model.crypto

data class BuyCryptoCurrencyResult(
    val sysdeTransactionNumber: String,
    val paxosSendTransferId: String,
    val paxosReceivedTransferId: String,
    val paxosIdOrder: String,
    val orderComplete: Boolean,
    val filledAmount: Double,
    val averagePrice: Double,
    val status: String,
    val taxAmount: Double,
    val commissionAmount: Double,
)