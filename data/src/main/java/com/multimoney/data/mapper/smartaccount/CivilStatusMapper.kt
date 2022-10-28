package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.accountsmart.apollomodel.CivilStatusQuery
import com.multimoney.domain.model.accountsmart.CivilStatus
import com.multimoney.domain.model.accountsmart.CivilStatusResult

private fun CivilStatusQuery.Result.mapToDomain() =
    CivilStatus(maritalStatusId, maritalStatusCode, maritalStatusDescription)

private fun CivilStatusQuery.CivilStatus.mapToDomain() =
    CivilStatusResult(status = result?.map { it?.mapToDomain() } ?: listOf())

fun CivilStatusQuery.Data.mapToDomain() = civilStatus?.mapToDomain()