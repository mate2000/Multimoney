package com.multimoney.domain.model.balance

data class Balance(
    val balanceCredit: List<BalanceCredit?>?,
    val balanceAccountSmart: List<Account?>?,
    val balanceCryptoAccount: BalanceCryptoAccount?,
    var balanceCardInformation: BalanceCardInformation?
) {
    fun getFirstCredit() = balanceCredit?.firstOrNull()

    fun getFirstSummary() = getFirstCredit()?.summary?.firstOrNull()

    fun getExpiredDays() = getFirstSummary()?.expiredDays ?: 1

    fun isBalanceCreditSummaryMultiple() = (getFirstCredit()?.summary?.size ?: 0) > 0

    fun isExpiredAutomaticDebitCard() = getFirstCredit()?.expiredAutomaticDebitCard ?: false
}
