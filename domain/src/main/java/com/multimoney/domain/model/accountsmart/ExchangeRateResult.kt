package com.multimoney.domain.model.accountsmart

data class ExchangeRateResult(
    val amount: Double?,
    val exchangeRate: Double?,
    val convertedAmount: Double?,
    val amountLabel: String?,
    val exchangeRateLabel: String?,
    val convertedAmountLabel: String?
)
