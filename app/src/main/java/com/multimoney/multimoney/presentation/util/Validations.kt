package com.multimoney.multimoney.presentation.util

import android.util.Patterns
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
