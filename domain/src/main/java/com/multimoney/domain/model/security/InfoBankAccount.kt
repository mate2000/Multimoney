package com.multimoney.domain.model.security

data class InfoBankAccount(
    val statusFirm: String?,
    val status: Int,
    val infoRequest: InfoRequest,
    val wording: Wording?
)