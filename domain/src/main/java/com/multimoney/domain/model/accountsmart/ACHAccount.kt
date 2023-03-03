package com.multimoney.domain.model.accountsmart

data class ACHAccount(
    val accountForAchTransferId: Int?,
    val accountNumber: String?,
    val description: String = "",
    val destinationBankDescription: String = "",
    val destinationAccountCurrencyId: Int? = null,
    val destinationAccountCurrency: String = "",
    val idBank: Int? = null,
    val idTypeAccount: Int? = null,
    val isFavorite: Boolean? = null,
    val identificationTypeAccount: Int? = null
)

data class ACHAccountFull(
    val accountForAchTransferId: Int?,
    val accountNumber: String,
    val description: String,
    val titularName: String,
    val notificationEmail: String,
    val identificacionNumber: String,
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
    val destinationAccountCurrencyId: Int?,
    val destinationAccountCurrency: String,
    val idBank: Int? = null,
    val idTypeAccount: Int? = null,
    val isFavorite: Boolean? = null,
    val identificationTypeAccount: Int? = null
)
