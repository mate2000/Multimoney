package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.LocalTransferFavoriteQuery
import com.multimoney.domain.model.accountsmart.LocalFavorite

private fun LocalTransferFavoriteQuery.Result.mapToDomainModel() = LocalFavorite(
    idFavorite = idFavorite.toString().toIntOrNull(),
    idCustomer = idCustomer.toString().toIntOrNull(),
    idAccounType = idAccounType,
    accountNumber = accountNumber,
    accountName = accountName,
    email = email,
    activo = activo,
    phoneNumber = phoneNumber,
    idCurrencyAccount = idCurrencyAccount,
    currencyAccount = currencyAccount,
    ibanNumber = ibanNumber,
    isFavorite = isFavorite,
    areaCode = areaCode
)

private fun LocalTransferFavoriteQuery.LocalTransferFavorite.mapToDomainModel() =
    result?.map { it.mapToDomainModel() }

fun LocalTransferFavoriteQuery.Data.mapToDomainModel() =
    this.localTransferFavorite?.mapToDomainModel()
