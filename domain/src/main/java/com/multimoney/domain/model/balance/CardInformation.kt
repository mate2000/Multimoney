package com.multimoney.domain.model.balance

data class CardInformation(
    val cardToken: String?,
    val cardNumber: String?,
    val expDate: String?,
    val holderName: String?,
    val status: String?,
    val blockType: String?,
    val cValidation: String?,
    val type: String?
)
