package com.multimoney.domain.model.crypto

data class SellCryptoRequest(
    val pkUser: Int,
    val identification: String,
    val market: String,
    val commissionPercentage: Double,
    val taxPercentage: Double,
    val accountToken: Long,
    val exchangeRate: Double,
    val idBrand: Int,
    val user: String,
    val quoteId: String,
    val baseAmount: Double,
    val fee: Double,
    val internalFee: Double,
    val totalFee: Double
)
