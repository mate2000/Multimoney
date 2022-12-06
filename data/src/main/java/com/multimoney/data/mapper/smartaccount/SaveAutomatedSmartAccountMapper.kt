package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.SaveAutomatedSmartAccountMutation
import com.multimoney.domain.model.accountsmart.SaveSmartAccount

private fun SaveAutomatedSmartAccountMutation.Result.mapToDomainModel() =
    SaveSmartAccount(
        message = message
    )

private fun SaveAutomatedSmartAccountMutation.SaveAutomatedSmartAccount.mapToDomainModel() =
    this.result.mapToDomainModel()

fun SaveAutomatedSmartAccountMutation.Data.mapToDomainModel() = saveAutomatedSmartAccount?.mapToDomainModel()
