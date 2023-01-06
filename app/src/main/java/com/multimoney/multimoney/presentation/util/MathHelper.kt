package com.multimoney.multimoney.presentation.util

import com.multimoney.domain.model.crypto.CurrencyHistoricPrice
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

fun calculateGainLosesMarketDetails(
    currentBalance: Double,
    listOfBalance: List<CurrencyHistoricPrice>
): Double {

    if (listOfBalance.isEmpty()) {
        return 0.0
    }
    val firstBalance = listOfBalance.first().average_price.toDouble()
    return currentBalance - firstBalance
}

fun calculatePercentageMarketDetails(
    currentBalance: Double,
    listOfBalance: List<CurrencyHistoricPrice>
): Double {
    if (listOfBalance.isEmpty()) {
        return 0.0
    }
    val firstBalance = listOfBalance.first().average_price.toDouble()
    return ((currentBalance - firstBalance) / currentBalance) * 100
}