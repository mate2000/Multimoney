package com.multimoney.domain.model.security

data class InfoPreApprove(
    val idUserRequest: Int?,
    val status: String?,
    val infoProducts: List<Product?>?,
    val idPrint: Long,
    val selectedAmount: Float?,
    val statusFirm: String?,
    val currentStep: String?
)