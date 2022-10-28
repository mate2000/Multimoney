package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.accountsmart.apollomodel.ProfessionQuery
import com.multimoney.domain.model.accountsmart.Profession
import com.multimoney.domain.model.accountsmart.Professions

private fun ProfessionQuery.Result.mapToDomain() = Profession(id, name)

private fun ProfessionQuery.Profession.mapToDomain() =
    Professions(status = result?.map { it?.mapToDomain() } ?: listOf())

fun ProfessionQuery.Data.mapToDomain() = profession?.mapToDomain()