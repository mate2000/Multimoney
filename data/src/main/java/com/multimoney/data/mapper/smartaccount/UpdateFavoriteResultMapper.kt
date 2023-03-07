package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.UpdateFavoriteContactSmartMutation
import com.multimoney.domain.model.accountsmart.SmartFavoriteData
import com.multimoney.domain.model.accountsmart.SmartFavoriteResult

private fun UpdateFavoriteContactSmartMutation.Result.mapToDomainModel() = SmartFavoriteData(
    idFavorite = idFavorite.toString().toLong(),
    idCurrencyAccount = idCurrencyAccount,
    idAccountType = idAccounType,
    accountNumber = accountNumber,
    accountName = accountName,
    email = email,
    active = activo,
    phoneNumber = phoneNumber,
    idCustomer = idCustomer.toString().toLong(),
    currencyAccount = currencyAccount,
    identification = identification
)

private fun UpdateFavoriteContactSmartMutation.LocalTransferFavoriteMan.mapToDomainModel() =
    SmartFavoriteResult(results = result?.map { it.mapToDomainModel() })

fun UpdateFavoriteContactSmartMutation.Data.mapToDomainModel() =
    localTransferFavoriteMan?.mapToDomainModel()
