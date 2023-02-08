package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.RelatedContactsByPhoneQuery
import com.multimoney.domain.model.accountsmart.PhoneSmart
import com.multimoney.domain.model.accountsmart.PhonesResult

private fun RelatedContactsByPhoneQuery.RelatedContactsByPhone.mapToDomainModel() = PhonesResult(
    phones = result?.phones?.map { it?.mapToDomainModel() }
)

fun RelatedContactsByPhoneQuery.Data.mapToDomainModel() = relatedContactsByPhone?.mapToDomainModel()

fun RelatedContactsByPhoneQuery.Phone.mapToDomainModel() = PhoneSmart(
    number = number,
    titular = titular,
    bankName = bankName,
    identification = identification,
    accountNumber = accountNumber,
    email = email,
    idCurrency = id_Currency,
    currency = currency,
    ibanNumber = ibanNumber
)
