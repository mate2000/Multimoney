package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.ProfessionSmartQuery
import com.multimoney.domain.model.accountsmart.ProfessionSmart
import com.multimoney.domain.model.accountsmart.Professions

private fun ProfessionSmartQuery.ProfessionSmart.mapToDomainModel() =
    Professions(status = result.map { ProfessionSmart(it.id, it.name) })

fun ProfessionSmartQuery.Data.mapToDomainModel() = professionSmart?.mapToDomainModel()
