package com.multimoney.domain.model.credit

data class ExchangeRateResult(
    var exchangeRate: Double? = null,
    var amount: Double? = null,
    var convertedAmount: Double? = null
)
