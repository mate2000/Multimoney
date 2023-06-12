package com.multimoney.domain.model.credit

import com.multimoney.domain.model.accountsmart.BankTransfer365
import com.multimoney.domain.model.accountsmart.SmartAccountTypeResult

data class BankList365TypeAndAccountType(
    val bankList365Type: List<BankTransfer365>?,
    val accountType: SmartAccountTypeResult?
)
