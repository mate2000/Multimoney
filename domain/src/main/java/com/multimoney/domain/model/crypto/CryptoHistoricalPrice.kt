package com.multimoney.domain.model.crypto

data class CryptoHistoricalPrice(
    val items: List<CurrencyHistoricPrice>,
    val total_count: String
)