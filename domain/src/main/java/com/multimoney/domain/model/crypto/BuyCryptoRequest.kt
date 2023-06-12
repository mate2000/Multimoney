package com.multimoney.domain.model.crypto

data class BuyCryptoRequest(
    val pkUser: Int,
    val identification: String,
    val market: String,
    val commissionAmount: Double,
    val taxAmount: Double,
    val accountToken: Long,
    val exchangeRate: Double,
    val idBrand: Int,
    val user: String,
    val quoteId: String,
    val quoteAmount: Double,
    val fee: Double,
    val internalFee: Double,
    val totalFee: Double
)