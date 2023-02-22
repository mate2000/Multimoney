package com.multimoney.domain.model.balance

data class BalanceCryptoAccount(
    val status: Int,
    val message: String?,
    val outOfService: Boolean,
    val globalBalance: Double?,
    val investedBalance: String?,
    val percentageInvested: String?,
    val items: List<BalanceCryptoAccountItems>?
)
