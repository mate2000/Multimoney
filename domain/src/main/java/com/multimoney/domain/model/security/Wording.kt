package com.multimoney.domain.model.security

data class Wording(
    val textOne: String,
    val textTwo: String,
    val cTA: String,
    val link: String = "",
    val display: Boolean = false,
    val workFlow: String? = ""
)
