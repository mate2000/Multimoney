package com.multimoney.data.mapper.crypto

import com.multimoney.data.networking.graphql.apollomodel.GetBalanceCryptoAccountQuery
import com.multimoney.domain.model.balance.BalanceCryptoAccount
import com.multimoney.domain.model.balance.BalanceCryptoAccountItems

fun GetBalanceCryptoAccountQuery.Data.mapToDomainModel() = BalanceCryptoAccount(
    globalBalance = balanceCryptoAccount?.globalBalance.toString().toDouble(),
    investedBalance = balanceCryptoAccount?.investedBalance.toString(),
    percentageInvested = balanceCryptoAccount?.percentageInvested.toString(),
    items = balanceCryptoAccount?.items?.map { it.mapToDomainModel() } ?: emptyList()
)

private fun GetBalanceCryptoAccountQuery.Item.mapToDomainModel() = BalanceCryptoAccountItems(
    asset = asset.toString(),
    available = available.toString().toDouble(),
    trading = trading.toString(),
    descriptionCurrency = descriptionCurrency.toString(),
    balanceDollars = balanceDollars.toString().toDouble(),
    investedBalanceCurrency = investedBalanceCurrency.toString(),
    percentageInvestedCurrency = percentageInvestedCurrency.toString(),
    priceOfTheDay = priceOfTheDay.toString().toDouble(),
    url_image = url_image.toString()
)