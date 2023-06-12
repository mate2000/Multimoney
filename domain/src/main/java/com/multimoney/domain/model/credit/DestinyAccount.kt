package com.multimoney.domain.model.credit

data class DestinyAccount(
    val destinyAccountNumber: String,
    val destinyCurrencyId: String,
    val destinyAmount: Double?
)
