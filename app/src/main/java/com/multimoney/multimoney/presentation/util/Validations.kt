package com.multimoney.multimoney.presentation.util

import android.util.Patterns
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE
import com.google.i18n.phonenumbers.Phonenumber
import com.multimoney.data.util.catalog.Brand
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
    phoneNumberType: PhoneNumberUtil.PhoneNumberType,
): Boolean {
    val number: Phonenumber.PhoneNumber?
    if (phone.length > 6) {
        return try {
            number = PhoneNumberUtil.getInstance().parse(
                fullPhoneNumber,
                Phonenumber.PhoneNumber.CountryCodeSource.UNSPECIFIED.name,
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

fun isMobileOrFixedLinePhoneNumberValid(
    phone: String,
    fullPhoneNumber: String,
    countryCode: String,
): Boolean {
    val number: Phonenumber.PhoneNumber?
    if (phone.length > 6) {
        return try {
            number = PhoneNumberUtil.getInstance().parse(
                fullPhoneNumber,
                Phonenumber.PhoneNumber.CountryCodeSource.UNSPECIFIED.name,
            )
            if (PhoneNumberUtil.getInstance().getNumberType(number) == PhoneNumberUtil.PhoneNumberType.MOBILE ||
                PhoneNumberUtil.getInstance().getNumberType(number) == PhoneNumberUtil.PhoneNumberType.FIXED_LINE
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
    } else {
        Pair(true, errorMessage)
    }

fun validDui(personalDocumentValue: String) =
    if (personalDocumentValue.length == Nationalities.ElSalvadorDui.documentSize) {
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
            R.string.sign_up_personal_data_id_not_valid,
        )
    } else {
        Pair(true, R.string.sign_up_personal_data_id_not_valid)
    }

fun validCarne(sizeRequired: Int, errorMessage: Int, personalDocumentLength: Int) =
    if (personalDocumentLength == sizeRequired) {
        Pair(false, R.string.error_empty)
    } else {
        Pair(true, errorMessage)
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

fun haveMoreThanThreeConsecutiveLetterOrNumber(value: String): Boolean {
    val regEx = Regex(FOUR_REPEATED_CHARS_REGEX)
    return regEx.containsMatchIn(value)
}

fun haveMoreThanThreeSequentialLetterOrNumber(value: String): Boolean {
    val regExAsc = Regex(FOUR_SEQUENTIAL_ASC_CHARS_REGEX)
    val regExDsc = Regex(FOUR_SEQUENTIAL_DESC_CHARS_REGEX)
    return regExAsc.containsMatchIn(value) || regExDsc.containsMatchIn(value)
}

fun containForbiddenWords(value: String, forbiddenWords: List<String>): Boolean {
    if (forbiddenWords.isEmpty() || value.isEmpty()) {
        return false
    }

    return forbiddenWords.any { value.contains(it, ignoreCase = true) }
}

fun validateDecimalIncome(value: String): Boolean {
    return ((Pattern.matches(DECIMAL_REGEX, value) || value.isEmpty()) && value != "00")
}

fun validateEightDecimalIncome(value: String): Boolean {
    return ((Pattern.matches(EIGHT_DECIMAL_REGEX, value) || value.isEmpty()))
}

fun isPhoneNumberValid(phone: String, idBrand: Int): Boolean {
    return when (idBrand) {
        Brand.ElSalvador.id -> {
            isMobileOrFixedLinePhoneNumberValid(
                phone = phone,
                fullPhoneNumber = "${Brand.ElSalvador.phoneCode}$phone",
                countryCode = Brand.ElSalvador.countryCode,
            )
        }
        Brand.Guatemala.id -> {
            isMobileOrFixedLinePhoneNumberValid(
                phone = phone,
                fullPhoneNumber = "${Brand.Guatemala.phoneCode}$phone",
                countryCode = Brand.Guatemala.countryCode
            )
        }
        Brand.CostaRica.id -> {
            isMobileOrFixedLinePhoneNumberValid(
                phone = phone,
                fullPhoneNumber = "${Brand.CostaRica.phoneCode}$phone",
                countryCode = Brand.CostaRica.countryCode
            )
        }
        else -> false
    }
}

const val INVALID_CHARACTERS_CHUNKS = 4
const val CHARACTER_NEED_TO_VALIDATE = 3
const val EIGHT_MINIMUM_CHARACTERS = 8
const val DESCRIPTION_MAX_LENGTH = 150
const val ADDRESS_MAX_LENGTH = 150
const val MIN_INCOME = 0
const val MAX_CRYPTO_ITEMS = 3
const val MIN_SMART_ACCOUNT_DIGITS = 9
const val MAX_SMART_ACCOUNT_DIGITS = 16
