package com.multimoney.domain.model.security

data class InfoUser(
    val idBrand: Int,
    val userName: String,
    val idClient: Int,
    val firstName: String,
    val secondName: String,
    val lastName: String,
    val secondLastName: String,
    val phone: String,
    val visaDirectUser: String?,
    val statusOnfido: String?
)
