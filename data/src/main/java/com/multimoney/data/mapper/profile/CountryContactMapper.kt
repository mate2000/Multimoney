package com.multimoney.data.mapper.profile

import com.multimoney.data.networking.graphql.apollomodel.GetCountryContactQuery
import com.multimoney.domain.model.profile.CountryContact

private fun GetCountryContactQuery.GetCountryContact.mapToDomainModel() = CountryContact(
    whatsappLink = this.whatsappLink,
    customerServicesPhone = this.customerServicesPhone
)

fun GetCountryContactQuery.Data.mapToDomainModel() = getCountryContact.mapToDomainModel()