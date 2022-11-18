package com.multimoney.multimoney.presentation.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Date

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

fun getCurrentDate(time : Date) : String{
    return BAR_DIVIDER_FORMAT.format(time)
}

fun getCurrentTime(time : Date) : String{
    return SHORT_TIME_FORMAT.format(time)
}

val DAY_FORMAT = SimpleDateFormat("dd", Locale.getDefault())
val API_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
val SHORT_DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
val BAR_DIVIDER_FORMAT = SimpleDateFormat("dd | MM | yyyy", Locale.getDefault())
val SHORT_TIME_FORMAT = SimpleDateFormat("hh:mm a", Locale.getDefault())

