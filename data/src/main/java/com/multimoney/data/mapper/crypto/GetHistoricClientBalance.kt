package com.multimoney.data.mapper.crypto

import com.multimoney.data.networking.graphql.apollomodel.GetCryptoPriceHistoryQuery
import com.multimoney.data.networking.graphql.apollomodel.GetHistoricClientBalanceQuery
import com.multimoney.domain.model.crypto.GetHistoricalClientBalance
import com.multimoney.domain.model.crypto.HistoricalBalanceClient

private fun GetHistoricClientBalanceQuery.HistoricalBalanceClient.mapToDomainModel() = HistoricalBalanceClient(
    convertedBalance = convertedBalance.toString().toDouble(),
    date = date.toString()
)

fun GetHistoricClientBalanceQuery.Data.mapToDomainModel() = GetHistoricalClientBalance(
    historicalBalanceClient = historicalBalanceClient.map { it.mapToDomainModel() }
)

fun GetCryptoPriceHistoryQuery.Data.mapToDomainModel() = GetHistoricalClientBalance(
    historicalBalanceClient = cryptoHistoricalPrice.items.map { HistoricalBalanceClient(
        convertedBalance = it.average_price.toDouble(),
        date = it.timestamp
    ) }
)