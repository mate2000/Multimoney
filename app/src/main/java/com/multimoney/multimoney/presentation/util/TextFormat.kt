package com.multimoney.multimoney.presentation.util

import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.time.Duration

private const val TIME_FORMAT = "%02d:%02d"
private const val INTEGER_FORMAT_SEPARATOR = ","
private const val INTEGER_FORMAT =
    "###$INTEGER_FORMAT_SEPARATOR###$INTEGER_FORMAT_SEPARATOR###$INTEGER_FORMAT_SEPARATOR###"

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
    if (isValidAmount() && separator == null) {
        DecimalFormat(INTEGER_FORMAT).format(toDouble())
    } else if (isValidAmount() && separator != null) {
        DecimalFormat(INTEGER_FORMAT.replace(INTEGER_FORMAT_SEPARATOR, separator)).format(toDouble())
    } else {
        this
    }

fun String.isValidAmount() = isNotBlank() && isValidAmountLength()

fun String.isValidAmountLength() = length <= 12
