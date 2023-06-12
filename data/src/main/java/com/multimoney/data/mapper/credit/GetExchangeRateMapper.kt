package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.GetExchangeRateCreditQuery
import com.multimoney.domain.model.credit.ExchangeRate
import com.multimoney.domain.model.credit.ExchangeRateResult

private fun GetExchangeRateCreditQuery.GetExchangeRateCredit.mapToDomainModel() = ExchangeRate(
    result = ExchangeRateResult(
        exchangeRate = result.exchangeRate.toString().toDouble(),
        amount = result.amount.toString().toDouble(),
        convertedAmount = result.convertedAmount.toString().toDouble()
    )
)

fun GetExchangeRateCreditQuery.Data.mapToDomainModel() = getExchangeRateCredit?.mapToDomainModel()
