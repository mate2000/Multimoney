package com.multimoney.domain.model.crypto

data class CryptoCurrencyMovement(
    val abbreviationCurrency: String = "",
    val amountFilled: String = "",
    val baseAmount: String = "",
    val createdAt: String = "",
    val dateCreated: String = "",
    val descriptionMovement: String = "",
    val held: Boolean = false,
    val id: String = "",
    val market: String = "",
    val modifiedAt: String = "",
    val monthLimitExceeded: Boolean = false,
    val price: String = "",
    val profileId: String = "",
    val quoteAmount: String = "",
    val side: String = "",
    val type: String = "",
    val volumeWeightedAveragePrice: String = ""
)
