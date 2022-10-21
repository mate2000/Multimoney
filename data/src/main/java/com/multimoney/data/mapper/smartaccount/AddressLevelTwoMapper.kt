package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.accountsmart.apollomodel.AddressLevel2Query
import com.multimoney.domain.model.accountsmart.AddressLevelTwo
import com.multimoney.domain.model.accountsmart.AddressesLevelTwo

private fun AddressLevel2Query.Result.mapToDomain() = AddressLevelTwo(id, name, code)

private fun AddressLevel2Query.AddressLevel2.mapToDomain() =
    AddressesLevelTwo(addresses = result?.map { it?.mapToDomain() } ?: listOf())

fun AddressLevel2Query.Data.mapToDomain() = addressLevel2?.mapToDomain()