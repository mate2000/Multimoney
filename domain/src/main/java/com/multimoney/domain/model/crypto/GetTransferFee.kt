package com.multimoney.domain.model.crypto

data class GetTransferFee(
    val id: String,
    val fee: String,
    val internalFee: Double,
    val taxAmount: Double,
    val expiresAt: String,
    val totalFee: Double
)