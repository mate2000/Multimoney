package com.multimoney.domain.model.crypto

data class BuyCryptoCurrencyResponse(
    val status: Int?,
    val message: String?,
    val result: BuyCryptoCurrencyResult?,
)