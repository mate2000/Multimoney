package com.multimoney.domain.model.crypto

data class GetPaxosChargeItem(
    val id: Int,
    val transaction: String,
    val key: String,
    val startAmount: Double,
    val finalAmount: Double,
    val value: Double,
    val type: String,
)