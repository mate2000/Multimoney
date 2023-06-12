package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.BankList365TypeAndTypeAccountQuery
import com.multimoney.domain.model.accountsmart.BankTransfer365
import com.multimoney.domain.model.accountsmart.SmartAccountType
import com.multimoney.domain.model.accountsmart.SmartAccountTypeResult
import com.multimoney.domain.model.credit.BankList365TypeAndAccountType

private fun BankList365TypeAndTypeAccountQuery.BankList365Type.mapToDomainModel() = BankTransfer365(
    bankId = bankId.toString().toLong(),
    bankName = name
)

private fun BankList365TypeAndTypeAccountQuery.TypeAccount.mapToDomainModel() = SmartAccountTypeResult(
    typeList = result.map { it.mapToDomainModel() }
)

private fun BankList365TypeAndTypeAccountQuery.Result.mapToDomainModel() = SmartAccountType(
    typeId = id,
    typeName = name
)

fun BankList365TypeAndTypeAccountQuery.Data.mapToDomainModel() = BankList365TypeAndAccountType(
    bankList365Type = bankList365Type?.map { it.mapToDomainModel() },
    accountType = typeAccount?.mapToDomainModel()
)

