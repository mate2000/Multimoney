package com.multimoney.multimoney.presentation.util

import android.util.Patterns
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.multimoney.data.util.catalog.Nationalities
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel
import java.util.regex.Pattern

fun isEmailValid(email: String?): Boolean {
    return email?.let {
        it.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(it).matches()
    } ?: false
}

fun isPhoneNumberValid(
    phone: String,
    fullPhoneNumber: String,
    countryCode: String,
    phoneNumberType: PhoneNumberUtil.PhoneNumberType
): Boolean {
    val number: Phonenumber.PhoneNumber?
    if (phone.length > 6) {
        return try {
            number = PhoneNumberUtil.getInstance().parse(
                fullPhoneNumber,
                Phonenumber.PhoneNumber.CountryCodeSource.UNSPECIFIED.name
            )
            if (phoneNumberType == PhoneNumberUtil.PhoneNumberType.MOBILE && PhoneNumberUtil.getInstance()
                .getNumberType(number) == PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE || PhoneNumberUtil.getInstance()
                    .getNumberType(number) == phoneNumberType
            ) {
                PhoneNumberUtil.getInstance()
                    .isValidNumberForRegion(number, countryCode.uppercase())
            } else {
                false
            }
        } catch (ex: Exception) {
            false
        }
    }
    return false
}

fun validId(sizeRequired: Int, errorMessage: Int, personalDocumentLength: Int) =
    if (personalDocumentLength >= sizeRequired) {
        Pair(false, R.string.error_empty)
    }else {
        Pair(true, errorMessage)
    }

fun validDui(personalDocumentValue: String) =
    if (personalDocumentValue.length == Nationalities.ElSalvador.documentSize) {
        val duiSplit = personalDocumentValue.split("").filter { it != "" }
        var verificationNumber = 0
        for (i in duiSplit.indices) {
            if (i != duiSplit.size - 1) {
                verificationNumber += duiSplit[i].toInt() * (duiSplit.size - i)
            }
        }
        val verificationValue =
            10 - verificationNumber.mod(SignUpPersonalDataViewModel.DUI_VERIFICATION_MODULE)
        Pair(
            verificationValue != 10 && verificationValue != duiSplit[duiSplit.lastIndex].toInt(),
            R.string.sign_up_personal_data_id_not_valid
        )
    } else {
        Pair(true, R.string.sign_up_personal_data_id_not_valid)
    }

fun passwordHasMinimumCharacters(value: String): Boolean {
    return value.length >= EIGHT_MINIMUM_CHARACTERS
}

fun passwordHasAUppercaseLetterValidation(value: String): Boolean {
    return matchRegex(value, getRegex(ONE_UPPERCASE_LETTER_REGEX))
}

fun passwordHasALowercaseLetterValidation(value: String): Boolean {
    return matchRegex(value, getRegex(ONE_LOWERCASE_LETTER_REGEX))
}

fun passwordHasANumberValidation(value: String): Boolean {
    return matchRegex(value, getRegex(ONE_NUMBER_REGEX))
}

fun passwordHasSpecialCharacterValidation(value: String): Boolean {
    return matchRegex(value, getRegex(ONE_CHARACTER_REGEX))
}

fun stringHasOnlyDigitOrLetter(value: String) =
    value.all { it.isDigit() } || value.all { it.isLetter() }

fun noMoreThanThreeLettersOrNumbers(value: String): Boolean {
    var error = false
    if (value.isNotEmpty() && value.length > CHARACTER_NEED_TO_VALIDATE) {
        val valueChunked = value.chunked(INVALID_CHARACTERS_CHUNKS)
        if (stringHasOnlyDigitOrLetter(valueChunked.first())) {
            error = true
        }
        if (error.not()) {
            val newValue = value.drop(1)
            return noMoreThanThreeLettersOrNumbers(newValue)
        }
    }
    return error
}

fun noMoreThanThreeConsecutiveLetterOrNumber(value: String): Boolean {
    var error = false
    if (value.isNotEmpty() && value.length > CHARACTER_NEED_TO_VALIDATE) {
        val valueChunked = value.chunkedSequence(INVALID_CHARACTERS_CHUNKS)
        if (stringHasOnlyDigitOrLetter(valueChunked.first())) {
            val valueSplit = valueChunked.first().lowercase().toCharArray()
            error =
                valueSplit.first().code.plus(1) == valueSplit[1].code &&
                valueSplit[1].code.plus(1) == valueSplit[2].code &&
                valueSplit[2].code.plus(1) == valueSplit.last().code
        }
        if (error.not()) {
            val newValue = value.drop(1)
            return noMoreThanThreeConsecutiveLetterOrNumber(newValue)
        }
    }
    return error
}

fun noMoreThanThreeEqualConsecutiveLetterOrNumber(value: String): Boolean {
    var error = false
    if (value.isNotEmpty() && value.length > CHARACTER_NEED_TO_VALIDATE) {
        val valueChunked = value.chunkedSequence(INVALID_CHARACTERS_CHUNKS)
        if (stringHasOnlyDigitOrLetter(valueChunked.first())) {
            val valueSplit = valueChunked.first().lowercase().toCharArray()
            error =
                valueSplit.first().code == valueSplit[1].code &&
                valueSplit[1].code == valueSplit[2].code &&
                valueSplit[2].code == valueSplit.last().code
        }
        if (error.not()) {
            val newValue = value.drop(1)
            return noMoreThanThreeEqualConsecutiveLetterOrNumber(newValue)
        }
    }
    return error
}

fun validateDecimalIncome(value: String): Boolean {
    return ((Pattern.matches(DECIMAL_REGEX, value) || value.isEmpty()) && value != "00")
}

const val INVALID_CHARACTERS_CHUNKS = 4
const val CHARACTER_NEED_TO_VALIDATE = 3
const val EIGHT_MINIMUM_CHARACTERS = 8
const val DESCRIPTION_MAX_LENGTH = 150
const val MIN_INCOME = 0
