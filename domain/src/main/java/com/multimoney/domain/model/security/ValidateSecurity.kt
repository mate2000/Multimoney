package com.multimoney.domain.model.security

import com.multimoney.domain.model.util.error.MessageError

data class ValidateSecurity(
    val messageError: MessageError
)