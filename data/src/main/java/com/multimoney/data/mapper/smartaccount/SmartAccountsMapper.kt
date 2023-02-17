package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.GetSmartAccountsQuery
import com.multimoney.domain.model.accountsmart.SmartAccountSmall

fun GetSmartAccountsQuery.Data.mapToDomainModel() = balanceAccountSmart?.accounts?.map {
    it.mapToDomainModel()
}

fun GetSmartAccountsQuery.Account.mapToDomainModel() = SmartAccountSmall(
    totalBalance = this.totalBalance.toString().toDouble(),
    currencyCode = this.currencyCode,
    idCurrencyAccount = this.idCurrencyAccount.toString().toInt(),
    accountToken = this.tokenNumber,
    accountNumber = this.accountNumber,
    ibanAccountNumber = this.ibanAccountNumber
)