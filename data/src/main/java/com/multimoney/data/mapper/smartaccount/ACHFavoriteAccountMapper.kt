package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.ACHTransferFavoriteGetQuery
import com.multimoney.data.networking.graphql.apollomodel.ACHTransferFavoriteListQuery
import com.multimoney.domain.model.accountsmart.ACHAccount
import com.multimoney.domain.model.accountsmart.ACHAccountFull
import com.multimoney.domain.model.accountsmart.FavoriteACHResult

private fun ACHTransferFavoriteListQuery.Result.mapToDomainModel() = ACHAccount(
    accountForAchTransferId = accountForAchTransferId.toString().toIntOrNull(),
    accountNumber = accountNumber,
    description = description,
    destinationBankDescription = destinationBankDescription,
    destinationAccountCurrencyId = destinationAccountCurrencyId.toString().toIntOrNull(),
    destinationAccountCurrency = destinationAccountCurrency,
    idBank = idBank.toString().toIntOrNull(),
    idTypeAccount = idTypeAccount.toString().toIntOrNull(),
    isFavorite = isFavorite,
    identificationTypeAccount = identificationTypeAccount.toString().toIntOrNull()
)

private fun ACHTransferFavoriteListQuery.ACHTransferFavoriteList.mapToDomainModel() =
    FavoriteACHResult(data = result.map { it.mapToDomainModel() })

fun ACHTransferFavoriteListQuery.Data.mapToDomainModel() = this.aCHTransferFavoriteList?.mapToDomainModel()

private fun ACHTransferFavoriteGetQuery.Result.mapToDomainModel() = ACHAccountFull(
    accountForAchTransferId = accountForAchTransferId.toString().toIntOrNull(),
    accountNumber = accountNumber,
    description = description,
    titularName = titularName,
    notificationEmail = notificationEmail,
    identificacionNumber = identificacionNumber,
    isFavorite = isFavorite,
    typeAccountId = typeAccountId.toString().toIntOrNull(),
    destinationBankId = destinationBankId.toString().toIntOrNull(),
    typeAccountDescription = typeAccountDescription,
    destinationBankDescription = destinationBankDescription,
    status = status,
    destinationIdentificationBank = destinationIdentificationBank,
    transferState = transferState,
    accountTypeCode = accountTypeCode,
    aCHTypeAccountCode = aCHTypeAccountCode,
    aCHPurposeType = aCHPurposeType,
    aPIACHAccountCode = aPIACHAccountCode,
    identificationNumberAccount = identificationNumberAccount,
    identificationTypeAccount = identificationTypeAccount,
    destinationAccountCurrencyId = destinationAccountCurrencyId.toString().toIntOrNull(),
    destinationAccountCurrency = destinationAccountCurrency,
)

private fun ACHTransferFavoriteGetQuery.ACHTransferFavoriteGet.mapToDomainModel() = result.mapToDomainModel()

fun ACHTransferFavoriteGetQuery.Data.mapToDomainModel() = this.aCHTransferFavoriteGet?.mapToDomainModel()
