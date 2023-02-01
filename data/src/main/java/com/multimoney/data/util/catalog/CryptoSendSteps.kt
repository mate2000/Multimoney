package com.multimoney.data.util.catalog

sealed class CryptoSendSteps(
    val id: Int,
    val name: String
) {
    object One : CryptoSendSteps(1, "List_Of_Currencies")
    object Two : CryptoSendSteps(2, "Crypto_Address")
    object Three : CryptoSendSteps(3, "Send_Amount")
    object Four : CryptoSendSteps(4, "Voucher")
}
