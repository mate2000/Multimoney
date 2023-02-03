package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.GetSmartAccountsQuery
import com.multimoney.domain.model.accountsmart.AccountSmartForBuyCrypto

fun GetSmartAccountsQuery.Data.mapToDomainModel() = balanceAccountSmart?.accounts?.map {
    it.mapToDomainModel()
}

fun GetSmartAccountsQuery.Account.mapToDomainModel() = AccountSmartForBuyCrypto(
    totalBalance = this.totalBalance.toString().toDouble(),
    currencyCode = this.currencyCode,
    idCurrencyAccount = this.idCurrencyAccount.toString().toInt(),
    accountToken = this.tokenNumber
)