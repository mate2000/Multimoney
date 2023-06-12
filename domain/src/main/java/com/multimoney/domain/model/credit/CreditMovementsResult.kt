package com.multimoney.domain.model.credit

data class CreditMovementsResult(
    val month: String?,
    val result: List<CreditMovement>?
)
