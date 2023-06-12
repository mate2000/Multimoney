package com.multimoney.domain.model.credit

data class SaveCreditOffer(
    val idUserRequest: Int,
    val rejectedBlaze: Boolean,
    val products: List<Product?>?

)