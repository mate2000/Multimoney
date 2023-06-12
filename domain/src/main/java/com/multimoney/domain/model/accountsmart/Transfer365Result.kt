package com.multimoney.domain.model.accountsmart

import com.multimoney.domain.model.util.error.MessageError

data class Transfer365Result(
    val bankAuthorization: BankAuthorization?,
    val messageError: MessageError
)

data class BankAuthorization(val referenceNumber: String?)
