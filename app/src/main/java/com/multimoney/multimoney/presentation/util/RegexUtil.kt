package com.multimoney.multimoney.presentation.util

import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel

fun matchRegex(value: String, regex: Regex) = value.matches(regex)

fun getRegex(regex: String) = regex.toRegex()

const val OTP_MESSAGE_REGEX = "(|^)\\d{${SignUpOtpViewModel.TOTAL_DIGITS}}"
const val ONE_UPPERCASE_LETTER_REGEX = "(.*[A-Z].*)"
const val ONE_LOWERCASE_LETTER_REGEX = "(.*[a-z].*)"
const val ONE_NUMBER_REGEX = "(.*\\d.*)"
const val ONE_CHARACTER_REGEX = "(.*[!@#\$%&*()_+=|<.>?{}\\\\[\\\\]~-].*)"
const val DECIMAL_REGEX = "^[1-9][0-9]*(\\.\\d{0,2})?$"
