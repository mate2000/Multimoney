package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.ProcessLocalTransferMutation
import com.multimoney.domain.model.accountsmart.LocalTransferResult

private fun ProcessLocalTransferMutation.Result.mapToDomainModel() =
    LocalTransferResult(
        authorization = authorization ?: "",
        currentBalance = currentBalance.toString().toDoubleOrNull() ?: 0.0,
        destinationTitularName = destinationTitularName ?: "",
        originAccountNumber = originAccountNumber ?: ""
    )

private fun ProcessLocalTransferMutation.ProcessLocalTransfer.mapToDomainModel() =
    this.result?.mapToDomainModel()

fun ProcessLocalTransferMutation.Data.mapToDomainModel() = processLocalTransfer?.mapToDomainModel()
