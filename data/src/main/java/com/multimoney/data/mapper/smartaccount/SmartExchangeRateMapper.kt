package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.ExchangeRateQuery
import com.multimoney.domain.model.accountsmart.ExchangeRateResult

private fun ExchangeRateQuery.GetExchangeRate.mapToDomainModel() = ExchangeRateResult(
    amount = result.amount as? Double,
    exchangeRate = result.exchangeRate,
    convertedAmount = result.convertedAmount as? Double,
    amountLabel = result.amount_label,
    exchangeRateLabel = result.exchangeRate_label,
    convertedAmountLabel = result.convertedAmount_label,
)

fun ExchangeRateQuery.Data.mapToDomainModel() = getExchangeRate?.mapToDomainModel()