package com.multimoney.domain.model.accountsmart

data class SinpeAccount(
    val accountId: Int,
    val country: String,
    val bank: String,
    val clientIdentification: String,
    val sinpeAccount: String,
    val active: Boolean,
    val currencyId: Int,
    val currency: String,
    val nameAccount: String,
)
