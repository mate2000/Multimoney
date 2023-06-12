package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.ACHTransferFavoriteDeleteMutation
import com.multimoney.domain.model.accountsmart.ACHAccount

private fun ACHTransferFavoriteDeleteMutation.Result.mapToDomainModel() = ACHAccount(
    accountForAchTransferId = accountForAchTransferId.toString().toIntOrNull(),
    accountNumber = accountNumber,
    description = description,
    destinationBankDescription = destinationBankDescription,
    idBank = destinationBankId.toString().toIntOrNull(),
    idTypeAccount = typeAccountId.toString().toIntOrNull(),
    isFavorite = isFavorite,
    identificationTypeAccount = identificationTypeAccount
)

private fun ACHTransferFavoriteDeleteMutation.ACHTransferFavoriteDelete.mapToDomainModel() =
    result.mapToDomainModel()

fun ACHTransferFavoriteDeleteMutation.Data.mapToDomainModel() = aCHTransferFavoriteDelete?.mapToDomainModel()
