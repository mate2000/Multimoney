package com.multimoney.multimoney.presentation.util

import com.multimoney.multimoney.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
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

fun getCurrentDateTimeString(dateTimeFormatter: DateTimeFormatter) =
    LocalDateTime.now().format(dateTimeFormatter).toString()

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

fun onBirthDateAgeValidation(pickedDate: LocalDate): Pair<Boolean, Int> {
    val actualDate = LocalDate.now()
    val periodBetweenDates = Period.between(pickedDate, actualDate).years
    return if (periodBetweenDates <= EIGHTEEN_YEARS_VALUE) {
        Pair(true, R.string.smart_account_document_birthdate_age_error)
    } else if (periodBetweenDates > EIGHTEEN_YEARS_VALUE && periodBetweenDates > ONE_HUNDRED_TWENTY_YEARS_VALUE
    ) {
        Pair(true, R.string.smart_account_document_birthdate_age_limit_error)
    } else {
        Pair(false, R.string.smart_account_document_birthdate_age_limit_error)
    }
}

fun getCurrentDateYMDPattern(): String {
    val date = LocalDate.now()
    return date.toString()
}

fun getPreviousDate(daysToSubtract: Long): String {
    val date = LocalDate.now().minusDays(daysToSubtract)
    return date.toString()
}

fun getCurrentDate(time: Date): String {
    return BAR_DIVIDER_FORMAT.format(time)
}

fun getCurrentTime(time: Date): String {
    return SHORT_TIME_FORMAT.format(time)
}

fun getCurrentDateMinusYears(years: Long): LocalDate {
    val today = LocalDate.now()
    return today.minusYears(years)
}

fun parseApiDateToCardDate(date: String?): String {
    return if (date.isNullOrEmpty().not()) {
        val dateFormatted = date?.let { API_DATE_FORMAT.parse(it) }
        dateFormatted?.let {
            BAR_DIVIDER_FORMAT.format(dateFormatted)
        } ?: run {
            ""
        }
    } else {
        ""
    }
}

fun parseApiDateToTermsAndConditionsDateTime(date: String?): String {
    return if (date.isNullOrEmpty().not()) {
        val dateFormatted = date?.let { API_DATE_FORMAT.parse(it) }
        dateFormatted?.let {
            API_DATE_AND_TIME_FORMAT.format(dateFormatted)
        } ?: run {
            ""
        }
    } else {
        ""
    }
}

const val YEAR_MONTH_DAY_PATTERN = "yyyy-mm-dd"
const val DAY_PATTERN = "dd"
const val ISO_8601_API_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
const val YEAR_MONTH_DAY_AND_TIME_BAR_FORMAT = "dd | MM | yyyy hh:mm a"
const val BIRTH_DATE_MIN_YEAR = 1902
const val BIRTH_DATE_MIN_MONTH = 0
const val BIRTH_DATE_MIN_DAY = 1
const val EIGHTEEN_YEARS_VALUE = 18
const val ONE_HUNDRED_TWENTY_YEARS_VALUE = 120

val DAY_FORMAT = SimpleDateFormat(DAY_PATTERN, Locale.getDefault())
val API_DATE_FORMAT = SimpleDateFormat(ISO_8601_API_FORMAT_PATTERN, Locale.getDefault())
val SHORT_DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
val BAR_DIVIDER_FORMAT = SimpleDateFormat("dd | MM | yyyy", Locale.getDefault())
val SHORT_TIME_FORMAT = SimpleDateFormat("hh:mm a", Locale.getDefault())
val BAR_DIVIDER_FORMAT_YEAR_TWO_DIGITS = SimpleDateFormat("dd | MM | yy", Locale.getDefault())
val DATE_TIME_DOCUMENTS_FORMAT = DateTimeFormatter.ofPattern("ddMMyyHHmmss")
val API_DATE_AND_TIME_FORMAT = SimpleDateFormat(YEAR_MONTH_DAY_AND_TIME_BAR_FORMAT, Locale.getDefault())
