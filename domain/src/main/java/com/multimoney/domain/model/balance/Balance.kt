package com.multimoney.domain.model.balance

data class Balance(
    val balanceCredit: List<BalanceCredit?>?,
    val balanceAccountSmart: BalanceAccountSmart?,
    val balanceCryptoAccount: BalanceCryptoAccount?,
    val balanceCardInformation: BalanceCardInformation?
){
    fun getBalanceCredit() = balanceCredit?.firstOrNull()

    fun getSummary() = getBalanceCredit()?.summary?.firstOrNull()

    fun isExpired() = (getSummary()?.daysExpired ?: 0) > 0
}