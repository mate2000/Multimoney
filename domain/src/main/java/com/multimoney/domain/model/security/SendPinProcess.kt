package com.multimoney.domain.model.security

import com.multimoney.domain.model.util.error.MessageError

data class SendPinProcess(
    val pkUser: Int?,
    val email: String?,
    val userName: String?,
    val phone: String?,
    val identification: String?,
    val numberOfPinForwards: String?,
    val numberOfOtpForwards: String?,
    val pinExpirationTime: String?,
    val messageError: MessageError
)
