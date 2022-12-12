package com.multimoney.domain.model.balance

data class BalanceCryptoAccount(
    val globalBalance: Double?,
    val investedBalance: String?,
    val percentageInvested: String?,
    val items: List<BalanceCryptoAccountItems?>
)