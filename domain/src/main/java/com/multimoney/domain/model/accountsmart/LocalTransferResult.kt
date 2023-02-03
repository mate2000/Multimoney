package com.multimoney.domain.model.accountsmart

import com.multimoney.domain.model.util.error.MessageError

data class LocalTransferResult(
    val authorization: String,
    val currentBalance: Double,
    val destinationTitularName: String,
    val originAccountNumber: String,
    val messageError: MessageError
)
