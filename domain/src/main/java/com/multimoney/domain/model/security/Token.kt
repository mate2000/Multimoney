package com.multimoney.domain.model.security

import com.multimoney.domain.model.util.error.MessageError

data class Token(
    val accessToken: String,
    val expiresIn: Int,
    val tokenType: String,
    val messageError: MessageError
)
