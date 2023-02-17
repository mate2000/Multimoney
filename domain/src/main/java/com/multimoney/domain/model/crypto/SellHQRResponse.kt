package com.multimoney.domain.model.crypto

data class SellHQRResponse(
    val status: Int?,
    val message: String?,
    val result: SellCryptoCurrencyResult?,
)
