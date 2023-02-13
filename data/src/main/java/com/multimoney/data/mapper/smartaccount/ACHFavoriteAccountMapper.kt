package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.ACHTransferFavoriteListQuery
import com.multimoney.domain.model.accountsmart.ACHAccount
import com.multimoney.domain.model.accountsmart.FavoriteACHResult

private fun ACHTransferFavoriteListQuery.Result.mapToDomainModel() = ACHAccount(
    accountForAchTransferId = accountForAchTransferId.toString().toIntOrNull(),
    accountNumber = accountNumber,
    description = description,
    destinationBankDescription = destinationBankDescription,
    destinationAccountCurrencyId = destinationAccountCurrencyId.toString().toIntOrNull(),
    destinationAccountCurrency = destinationAccountCurrency,
    idBank = idBank.toString().toIntOrNull(),
    idTypeAccount = idTypeAccount.toString().toIntOrNull(),
    isFavorite = isFavorite
)

private fun ACHTransferFavoriteListQuery.ACHTransferFavoriteList.mapToDomainModel() =
    FavoriteACHResult(data = result.map { it.mapToDomainModel() })

fun ACHTransferFavoriteListQuery.Data.mapToDomainModel() =
    this.aCHTransferFavoriteList?.mapToDomainModel()
