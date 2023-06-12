package com.multimoney.domain.model.crypto

data class SendCryptoAddressRequest(
    val pkUser: Int,
    val identification: String,
    val destinationAddress: String,
    val feeId: String,
    val asset: String,
    val market: String,
    val cryptoNetwork: String,
    val amount: Double,
    val fee: Double,
    val internalFee: Double,
    val taxAmount: Double,
    val idBrand: Int,
    val user: String
)
