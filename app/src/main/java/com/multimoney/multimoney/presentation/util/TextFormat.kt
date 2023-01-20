package com.multimoney.multimoney.presentation.util

import com.multimoney.data.util.catalog.Brand
import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.abs
import kotlin.time.Duration

private const val TIME_FORMAT = "%02d:%02d"
private const val INTEGER_FORMAT_SEPARATOR = ","
private const val INTEGER_FORMAT =
    "###$INTEGER_FORMAT_SEPARATOR###$INTEGER_FORMAT_SEPARATOR###$INTEGER_FORMAT_SEPARATOR###"
private const val DOUBLE_FORMAT_SEPARATOR = ","
private const val DOUBLE_FORMAT =
    "###$DOUBLE_FORMAT_SEPARATOR###$DOUBLE_FORMAT_SEPARATOR###$DOUBLE_FORMAT_SEPARATOR###.##"
private val DECIMAL_FORMAT_REGEX = "[0-9]{0,20}[.]*[0-9]{0,2}".toRegex()

// Convert time to milli seconds
fun Duration.format(): String {
    val seconds = abs(inWholeSeconds)
    val value = String.format(
        TIME_FORMAT,
        seconds % 3600 / 60,
        seconds % 60
    )
    return value
}

// Format String to Integer decimal format
fun String.stringToIntegerFormat(separator: String? = null): String =
    if (isNotEmpty() && isValidAmount() && separator == null) {
        DecimalFormat(INTEGER_FORMAT).format(toDouble())
    } else if (isNotEmpty() && isValidAmount() && separator != null) {
        DecimalFormat(
            INTEGER_FORMAT.replace(
                INTEGER_FORMAT_SEPARATOR,
                separator
            )
        ).format(toDouble())
    } else {
        this
    }

// Format String to Integer decimal format
fun String.stringToDoubleFormat(separator: String? = null): String =
    if (isNotEmpty() && isValidAmount() && separator == null) {
        DecimalFormat(DOUBLE_FORMAT).format(toDouble())
    } else if (isNotEmpty() && isValidAmount() && separator != null) {
        DecimalFormat(DOUBLE_FORMAT.replace(DOUBLE_FORMAT_SEPARATOR, separator)).format(toDouble())
    } else {
        this
    }

fun Double.formattedTwoDecimalsNumber(): Double =
    String.format(TWO_DECIMALS_FORMAT, this).toDouble()

fun String.isValidAmount() = DECIMAL_FORMAT_REGEX.matches(this)

fun String.capitalized(): String {
    return this.lowercase().replaceFirstChar {
        if (it.isLowerCase()) {
            it.titlecase(Locale.getDefault())
        } else it.toString()
    }
}

fun getMaskedAccount(accountNumber: String, maskedText: String = ACCOUNT_MASK) =
    accountNumber.take(ACCOUNT_FIRST_DIGITS).plus(maskedText)
        .plus(accountNumber.takeLast(ACCOUNT_LAST_DIGITS))

fun getMaskedVisaAccount(accountNumber: String, maskedText: String = ACCOUNT_MASK) =
    VISA_MASK.plus(maskedText)
        .plus(accountNumber.takeLast(ACCOUNT_LAST_DIGITS))

fun getMaskedAccountIban(accountNumber: String, maskedText: String = ACCOUNT_MASK) =
    Brand.CostaRica.iban.plus(
        accountNumber.take(ACCOUNT_IBAN_FIRST_DIGITS).plus(maskedText)
            .plus(accountNumber.takeLast(ACCOUNT_LAST_DIGITS))
    )

fun getFullMaskedAccountIban(accountBank: String, accountNumber: String, maskedText: String = ACCOUNT_MASK) =
    accountBank.plus(" | " + getMaskedAccountIban(accountNumber, maskedText))

const val ACCOUNT_IBAN_FIRST_DIGITS = 0
const val ACCOUNT_FIRST_DIGITS = 2
const val ACCOUNT_LAST_DIGITS = 4
const val CARD_NUMBER_LAST_DIGITS = 4
const val TWO_DECIMALS_FORMAT = "%.2f"
const val ACCOUNT_MASK = "••••"
const val VISA_MASK = "Visa"
