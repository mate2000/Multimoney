package com.multimoney.domain.model.credit

data class PaymentPoint(
    val name: String?,
    val description: String?,
    val address: String?,
    val addressDescription: String?,
    val schedule: String?,
    val latitude: String?,
    val longitude: String?
)
