package com.multimoney.multimoney.presentation.util

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar

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

val SHORT_DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
val BAR_DIVIDER_FORMAT = SimpleDateFormat("dd | MM | yyyy", Locale.getDefault())
