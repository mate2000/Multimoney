package com.multimoney.data.mapper.crypto

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