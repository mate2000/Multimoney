package com.multimoney.domain.model.accountsmart

data class SmartMovementsResult(
    val totalRecords: Int,
    val result: List<SmartMovement>,
    var accountToken: Long = 0
)
