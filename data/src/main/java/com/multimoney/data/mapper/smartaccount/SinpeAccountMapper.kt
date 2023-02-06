package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.ListSinpeAccountQuery
import com.multimoney.domain.model.accountsmart.SinpeAccount
import com.multimoney.domain.model.accountsmart.SinpeAccountResult

private fun ListSinpeAccountQuery.Result.mapToDomainModel() = SinpeAccount(
    accountId.toString().toInt(),
    country,
    bank,
    clientIdentification,
    sinpeAccount,
    active,
    currencyId.toString().toInt(),
    currency,
    nameAccount,
    idBank.toLong(),
    typeAccount.toString().toLong(),
    isFavorite
)

private fun ListSinpeAccountQuery.ListSinpeAccount.mapToDomainModel() = SinpeAccountResult(data = result.map { it.mapToDomainModel() })

fun ListSinpeAccountQuery.Data.mapToDomainModel() = this.listSinpeAccount?.mapToDomainModel()


