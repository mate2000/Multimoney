package com.multimoney.domain.model.accountsmart

data class SmartFavoriteData(
    val idFavorite: Long?,
    val idCustomer: Long?,
    val idAccountType: Int?,
    val accountNumber: String?,
    val accountName: String?,
    val email: String?,
    val active: Boolean?,
    val phoneNumber: String?,
    val idCurrencyAccount: Int?,
    val currencyAccount: String?
)

data class SmartFavoriteResult(val results: List<SmartFavoriteData?>?)
