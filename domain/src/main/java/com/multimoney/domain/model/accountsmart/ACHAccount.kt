package com.multimoney.domain.model.accountsmart

data class ACHAccount(
    val accountForAchTransferId: Int?,
    val accountNumber: String?,
    val description: String = "",
    val destinationBankDescription: String = "",
    val destinationAccountCurrencyId: Int = 0,
    val destinationAccountCurrency: String = "",
    val idBank: Int = 0,
    val idTypeAccount: Int = 0,
    val isFavorite: Boolean? = null
)

data class ACHAccountFull(
    val accountForAchTransferId: Int?,
    val accountNumber: String,
    val description: String,
    val titularName: String,
    val notificationEmail: String,
    val identificacionNumber: String,
    val isFavorite: Boolean,
    val typeAccountId: Int?,
    val destinationBankId: Int?,
    val typeAccountDescription: String,
    val destinationBankDescription: String,
    val status: Int,
    val destinationIdentificationBank: String,
    val transferState: Boolean,
    val accountTypeCode: String,
    val aCHTypeAccountCode: String,
    val aCHPurposeType: String,
    val aPIACHAccountCode: String,
    val identificationNumberAccount: String,
    val identificationTypeAccount: Int,
    val destinationAccountCurrencyId: Int?,
    val destinationAccountCurrency: String
)
