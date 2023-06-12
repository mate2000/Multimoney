package com.multimoney.domain.model.crypto

data class GetHistoricalClientBalance(
    val historicalBalanceClient: List<HistoricalBalanceClient>
)