package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.accountsmart.apollomodel.NationalityQuery
import com.multimoney.domain.model.accountsmart.Nationalities
import com.multimoney.domain.model.accountsmart.Nationality

private fun NationalityQuery.Result.mapToDomain() = Nationality(id, name)

private fun NationalityQuery.Nationality.mapToDomain() =
    Nationalities(nationalityList = result?.map { it?.mapToDomain() } ?: listOf())

fun NationalityQuery.Data.mapToDomain() = nationality?.mapToDomain()