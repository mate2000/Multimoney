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
const val EIGHT_DECIMAL_REGEX = "^[0-9]*(\\.\\d{0,8})?$"
const val FOUR_REPEATED_CHARS_REGEX = "(?=(\\w)\\1{3})\\w{4}"
const val FOUR_SEQUENTIAL_ASC_CHARS_REGEX = "(abcd|bcde|cdef|defg|efgh|fghi|ghij|hijk|ijkl|jklm|klmn|lmno|mnop|nopq|opqr|pqrs|qrst|rstu|stuv|tuvw|uvwx|vwxy|wxyz|0123|1234|2345|3456|4567|5678|6789)"
const val FOUR_SEQUENTIAL_DESC_CHARS_REGEX = "(dcba|edcb|fedc|gfed|hgfe|ihgf|jihg|kjih|lkji|mlkj|nmlk|onml|ponm|qpon|rqpo|srqp|tsrq|utsr|vuts|wvut|xwvu|yxwv|zyxw|3210|4321|5432|6543|7654|8765|9876)"