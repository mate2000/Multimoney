package com.multimoney.domain.model.credit

data class GetInfoDeposit(
    val accountNumber: String?,
    val amount: String?,
    val amountLabel: String?,
    val referenceNumber: String?,
    val creditNumber: String?,
    val date: String?,
    var message: String?,
    var status: Int?,
    var detail: String?
)
