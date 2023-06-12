package com.multimoney.domain.model.crypto

data class ValidateDepositAddressResult(
    val page: Int,
    val totalPages: Int,
    val itemsOnPage: Int,
    val address: String,
    val balance: String,
    val unconfirmedBalance: String,
    val unconfirmedTxs: Int,
    val txs: Int,
    val nonTokenTxs: Int
)
