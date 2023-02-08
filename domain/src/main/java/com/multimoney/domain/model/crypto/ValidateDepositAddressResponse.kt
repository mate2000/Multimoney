package com.multimoney.domain.model.crypto

data class ValidateDepositAddressResponse(
    val status: Int?,
    val message: String?,
    val result: ValidateDepositAddressResult?
)
