package com.multimoney.domain.model.security

import com.multimoney.domain.model.util.error.MessageError

data class ValidatePin(
    val pkUser: String?,
    val blockedIndicator: String?,
    val messageError: MessageError
)
