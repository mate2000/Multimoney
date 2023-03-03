package com.multimoney.domain.model.crypto

data class ReleaseTransactionResponse(
    val withHeld: Boolean,
    val userResponse: Boolean,
    val hasError: Boolean?
)
