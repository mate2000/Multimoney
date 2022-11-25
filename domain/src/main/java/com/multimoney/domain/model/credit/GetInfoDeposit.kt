package com.multimoney.domain.model.credit

data class GetInfoDeposit(
    val accountNumber: String?,
    val amount: String?,
    val referenceNumber: String?,
    val data: String?,
    var message: String?,
    var status: Int?,
    var detail: String?
)
