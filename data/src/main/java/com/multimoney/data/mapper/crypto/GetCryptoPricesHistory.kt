package com.multimoney.data.mapper.crypto

import com.multimoney.data.networking.graphql.apollomodel.GetHistoricalCurrencyPricesQuery
import com.multimoney.domain.model.crypto.CryptoHistoricalPrice
import com.multimoney.domain.model.crypto.CurrencyHistoricPrice
import com.multimoney.domain.model.crypto.GetHistoricalCurrencyPrices


fun GetHistoricalCurrencyPricesQuery.Data.mapToDomainModel() = GetHistoricalCurrencyPrices(
    cryptoHistoricalPrice = cryptoHistoricalPrice.mapToDomainModel()
)

fun GetHistoricalCurrencyPricesQuery.CryptoHistoricalPrice.mapToDomainModel() = CryptoHistoricalPrice(
    items = items.map { it.mapToDomainModel() },
    total_count = total_count
)

private fun GetHistoricalCurrencyPricesQuery.Item.mapToDomainModel() = CurrencyHistoricPrice(
    average_price = average_price,
    timestamp = timestamp
)
