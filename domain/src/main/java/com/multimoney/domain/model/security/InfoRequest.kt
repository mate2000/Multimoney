package com.multimoney.domain.model.security

data class InfoRequest(
    val idRequestSysde: Long,
    val idRequestGlobal: Long,
    val currentStep: String?,
    val statusRequest: String?
)