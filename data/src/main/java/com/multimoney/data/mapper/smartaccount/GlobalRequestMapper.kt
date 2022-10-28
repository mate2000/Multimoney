package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.accountsmart.apollomodel.GlobalRequestMutation
import com.multimoney.domain.model.accountsmart.GlobalRequest

private fun GlobalRequestMutation.Result.mapToDomain() = GlobalRequest(idGlobalRequest.toString().toInt(),
    accountExists,
    idSysdeRequest.toString().toInt())

private fun GlobalRequestMutation.GlobalRequest.mapToDomain() = this.result?.mapToDomain()

fun GlobalRequestMutation.Data.mapToDomain() = this.globalRequest?.mapToDomain()