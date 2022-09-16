package com.multimoney.multimoney.presentation.util

sealed class ResendOtp(val option: String) {
    object Voice : ResendOtp("Voice")
    object SMS : ResendOtp("SMS")
}
