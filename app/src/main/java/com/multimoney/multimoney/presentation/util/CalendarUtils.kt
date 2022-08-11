package com.multimoney.multimoney.presentation.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun getPickedDateAsString(year: Int, month: Int, day: Int, dateFormat: String): String {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, day)
    return SimpleDateFormat(dateFormat, Locale.getDefault()).format(calendar.time)
}