package com.multimoney.domain.model.crypto

data class SendCryptoToAddressResult(
    val sysdeTransactionNumber: String,
    val paxosIdOrder: String?,
    val paxosSendTransferId: String,
    val paxosReceivedTransferId: String,
    val steps: String,
    val orderComplete: Boolean,
    val filledAmount: Double,
    val averagePrice: Double,
    val status: String,
    val commissionAmount: Double,
    val totalAmount: Double
)
