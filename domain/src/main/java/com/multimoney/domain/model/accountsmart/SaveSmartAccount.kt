package com.multimoney.domain.model.accountsmart

data class SaveSmartAccount(
    val requestID: Long,
    val message: String,
    val idAccount: Long,
    val documentRoute: String,
    val identification: String
)
