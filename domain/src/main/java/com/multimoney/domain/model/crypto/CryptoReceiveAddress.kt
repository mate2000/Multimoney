package com.multimoney.domain.model.crypto

data class CryptoReceiveAddress(
    val id: String,
    val address: String,
    val crypto_network: String
)
