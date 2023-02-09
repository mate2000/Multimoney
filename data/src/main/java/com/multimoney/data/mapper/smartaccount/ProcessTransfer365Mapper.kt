package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.ProcessTransfer365MobileMutation
import com.multimoney.data.networking.graphql.apollomodel.ProcessTransfer365Mutation
import com.multimoney.domain.model.accountsmart.BankAuthorization
import com.multimoney.domain.model.accountsmart.Transfer365Result
import com.multimoney.domain.model.util.error.MessageError

private fun ProcessTransfer365Mutation.BankAuthorization.mapToDomainModel() = BankAuthorization(
    referenceNumber = authorizationId
)

private fun ProcessTransfer365Mutation.Transfer365.mapToDomainModel() = Transfer365Result(
    bankAuthorization = result?.bankAuthorization?.mapToDomainModel(),
    messageError = MessageError(
        status = status,
        message = message,
        detail = detail
    )
)

fun ProcessTransfer365Mutation.Data.mapToDomainModel() = transfer365?.mapToDomainModel()

private fun ProcessTransfer365MobileMutation.BankAuthorization.mapToDomainModel() =
    BankAuthorization(
        referenceNumber = authorizationId
    )

private fun ProcessTransfer365MobileMutation.TransferMovil365.mapToDomainModel() = Transfer365Result(
    bankAuthorization = result?.bankAuthorization?.mapToDomainModel(),
    messageError = MessageError(
        status = status,
        message = message,
        detail = detail
    )
)

fun ProcessTransfer365MobileMutation.Data.mapToDomainModel() = transferMovil365?.mapToDomainModel()