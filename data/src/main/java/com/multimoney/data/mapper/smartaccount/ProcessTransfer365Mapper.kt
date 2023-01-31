package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.ProcessTransfer365MobileMutation
import com.multimoney.data.networking.graphql.apollomodel.ProcessTransfer365Mutation
import com.multimoney.domain.model.accountsmart.Transfer365Result

private fun ProcessTransfer365Mutation.BankAuthorization.mapToDomainModel() = Transfer365Result(
    referenceNumber = authorizationId
)

private fun ProcessTransfer365Mutation.Transfer365.mapToDomainModel() =
    result?.bankAuthorization?.mapToDomainModel()

fun ProcessTransfer365Mutation.Data.mapToDomainModel() = transfer365?.mapToDomainModel()

private fun ProcessTransfer365MobileMutation.BankAuthorization.mapToDomainModel() =
    Transfer365Result(
        referenceNumber = authorizationId
    )

private fun ProcessTransfer365MobileMutation.TransferMovil365.mapToDomainModel() =
    result?.bankAuthorization?.mapToDomainModel()

fun ProcessTransfer365MobileMutation.Data.mapToDomainModel() = transferMovil365?.mapToDomainModel()