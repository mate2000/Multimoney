package com.multimoney.domain.model.security

data class InfoUser(
val pkUser: Int,

val idBrand: Int,

val fullName: String,

val firstName: String,

val secondName: String,

val lastName: String,

val secondLastName: String,

val userName: String,

val email: String,

val phone: String,

val birthDate: String,

val idClient: Int,

val vISADirect_ID: String,

val vISADirect_Usuario: String,

val fecha_ultimo_acceso: String
)
