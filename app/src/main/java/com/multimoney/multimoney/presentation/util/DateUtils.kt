package com.multimoney.multimoney.presentation.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

fun getCurrentDateString() = getPickedDateAsString(
    Calendar.getInstance().get(Calendar.YEAR),
    Calendar.getInstance().get(Calendar.MONTH),
    Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
    YEAR_MONTH_DAY_PATTERN
)

fun getCardDateFormat(date: String?, newFormat: SimpleDateFormat = BAR_DIVIDER_FORMAT, oldFormat: SimpleDateFormat = SHORT_DATE_FORMAT): String {
    return if (date.isNullOrEmpty().not()) {
        try {
            val dateFormatted = oldFormat.parse(date)
            dateFormatted?.let {
                newFormat.format(dateFormatted)
            } ?: run {
                ""
            }
        } catch (e: ParseException) {
            ""
        }
    } else {
        ""
    }
}

fun getDayFromString(date: String?, format: SimpleDateFormat): String {
    return if (date.isNullOrEmpty().not()) {
        val dateFormatted = format.parse(date)
        dateFormatted?.let {
            DAY_FORMAT.format(dateFormatted)
        } ?: run {
            ""
        }
    } else {
        ""
    }
}

fun getCurrentDate(time: Date): String {
    return BAR_DIVIDER_FORMAT.format(time)
}

fun getCurrentTime(time: Date): String {
    return SHORT_TIME_FORMAT.format(time)
}

const val YEAR_MONTH_DAY_PATTERN = "yyyy-mm-dd"
const val ISO_8601_API_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

val DAY_FORMAT = SimpleDateFormat(YEAR_MONTH_DAY_PATTERN, Locale.getDefault())
val API_DATE_FORMAT = SimpleDateFormat(ISO_8601_API_FORMAT_PATTERN, Locale.getDefault())
val SHORT_DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
val BAR_DIVIDER_FORMAT = SimpleDateFormat("dd | MM | yyyy", Locale.getDefault())
val SHORT_TIME_FORMAT = SimpleDateFormat("hh:mm a", Locale.getDefault())
val BAR_DIVIDER_FORMAT_YEAR_TWO_DIGITS = SimpleDateFormat("dd | MM | yy", Locale.getDefault())
