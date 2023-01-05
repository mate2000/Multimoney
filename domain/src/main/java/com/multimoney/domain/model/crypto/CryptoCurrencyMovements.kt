package com.multimoney.domain.model.crypto

data class CryptoCurrencyMovements(
    val items: List<CryptoCurrencyMovement>,
    val total_count: Int
)