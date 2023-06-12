package com.multimoney.multimoney.presentation.util.catalog

sealed class SendOtpMethod(val value: String, val apiValue: String) {
    object Email : SendOtpMethod(
        value = "EMAIL",
        apiValue = "EMAIL"
    )

    object Sms : SendOtpMethod(
        value = "SMS",
        apiValue = "PHONE"
    )
}
