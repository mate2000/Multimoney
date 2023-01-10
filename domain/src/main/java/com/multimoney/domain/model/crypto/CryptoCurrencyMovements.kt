package com.multimoney.domain.model.crypto

data class CryptoCurrencyMovements(
    val items: List<CryptoCurrencyMovement>,
    val totalCount: Int
)