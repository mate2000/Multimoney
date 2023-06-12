package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.InitialRequestSmartAccountMutation
import com.multimoney.domain.model.accountsmart.GlobalRequest

private fun InitialRequestSmartAccountMutation.Result.mapToDomain() =
    GlobalRequest(
        idGlobalRequest.toString().toLongOrNull() ?: 0,
        accountExists,
        idSysdeRequest.toString().toIntOrNull() ?: 0
    )

private fun InitialRequestSmartAccountMutation.InitialRequestSmartAccount.mapToDomainModel() =
    this.result.mapToDomain()

fun InitialRequestSmartAccountMutation.Data.mapToDomainModel() =
    this.initialRequestSmartAccount?.mapToDomainModel()
