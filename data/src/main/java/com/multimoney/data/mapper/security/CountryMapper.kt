package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.GetCountryQuery
import com.multimoney.domain.model.security.Country
import com.multimoney.domain.model.security.CountryList

fun GetCountryQuery.GetCountry.mapToDomainModel() =
    Country(idBrand.toString().toInt(), nombre, prefijoPais, descripcionPais)

fun GetCountryQuery.Data.mapToDomainModel() = CountryList(countryList = getCountry.map { it.mapToDomainModel() })
