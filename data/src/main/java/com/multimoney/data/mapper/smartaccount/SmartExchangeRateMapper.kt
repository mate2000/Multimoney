package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.ExchangeRateQuery

private fun ExchangeRateQuery.GetExchangeRate.mapToDomainModel() = result.exchangeRate

fun ExchangeRateQuery.Data.mapToDomainModel() = getExchangeRate?.mapToDomainModel()