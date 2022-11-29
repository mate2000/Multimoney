package com.multimoney.domain.model.balance

data class BalanceCryptoAccountItems(
    val asset: String,
    val available: Double,
    val trading: String,
    val descriptionCurrency: String,
    val balanceDollars: Double,
    val investedBalanceCurrency: String,
    val percentageInvestedCurrency: String,
    val priceOfTheDay: Double,
    val url_image: String
)