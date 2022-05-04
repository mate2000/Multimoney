package com.multimoney.multimoney.presentation.util

import android.util.Patterns
import com.github.mikephil.charting.formatter.IFillFormatter
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel

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
    if (personalDocumentLength >= sizeRequired)
        Pair(false, R.string.error_empty) else
        Pair(true, errorMessage)

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
            R.string.sign_up_personal_data_dui_sv_not_valid
        )
    } else {
        Pair(false, R.string.error_empty)
    }

fun passwordHasMinimumCharacters(password: String): Boolean {
    return password.length >= EIGHT_MINIMUM_CHARACTERS
}

fun passwordHasAUppercaseLetterValidation(password: String): Boolean {
    return matchRegex(password, getRegex(ONE_UPPERCASE_LETTER_REGEX))
}

fun passwordHasALowercaseLetterValidation(password: String): Boolean {
    return matchRegex(password, getRegex(ONE_LOWERCASE_LETTER_REGEX))
}

fun passwordHasANumberValidation(password: String): Boolean {
    return matchRegex(password, getRegex(ONE_NUMBER_REGEX))
}

fun sameConsecutiveCharacterValidationValidation(password: String): Boolean {
    var hasTheSameConsecutiveCharacter = false
    if (password.isNotEmpty() && password.length >= CHARACTER_NEED_TO_VALIDATE) {
        val passwordSplit = password.toCharArray()
        var characterCounter = 1
        for (index in 0..passwordSplit.lastIndex) {
            if (index + 1 < passwordSplit.lastIndex) {
                for (i in (index + 1)..passwordSplit.lastIndex) {
                    if (passwordSplit[index] == passwordSplit[i]) {
                        characterCounter++
                    } else {
                        characterCounter = 1
                    }
                    if (characterCounter > MIN_CHARACTER_ALLOW) {
                        hasTheSameConsecutiveCharacter = true
                        break
                    }
                }
            } else {
                break
            }
        }
    }
    return hasTheSameConsecutiveCharacter
}

const val CHARACTER_NEED_TO_VALIDATE = 3
const val MIN_CHARACTER_ALLOW = 2
const val EIGHT_MINIMUM_CHARACTERS = 8