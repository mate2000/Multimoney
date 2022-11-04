package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.GlobalRequestMutation
import com.multimoney.domain.model.accountsmart.GlobalRequest

private fun GlobalRequestMutation.Result.mapToDomainModel() = GlobalRequest(
    idGlobalRequest.toString().toInt(),
    accountExists,
    idSysdeRequest.toString().toInt()
)

private fun GlobalRequestMutation.GlobalRequest.mapToDomainModel() = this.result.mapToDomainModel()

fun GlobalRequestMutation.Data.mapToDomainModel() = this.globalRequest?.mapToDomainModel()
