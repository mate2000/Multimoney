package com.multimoney.domain.model.security

data class ClientInfoCr(
    val nombre: String,
    val tipoIdentificacion: String,
    val status: Boolean,
    val message: String,
)