package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.accountsmart.apollomodel.AddressLevel1Query
import com.multimoney.data.networking.accountsmart.apollomodel.AddressLevel2Query
import com.multimoney.data.networking.accountsmart.apollomodel.AddressLevel3Query
import com.multimoney.domain.model.accountsmart.AddressLevelTwo
import com.multimoney.domain.model.accountsmart.AddressesLevel

private fun AddressLevel1Query.Result.mapToDomain() = AddressLevelTwo(id, name, code)

private fun AddressLevel2Query.Result.mapToDomain() = AddressLevelTwo(id, name, code)

private fun AddressLevel3Query.Result.mapToDomain() = AddressLevelTwo(id, name, code)

private fun AddressLevel1Query.AddressLevel1.mapToDomain() =
    AddressesLevel(addresses = result?.map { it?.mapToDomain() } ?: listOf())

private fun AddressLevel2Query.AddressLevel2.mapToDomain() =
    AddressesLevel(addresses = result?.map { it?.mapToDomain() } ?: listOf())

private fun AddressLevel3Query.AddressLevel3.mapToDomain() =
    AddressesLevel(addresses = result?.map { it?.mapToDomain() } ?: listOf())

fun AddressLevel2Query.Data.mapToDomain() = addressLevel2?.mapToDomain()

fun AddressLevel1Query.Data.mapToDomain() = addressLevel1?.mapToDomain()

fun AddressLevel3Query.Data.mapToDomain() = addressLevel3?.mapToDomain()