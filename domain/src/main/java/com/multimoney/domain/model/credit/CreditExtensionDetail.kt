package com.multimoney.domain.model.credit

import com.multimoney.domain.model.util.error.MessageError

data class CreditExtensionDetail(
    val comissionDisbursement: String?,
    val quotaTotal: String?,
    val quotaMaximum: String?,
    val selectedAmount: String?,
    val nextPayment: String?,
    val fkFlowControl: Int,
    val messageError: MessageError
)
