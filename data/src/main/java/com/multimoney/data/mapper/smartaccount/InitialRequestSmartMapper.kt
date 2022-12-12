package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.InitialRequestSmartAccountMutation
import com.multimoney.domain.model.accountsmart.GlobalRequest

private fun InitialRequestSmartAccountMutation.Result.mapToDomain() =
    GlobalRequest(
        idGlobalRequest.toString().toInt(),
        accountExists,
        idSysdeRequest.toString().toInt()
    )

private fun InitialRequestSmartAccountMutation.InitialRequestSmartAccount.mapToDomainModel() =
    this.result.mapToDomain()

fun InitialRequestSmartAccountMutation.Data.mapToDomainModel() =
    this.initialRequestSmartAccount?.mapToDomainModel()