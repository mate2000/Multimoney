package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.GlobalRequestMutation
import com.multimoney.domain.model.accountsmart.GlobalRequest

private fun GlobalRequestMutation.Result.mapToDomainModel() = GlobalRequest(
    idGlobalRequest.toString().toLongOrNull() ?: 0,
    accountExists,
    idSysdeRequest.toString().toIntOrNull() ?: 0
)

private fun GlobalRequestMutation.GlobalRequest.mapToDomainModel() = this.result.mapToDomainModel()

fun GlobalRequestMutation.Data.mapToDomainModel() = this.globalRequest?.mapToDomainModel()
