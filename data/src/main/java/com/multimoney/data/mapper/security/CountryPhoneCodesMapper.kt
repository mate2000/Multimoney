package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.GetCountryPhoneCodesQuery
import com.multimoney.domain.model.security.CountryPhoneCodeItem
import com.multimoney.domain.model.security.CountryPhoneCodes

fun GetCountryPhoneCodesQuery.Data.mapToDomainModel() = CountryPhoneCodes(
    countryPhoneCodes = countryPhoneCodes.map {
        CountryPhoneCodeItem(
            code = it.code,
            country = it.country
        )
    }
)