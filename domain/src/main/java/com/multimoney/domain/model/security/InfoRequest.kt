package com.multimoney.domain.model.security

data class InfoRequest(
    val idRequestSysde: Long,
    val idRequestGlobal: Long,
    var currentStep: String?,
    val statusRequest: String?
)