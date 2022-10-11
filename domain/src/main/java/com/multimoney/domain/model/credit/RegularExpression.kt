package com.multimoney.domain.model.credit

data class RegularExpression(
    val pkRegularExpression: Int,
    val value: String,
    val description: String,
    val regularExpression: String,
    val fkRegularExpression: Int,
    val idTypeAccount: Int
)