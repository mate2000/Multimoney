package com.multimoney.domain.model.security

data class InfoCrypto(
    val status: Int,
    val statusFirm: String,
    val profileEnable: Boolean?,
    val wording: Wording?
)
