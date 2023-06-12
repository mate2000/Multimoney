package com.multimoney.domain.model.accountsmart

data class ACHFavoriteAccount(
    val accountForAchTransferId: Int,
    val accountNumber: String,
    val description: String,
    val destinationBankDescription: String,
    val destinationAccountCurrencyId: Int,
    val destinationAccountCurrency: String,
    val idBank: Int,
    val idTypeAccount: Int,
    val isFavorite: Boolean?,
)
