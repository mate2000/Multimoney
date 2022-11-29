package com.multimoney.multimoney.presentation.util

import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.Companion
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
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

fun onBirthDateAgeValidation(pickedDate: LocalDate): Pair<Boolean, Int> {
    val actualDate = LocalDate.now()
    val periodBetweenDates = Period.between(pickedDate, actualDate).years
    return if (periodBetweenDates <= SmartDocumentViewModel.EIGHTEEN_YEARS_VALUE) {
        Pair(true, R.string.smart_account_document_birthdate_age_error)
    } else if (periodBetweenDates > SmartDocumentViewModel.EIGHTEEN_YEARS_VALUE && periodBetweenDates > SmartDocumentViewModel.ONE_HUNDRED_TWENTY_YEARS_VALUE
    ) {
        Pair(true, R.string.smart_account_document_birthdate_age_limit_error)
    } else {
        Pair(false, R.string.smart_account_document_birthdate_age_limit_error)
    }
}

fun getCurrentDate(time : Date) : String{
    return BAR_DIVIDER_FORMAT.format(time)
}

fun getCurrentTime(time : Date) : String{
    return SHORT_TIME_FORMAT.format(time)
}

const val YEAR_MONTH_DAY_PATTERN = "yyyy-mm-dd"
const val ISO_8601_API_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

val DAY_FORMAT = SimpleDateFormat(YEAR_MONTH_DAY_PATTERN, Locale.getDefault())
val API_DATE_FORMAT = SimpleDateFormat(ISO_8601_API_FORMAT_PATTERN, Locale.getDefault())
val SHORT_DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
val BAR_DIVIDER_FORMAT = SimpleDateFormat("dd | MM | yyyy", Locale.getDefault())
val SHORT_TIME_FORMAT = SimpleDateFormat("hh:mm a", Locale.getDefault())

