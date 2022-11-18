package com.multimoney.domain.model.security

data class InfoUser(
    val idBrand: Int,
    val userName: String,
    val idClient: Int,
    val phone: String,
    val statusOnfido: String?
)