package com.multimoney.domain.model.profile

data class TermsAndConditionsSignedItem(
    val type: String,
    val dateSigned: String,
    val version: String,
    val html: String
)

data class TermsAndConditionsSigned(
    val items: List<TermsAndConditionsSignedItem>
)
