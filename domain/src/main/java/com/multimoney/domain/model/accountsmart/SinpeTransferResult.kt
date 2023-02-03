package com.multimoney.domain.model.accountsmart

import com.multimoney.domain.model.util.error.MessageError

data class SinpeTransferResult(
    val referenceNumber: String?,
    val messageError: MessageError
)
