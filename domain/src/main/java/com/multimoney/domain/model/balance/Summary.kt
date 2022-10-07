package com.multimoney.domain.model.balance

data class Summary(
    val idCurrency: Int?,
    val currency: String?,
    val currentBalanceLabel: String?,
    val availableBalance: String?,
    val availableBalanceLabel: String?,
    val paymentDateLabel: String?,
    val monthlyQuota: String?,
    val monthlyQuotaLabel: String?,
    val balanceAmountCancel: String
)
