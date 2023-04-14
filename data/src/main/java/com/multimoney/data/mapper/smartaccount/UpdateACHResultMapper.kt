package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.UpdateACHAccountMutation

private fun UpdateACHAccountMutation.Result.mapToDomainModel() = accountForAchTransferId.toString().toIntOrNull()

private fun UpdateACHAccountMutation.ACHTransferFavoriteUpdate.mapToDomainModel() =
    result.mapToDomainModel()

fun UpdateACHAccountMutation.Data.mapToDomainModel() = aCHTransferFavoriteUpdate?.mapToDomainModel()