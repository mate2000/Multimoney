package com.multimoney.multimoney.presentation.util

import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.Companion.DATE_FORMAT
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.ZoneOffset.UTC
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun getPickedDateAsString(year: Int, month: Int, day: Int, dateFormat: String): String {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, day)
    return SimpleDateFormat(dateFormat, Locale.getDefault()).format(calendar.time)
}

fun getFormatDateByString(date: String, formatOne: String, formatTwo: String): String {
    val simpleDateFormat = SimpleDateFormat(formatOne, Locale.getDefault())
    val formattedDate = simpleDateFormat.parse(date)
    val simpleDateFormatTow = SimpleDateFormat(formatTwo, Locale.getDefault())
    return formattedDate?.let {
        simpleDateFormatTow.format(formattedDate)
    } ?: run {
        ""
    }
}

fun getCardDateFormat(date: String?): String {
    return if (date.isNullOrEmpty().not()) {
        val dateFormatted = SHORT_DATE_FORMAT.parse(date)
        dateFormatted?.let {
            BAR_DIVIDER_FORMAT.format(dateFormatted)
        } ?: run {
            ""
        }
    } else {
        ""
    }
}

fun parseStringToLocalDate(date: String): Date {
    val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)
    simpleDateFormat.timeZone = TimeZone.getTimeZone(UTC)
    return try {
        simpleDateFormat.parse(date) ?: Date()
    } catch (e: Exception) {
        Date()
    }
}

fun getISO8601DateFormat(
    pattern: String? = DATE_FORMAT,
    local: Locale? = Locale.ENGLISH,
    formatToUTC: Boolean = true
): DateFormat {
    val dateFormat = SimpleDateFormat(pattern, local)
    if (formatToUTC) dateFormat.timeZone = TimeZone.getTimeZone(UTC)
    return dateFormat
}

val SHORT_DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
val BAR_DIVIDER_FORMAT = SimpleDateFormat("dd | MM | yyyy", Locale.getDefault())
