package com.multimoney.domain.model.virtualcard

class CreateCard(
    val idCard: Int,
    val creationDate: Any,
    val verified: Boolean,
    val detail: String,
    val cardMasked: String,
    val verificationValue: String,
    val expirationMonth: String,
    val expirationYear: String,
    val cardTokenId: String,
    val currencyDescription: String,
    val country: String,
    val default: Boolean
)