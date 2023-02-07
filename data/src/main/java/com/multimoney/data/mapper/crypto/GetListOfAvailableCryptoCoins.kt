package com.multimoney.data.mapper.crypto

import com.multimoney.data.networking.graphql.apollomodel.GetAvailableListOfCryptoCoinsQuery
import com.multimoney.domain.model.crypto.GetListOfAvailableCryptoCoins
import com.multimoney.domain.model.crypto.MarketCryptoCoin

fun GetAvailableListOfCryptoCoinsQuery.Data.mapToDomainModel() = GetListOfAvailableCryptoCoins(
    availableCryptoCoins = cryptoMarketPrice.map { it.mapToDomainModel() }
)

fun GetAvailableListOfCryptoCoinsQuery.CryptoMarketPrice.mapToDomainModel() = MarketCryptoCoin(
    description = description,
    baseAsset = baseAsset,
    amountchange = amountchange,
    percentChange = percentChange,
    priority = priority,
    currentPrice = currentPrice.toString().toDouble(),
    url_image = url_image,
    historico = historico,
    cryptoNetwork = crypto_network ?: ""
)