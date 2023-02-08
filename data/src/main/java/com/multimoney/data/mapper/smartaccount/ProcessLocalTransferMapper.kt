package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.ProcessLocalTransferMutation
import com.multimoney.domain.model.accountsmart.LocalTransferResult
import com.multimoney.domain.model.util.error.MessageError

private fun ProcessLocalTransferMutation.ProcessLocalTransfer.mapToDomainModel(): LocalTransferResult {
    return LocalTransferResult(
        authorization = result?.authorization ?: "",
        currentBalance = result?.currentBalance.toString().toDoubleOrNull() ?: 0.0,
        destinationTitularName = result?.destinationTitularName ?: "",
        originAccountNumber = result?.originAccountNumber ?: "",
        messageError = MessageError(
            status = status,
            message = message,
            detail = detail
        )
    )
}

fun ProcessLocalTransferMutation.Data.mapToDomainModel() = processLocalTransfer?.mapToDomainModel()
