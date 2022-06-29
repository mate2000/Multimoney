package com.multimoney.multimoney.presentation.util


fun matchRegex(value: String, regex: Regex) = value.matches(regex)

fun getRegex(regex: String) = regex.toRegex()

const val ONE_UPPERCASE_LETTER_REGEX = "(.*[A-Z].*)"
const val ONE_LOWERCASE_LETTER_REGEX = "(.*[a-z].*)"
const val ONE_NUMBER_REGEX = "(.*\\d.*)"
const val ONE_CHARACTER_REGEX = "[!@#\$%&*()_+=|<.>?{}\\\\[\\\\]~-]"