package com.multimoney.multimoney.presentation.util

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber

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