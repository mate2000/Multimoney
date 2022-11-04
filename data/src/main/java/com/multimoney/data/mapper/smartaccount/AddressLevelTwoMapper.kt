package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.AddressLevel2Query
import com.multimoney.domain.model.accountsmart.AddressLevelTwo
import com.multimoney.domain.model.accountsmart.AddressesLevelTwo

private fun AddressLevel2Query.AddressLevel2.mapToDomainModel() =
    result.map { AddressLevelTwo(it.id, it.name, it.code) }

fun AddressLevel2Query.Data.mapToDomainModel() =
    AddressesLevelTwo(addresses = addressLevel2?.mapToDomainModel() ?: listOf())
