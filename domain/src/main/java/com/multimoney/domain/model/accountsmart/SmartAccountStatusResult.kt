package com.multimoney.domain.model.accountsmart

data class SmartAccountStatusResult(
    val requestID: Int,
    val message: String,
    val status: String?,
    val urlFirmDocument: String?
)
