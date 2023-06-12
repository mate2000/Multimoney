package com.multimoney.domain.model.credit

data class CreditExtensionMessage(
    val pkPromotionMonth: Int?,
    val product: List<CreditExtensionProduct?>?
)
