package com.multimoney.domain.model.security

data class ValidateAccount(
    val responseCode: Int,
    val responseMessage: String,
    val identification: String?,
    val name: String,
    val currency: String,
    val sellPriceDollar: String,
    val buyPriceDollar: String,
    val bankId: Int?,
    val bankName: String?
)
