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
