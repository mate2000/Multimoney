package com.multimoney.domain.model.security


data class CountryPhoneCodes(val countryPhoneCodes: List<CountryPhoneCodeItem>)

data class CountryPhoneCodeItem(
    val country: String,
    val code: Int,
    val isoCode: String
)
