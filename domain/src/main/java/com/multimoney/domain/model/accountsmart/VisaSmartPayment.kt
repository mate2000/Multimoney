package com.multimoney.domain.model.accountsmart

import com.multimoney.domain.model.util.error.MessageError

data class VisaSmartPayment(
    val referenceNumber: String?,
    val referenceNumberVisa: String?,
    val messageError: MessageError
)
