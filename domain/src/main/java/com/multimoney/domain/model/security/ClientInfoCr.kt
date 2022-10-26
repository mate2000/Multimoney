package com.multimoney.domain.model.security

data class ClientInfoCr(
    var name: String,
    var firstName: String,
    var secondName: String,
    var firstLastName: String,
    var secondLastName: String,
    var status: String,
    var message: String
)
