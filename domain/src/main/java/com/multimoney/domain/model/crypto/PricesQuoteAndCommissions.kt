package com.multimoney.domain.model.crypto

data class PricesQuoteAndCommissions(
    val fee: String,
    val internal_fee: Double,
    val taxAmount: Double,
    val totalFee: Double,
    val quote_id: String,
    val side: String,
    val price: Double,
    val created_at: String,
    val expires_at: String,
    val quote_amount: Double,
    val base_amount: Double,
)