package com.multimoney.multimoney.presentation.util

import com.multimoney.domain.model.crypto.HistoricalBalanceClient

// fun to calculate gain loses based on the list of historical balance
fun calculateGainLoses(
    currentBalance: Double,
    listOfBalance: List<HistoricalBalanceClient>
): Double {

    if (listOfBalance.isEmpty()) {
        return 0.0
    }
    val lastBalance = listOfBalance.last().convertedBalance
    return lastBalance - currentBalance
}

fun calculatePercentage(
    currentBalance: Double,
    listOfBalance: List<HistoricalBalanceClient>
): Double {
    if (listOfBalance.isEmpty()) {
        return 0.0
    }
    val lastBalance = listOfBalance.last().convertedBalance
    return ((lastBalance - currentBalance) / currentBalance) * 100
}