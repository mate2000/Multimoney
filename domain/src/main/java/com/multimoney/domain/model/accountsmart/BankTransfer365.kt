package com.multimoney.domain.model.accountsmart

data class BankTransfer365(
    val bankId: Long?,
    val bankName: String?
)

data class BankListTransfer365(val bankList: List<BankTransfer365>?)
