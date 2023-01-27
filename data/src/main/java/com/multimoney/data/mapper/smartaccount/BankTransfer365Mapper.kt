package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.BankListTransfer365Query
import com.multimoney.domain.model.accountsmart.BankListTransfer365
import com.multimoney.domain.model.accountsmart.BankTransfer365

private fun BankListTransfer365Query.BankList365Type.mapToDomainModel() = BankTransfer365(
    bankId = bankId.toString().toLongOrNull(),
    bankName = name
)

fun BankListTransfer365Query.Data.mapToDomainModel() =
    BankListTransfer365(bankList = bankList365Type?.map { it.mapToDomainModel() })