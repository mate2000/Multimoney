package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.ACHTransferFavoriteListQuery
import com.multimoney.domain.model.accountsmart.ACHFavoriteAccount
import com.multimoney.domain.model.accountsmart.FavoriteACHResult

private fun ACHTransferFavoriteListQuery.Result.mapToDomainModel() = ACHFavoriteAccount(
    accountForAchTransferId.toString().toInt(),
    accountNumber,
    description,
    destinationBankDescription,
    destinationAccountCurrencyId.toString().toInt(),
    destinationAccountCurrency,
    idBank.toString().toInt(),
    idTypeAccount.toString().toInt(),
    isFavorite
)

private fun ACHTransferFavoriteListQuery.ACHTransferFavoriteList.mapToDomainModel() = FavoriteACHResult(data = result.map { it.mapToDomainModel() })

fun ACHTransferFavoriteListQuery.Data.mapToDomainModel() = this.aCHTransferFavoriteList?.mapToDomainModel()
