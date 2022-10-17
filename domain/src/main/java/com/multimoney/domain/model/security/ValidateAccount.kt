package com.multimoney.domain.model.security

import com.multimoney.domain.model.util.error.MessageError

data class ValidateAccount(
    val responseCode: Int,
    val responseMessage: String,
    val name: String,
    val currency: String,
    val sellPriceDollar: String,
    val buyPriceDollar: String,
    val bankId: Any?,
    val bankName: String?,
)
