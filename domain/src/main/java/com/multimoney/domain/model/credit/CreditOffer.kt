package com.multimoney.domain.model.credit

data class CreditOffer(
    val idUserRequest: Int,
    val products: List<Product?>?,
    val isCrosseling: Boolean
)
