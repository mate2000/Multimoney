package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.AddACHAccountMutation
import com.multimoney.domain.model.accountsmart.ACHAccount

private fun AddACHAccountMutation.Result.mapToDomainModel() = ACHAccount(
    accountForAchTransferId = accountForAchTransferId.toString().toIntOrNull(),
    accountNumber = accountNumber,
    description = description,
    destinationBankDescription = destinationBankDescription,
    idBank = destinationBankId.toString().toIntOrNull(),
    idTypeAccount = typeAccountId.toString().toIntOrNull(),
    isFavorite = isFavorite,
    identificationTypeAccount = identificationTypeAccount
)

private fun AddACHAccountMutation.ACHTransferFavoriteAdd.mapToDomainModel() =
    result.mapToDomainModel()

fun AddACHAccountMutation.Data.mapToDomainModel() = aCHTransferFavoriteAdd?.mapToDomainModel()
