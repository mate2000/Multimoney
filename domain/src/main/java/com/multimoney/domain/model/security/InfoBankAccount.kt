package com.multimoney.domain.model.security

data class InfoBankAccount(
    var statusFirm: String?,
    val status: Int,
    val infoRequest: InfoRequest,
    val wording: Wording?
)