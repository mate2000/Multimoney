package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.SmartAccountTypeQuery
import com.multimoney.domain.model.accountsmart.SmartAccountType
import com.multimoney.domain.model.accountsmart.SmartAccountTypeResult

private fun SmartAccountTypeQuery.Result.mapToDomainModel() = SmartAccountType(
    typeId = id,
    typeName = name
)

private fun SmartAccountTypeQuery.TypeAccount.mapToDomainModel() =
    SmartAccountTypeResult(typeList = result.map { it.mapToDomainModel() })

fun SmartAccountTypeQuery.Data.mapToDomainModel() = typeAccount?.mapToDomainModel()