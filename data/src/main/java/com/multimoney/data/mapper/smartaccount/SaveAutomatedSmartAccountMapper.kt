package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.SaveAutomatedSmartAccountMutation
import com.multimoney.domain.model.accountsmart.SaveSmartAccount

private fun SaveAutomatedSmartAccountMutation.Result.mapToDomainModel() =
    SaveSmartAccount(
        requestID = requestID.toString().toLong(),
        message = message,
        documentRoute = documentRoute,
        idAccount = idAccount.toString().toLong(),
        identification = identification
    )

private fun SaveAutomatedSmartAccountMutation.SaveAutomatedSmartAccount.mapToDomainModel() =
    this.result.mapToDomainModel()

fun SaveAutomatedSmartAccountMutation.Data.mapToDomainModel() = saveAutomatedSmartAccount?.mapToDomainModel()
