package com.multimoney.domain.model.credit

data class ProcessPaymentList(
    val status: String?,
    val message: String?,
    val detail: String?,
    val rejectCodeSinpe: String?,
    val rejectCauseSinpe: String?,
    val responseMessage: String?,
    val internResponseMessage: String?,
    val internReferenceNumber: String?,
    val referenceNumberSinpe: String?
)
