package com.multimoney.domain.model.credit

import com.multimoney.domain.model.util.error.MessageError

data class ProcessCreditExtensionDetail(
    val loanId: Int?,
    val messageError: MessageError? = null,
    val reference: String? = ""
)
