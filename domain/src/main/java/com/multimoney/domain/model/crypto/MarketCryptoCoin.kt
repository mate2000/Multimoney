package com.multimoney.domain.model.crypto

data class MarketCryptoCoin(
    val description: String,
    val baseAsset: String,
    val amountchange: String,
    val percentChange: String,
    val priority: Any,
    val currentPrice: Any,
    val url_image: String,
    val historico: Boolean,
    val cryptoNetwork: String?
)
