package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.NationalityQuery
import com.multimoney.domain.model.accountsmart.Nationalities
import com.multimoney.domain.model.accountsmart.Nationality

private fun NationalityQuery.Result.mapToDomainModel() = Nationality(id, name)

private fun NationalityQuery.Nationality.mapToDomainModel() =
    Nationalities(nationalityList = result.map { it.mapToDomainModel() })

fun NationalityQuery.Data.mapToDomainModel() = nationality?.mapToDomainModel()
