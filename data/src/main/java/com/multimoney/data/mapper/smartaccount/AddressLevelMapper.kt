package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.AddressLevel1Query
import com.multimoney.data.networking.graphql.apollomodel.AddressLevel2Query
import com.multimoney.data.networking.graphql.apollomodel.AddressLevel3Query
import com.multimoney.domain.model.accountsmart.Address
import com.multimoney.domain.model.accountsmart.AddressesLevel

private fun AddressLevel1Query.Result.mapToDomainModel() = Address(id, name, code)

private fun AddressLevel2Query.Result.mapToDomainModel() = Address(id, name, code)

private fun AddressLevel3Query.Result.mapToDomainModel() = Address(id, name, code)

private fun AddressLevel1Query.AddressLevel1.mapToDomainModel() =
    AddressesLevel(addresses = result.map { it.mapToDomainModel() })

private fun AddressLevel2Query.AddressLevel2.mapToDomainModel() =
    AddressesLevel(addresses = result.map { it.mapToDomainModel() })

private fun AddressLevel3Query.AddressLevel3.mapToDomainModel() =
    AddressesLevel(addresses = result.map { it.mapToDomainModel() })

fun AddressLevel2Query.Data.mapToDomainModel() = addressLevel2?.mapToDomainModel()

fun AddressLevel1Query.Data.mapToDomainModel() = addressLevel1?.mapToDomainModel()

fun AddressLevel3Query.Data.mapToDomainModel() = addressLevel3?.mapToDomainModel()
