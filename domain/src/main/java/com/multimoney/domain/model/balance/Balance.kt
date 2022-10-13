package com.multimoney.domain.model.balance

data class Balance(
    val balanceCredit: List<BalanceCredit?>?,
    val balanceAccountSmart: BalanceAccountSmart?,
    val balanceCryptoAccount: BalanceCryptoAccount?,
    val balanceCardInformation: BalanceCardInformation?
) {
    fun getFirstCredit() = balanceCredit?.firstOrNull()

    fun getFirstSummary() = getFirstCredit()?.summary?.firstOrNull()

    fun getExpiredDays() = getFirstSummary()?.expiredDays ?: 0
}