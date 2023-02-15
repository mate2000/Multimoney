package com.multimoney.domain.model.crypto

data class SendCryptoToAddressOrder(
    val result: SendCryptoToAddressResult?,
    val status: Int?,
    val message: String?,
    val detail: String?
)
