package com.multimoney.multimoney.presentation.util.catalog

sealed class SendOtpMethod(val value: String) {
    object Email : SendOtpMethod(
        value = "EMAIL"
    )

    object Sms : SendOtpMethod(
        value = "SMS"
    )
}
