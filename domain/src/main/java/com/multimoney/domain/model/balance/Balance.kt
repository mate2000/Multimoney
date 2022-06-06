package com.multimoney.domain.model.balance

data class Balance(
    val balanceCredit: List<BalanceCredit?>?,
    val balanceAccountSmart: BalanceAccountSmart?,
    val balanceCryptoAccount: BalanceCryptoAccount?,
    val balanceCardInformation: BalanceCardInformation?
)