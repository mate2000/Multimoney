package com.multimoney.domain.model.accountsmart

data class LocalFavorite(
    val idFavorite: Int?,
    val idCustomer: Int?,
    val idAccounType: Int?,
    val accountNumber: String,
    val accountName: String,
    val email: String,
    val activo: Boolean?,
    val phoneNumber: String,
    val idCurrencyAccount: Int?,
    val currencyAccount: String,
    val ibanNumber: String,
    val isFavorite: Boolean,
    val areaCode: String
)
