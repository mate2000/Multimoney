package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.ListSavedSACAccountsQuery
import com.multimoney.domain.model.accountsmart.LocalSACAccount
import com.multimoney.domain.model.accountsmart.LocalTransferFavorite

private fun ListSavedSACAccountsQuery.Result.mapToDomainModel() = LocalSACAccount(
    idFavorite = idFavorite.toString().toLongOrNull(),
    idCustomer = idCustomer.toString().toLongOrNull(),
    idAccountType = idAccounType,
    accountNumber = accountNumber,
    accountName = accountName,
    email = email,
    phoneNumber = phoneNumber,
    idCurrency = idCurrencyAccount,
    currency = currencyAccount,
    ibanNumber = ibanNumber,
    isFavorite = isFavorite,
    identification = identification
)

private fun ListSavedSACAccountsQuery.LocalTransferFavorite.mapToDomainModel() = LocalTransferFavorite(
    accounts = result?.map { it.mapToDomainModel() }
)

fun ListSavedSACAccountsQuery.Data.mapToDomainModel() = localTransferFavorite?.mapToDomainModel()