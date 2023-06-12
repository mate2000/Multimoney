package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.CivilStatusQuery
import com.multimoney.domain.model.accountsmart.CivilStatus
import com.multimoney.domain.model.accountsmart.CivilStatusResult

private fun CivilStatusQuery.Result.mapToDomainModel() =
    CivilStatus(maritalStatusId, maritalStatusCode, maritalStatusDescription)

private fun CivilStatusQuery.CivilStatus.mapToDomainModel() =
    CivilStatusResult(status = result.map { it.mapToDomainModel() })

fun CivilStatusQuery.Data.mapToDomainModel() = civilStatus?.mapToDomainModel()
