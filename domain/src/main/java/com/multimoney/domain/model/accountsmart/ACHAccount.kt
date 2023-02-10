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
    val isFavorite: Boolean? = null,
)
