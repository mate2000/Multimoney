package com.multimoney.domain.model.crypto

data class CryptoCurrencyMovement(
    val items: List<Item>,
    val total_count: Int
)