package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.ProcessTransfer365Mutation
import com.multimoney.domain.model.accountsmart.Transfer365Result

private fun ProcessTransfer365Mutation.BankAuthorization.mapToDomainModel() = Transfer365Result(
    referenceNumber = authorizationId
)

private fun ProcessTransfer365Mutation.Transfer365.mapToDomainModel() = result?.bankAuthorization?.mapToDomainModel()

fun ProcessTransfer365Mutation.Data.mapToDomainModel() = transfer365?.mapToDomainModel()