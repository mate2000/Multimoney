package com.multimoney.data.mapper.crypto

import com.multimoney.data.networking.graphql.apollomodel.GetCryptoCurrencyNewsQuery
import com.multimoney.domain.model.crypto.CryptoCurrencyNews
import com.multimoney.domain.model.crypto.CryptoNewsFeed
import com.multimoney.domain.model.crypto.New

fun GetCryptoCurrencyNewsQuery.Data.mapToDomainModel() = CryptoCurrencyNews(
    cryptoNewsFeed = cryptoNewsFeed.mapToDomainModel()
)

private fun GetCryptoCurrencyNewsQuery.CryptoNewsFeed.mapToDomainModel() = CryptoNewsFeed(
    information = information,
    result = result.map { it.mapToDomainModel() },
    title = title
)

private fun GetCryptoCurrencyNewsQuery.Result.mapToDomainModel() = New(
    link = link,
    title = title
)