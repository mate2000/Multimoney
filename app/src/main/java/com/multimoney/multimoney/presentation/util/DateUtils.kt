package com.multimoney.multimoney.presentation.util

import android.os.Build
import com.multimoney.multimoney.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
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
    if (date.isBlank()) return ""

    val simpleDateFormat = SimpleDateFormat(formatOne, Locale.getDefault())
    val formattedDate = simpleDateFormat.parse(date)
    val simpleDateFormatTwo = SimpleDateFormat(formatTwo, Locale.getDefault())
    return formattedDate?.let {
        simpleDateFormatTwo.format(formattedDate)
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

fun getCardDateFormat(
    date: String?,
    newFormat: SimpleDateFormat = BAR_DIVIDER_FORMAT,
    oldFormat: SimpleDateFormat = SHORT_DATE_FORMAT
): String {
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
    return if (date.isNullOrBlank().not()) {
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

fun getDateFormat(date: Date?, format: SimpleDateFormat): String {
    return if (date != null) {
        return format.format(date)
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

fun onExpirationDateValidation(pickedDate: String): Boolean {
    val pickedAsDate = LocalDate.parse(pickedDate)
    val actualDate = LocalDate.now()
    val periodBetweenDates = Period.between(actualDate, pickedAsDate).days
    return periodBetweenDates >= 0
}

fun getCurrentDateYMDPattern(): String {
    val date = LocalDate.now()
    return date.toString()
}

fun getPreviousDate(daysToSubtract: Long): String {
    val date = LocalDate.now().minusDays(daysToSubtract)
    return date.toString()
}

fun getPreviousDate(dateFilter: FilterDate = FilterDate.YESTERDAY): String {
    val date = LocalDate.now()
    when (dateFilter) {
        FilterDate.YESTERDAY -> date.minusDays(1)
        FilterDate.LAST_7_DAYS -> date.minusDays(7)
        FilterDate.LAST_30_DAYS -> date.minusDays(30)
        FilterDate.LAST_90_DAYS -> date.minusDays(90)
        FilterDate.LAST_180_DAYS -> date.minusDays(180)
        FilterDate.LAST_365_DAYS -> date.minusDays(365)
    }

    val formatters: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return date.format(formatters)
}

fun getCurrentDate(time: Date): String {
    return BAR_DIVIDER_FORMAT.format(time)
}

fun getCurrentTime(time: Date): String {
    return SHORT_TIME_FORMAT.format(time)
}

fun getCurrentDate(): LocalDate = LocalDate.now()

fun getCurrentDateMinusYears(years: Long): LocalDate {
    val today = getCurrentDate()
    return today.minusYears(years)
}

fun getDateTimeFormatterPattern(pattern: String): DateTimeFormatter {
    return DateTimeFormatter.ofPattern(pattern)
}

fun getLocalDateFromParse(date: String, formatter: DateTimeFormatter): LocalDate {
    return LocalDate.parse(date, formatter)
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

fun Calendar.toLocalDate(): LocalDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    LocalDateTime.ofInstant(this.toInstant(), this.timeZone.toZoneId()).toLocalDate()
} else {
    LocalDateTime.ofInstant(this.toInstant(), ZoneId.systemDefault()).toLocalDate()
}

enum class FilterDateByDays(
    val time: Long,
    val timeDescription: String,
    val timeDescriptionExtended: String,
    val timeAbv: String,
    val timeAbvExtended: String,
    val dataPoints: Long
) {
    HOUR(24, "Hora", "1 Hora", "H", "1H", 60),
    YESTERDAY(1, "Dia", "1 Dia", "D", "1D", 24),
    LAST_7_DAYS(7, "Semana", "1 Semana", "S", "1S", 7),
    LAST_30_DAYS(30, "Mes", "1 Mes", "M", "1M", 30),
    LAST_90_DAYS(90, "Meses", "3 Meses", "3M", "3M", 90),
    LAST_180_DAYS(180, "Meses", "6 Meses", "6M", "6M", 180),
    LAST_365_DAYS(365, "Año", "1 Año", "A", "1A", 365),
}

enum class FilterDate {
    YESTERDAY,
    LAST_7_DAYS,
    LAST_30_DAYS,
    LAST_90_DAYS,
    LAST_180_DAYS,
    LAST_365_DAYS
}

const val YEAR_MONTH_DAY_PATTERN = "yyyy-MM-dd"
const val DAY_MONTH_YEAR_PATTERN = "dd-MM-yyyy"
const val YEAR_MONTH_DAY_PATTERN_BAR_FORMAT = "yyyy | MM | dd"
const val DAY_PATTERN = "dd"
const val ISO_8601_API_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
const val YEAR_MONTH_DAY_AND_TIME_BAR_FORMAT = "dd | MM | yyyy hh:mm a"
const val YEAR_PATTER = "yyyy"
const val BAR = " | "
const val HYPHEN = "-"
const val BIRTH_DATE_MIN_YEAR = 1902
const val BIRTH_DATE_MIN_MONTH = 0
const val BIRTH_DATE_MIN_DAY = 1
const val EIGHTEEN_YEARS_VALUE = 18
const val ONE_HUNDRED_TWENTY_YEARS_VALUE = 120
const val DATE_MIN_YEAR = 0
const val DATE_MIN_MONTH = 0
const val DATE_MIN_DAY = 1

val YEAR_FORMAT = SimpleDateFormat(YEAR_PATTER, Locale.getDefault())
val DAY_FORMAT = SimpleDateFormat(DAY_PATTERN, Locale.getDefault())
val API_DATE_FORMAT = SimpleDateFormat(ISO_8601_API_FORMAT_PATTERN, Locale.getDefault())
val SHORT_DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
val BAR_DIVIDER_FORMAT = SimpleDateFormat("dd | MM | yyyy", Locale.getDefault())
val SHORT_TIME_FORMAT = SimpleDateFormat("hh:mm a", Locale.getDefault())
val BAR_DIVIDER_FORMAT_YEAR_TWO_DIGITS = SimpleDateFormat("dd | MM | yy", Locale.getDefault())
val DATE_TIME_DOCUMENTS_FORMAT = DateTimeFormatter.ofPattern("ddMMyyHHmmss")
val API_DATE_AND_TIME_FORMAT =
    SimpleDateFormat(YEAR_MONTH_DAY_AND_TIME_BAR_FORMAT, Locale.getDefault())
