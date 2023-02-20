package com.multimoney.domain.model.accountsmart

data class LocalSACAccount(
    val idFavorite: Long?,
    val idCustomer: Long?,
    val idAccountType: Int?,
    val accountNumber: String?,
    val accountName: String?,
    val phoneNumber: String?,
    val email: String?,
    val idCurrency: Int?,
    val currency: String?,
    val ibanNumber: String?,
    val isFavorite: Boolean?
)

data class LocalTransferFavorite(val accounts: List<LocalSACAccount>?)
