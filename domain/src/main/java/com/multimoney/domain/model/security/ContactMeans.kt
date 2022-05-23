package com.multimoney.domain.model.security

data class ContactMeans(
    var whatsapp: Boolean? = true,
    var call: Boolean? = true,
    var email: Boolean? = true
)