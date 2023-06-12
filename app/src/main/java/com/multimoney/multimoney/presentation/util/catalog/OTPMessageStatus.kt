package com.multimoney.multimoney.presentation.util.catalog

enum class OTPMessageStatus(value : Int) {
    RESEND_OTP (1),
    RESEND_OTP_AGAIN (2),
    COULD_NOT_VERIFY_ID (3)
}