package com.multimoney.domain.model.accountsmart

data class ACHAccount(
    val accountForAchTransferId: Int?,
    val accountNumber: String?,
    val description: String? = null,
    val destinationBankDescription: String? = null,
    val destinationAccountCurrencyId: Int? = null,
    val destinationAccountCurrency: String? = null,
    val idBank: Int? = null,
    val idTypeAccount: Int? = null,
    val identificationTypeAccount: Int? = null,
    val isFavorite: Boolean? = null
)
