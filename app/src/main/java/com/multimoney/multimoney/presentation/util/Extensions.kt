package com.multimoney.multimoney.presentation.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType.All
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType.Colon
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType.Dollar
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType.Quetzal
import com.multimoney.multimoney.presentation.util.catalog.PaymentMethodType
import com.multimoney.multimoney.presentation.util.catalog.PaymentMethodType.CashPaymentPoint
import com.multimoney.multimoney.presentation.util.catalog.PaymentMethodType.TransferBank
import com.multimoney.multimoney.presentation.util.catalog.PaymentMethodType.VisaDirect
import com.multimoney.multimoney.presentation.util.catalog.SourceIncomeType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration

fun Context.openWhatsAppDeepLink(link: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(link)
    this.startActivity(intent)
}

fun Context.openMapsLink(latitude: String, longitude: String) {
    val mapsIntentUri =
        Uri.parse(
            String.format(
                resources.getString(R.string.payment_location_intent_uri_format),
                latitude,
                longitude
            )
        )
    val mapIntent = Intent(Intent.ACTION_VIEW, mapsIntentUri)
    mapIntent.setPackage(resources.getString(R.string.payment_location_intent_package))
    this.startActivity(mapIntent)
}

fun tickerFlow(
    period: Duration,
    initialDelay: Duration = Duration.ZERO,
    duration: Duration = Duration.ZERO
) = flow {
    var durationTime = duration
    delay(initialDelay)
    while (durationTime >= Duration.ZERO) {
        emit(Unit)
        delay(period)
        durationTime = durationTime.minus(period)
    }
}

/**
 * return the proper icon from the local drawable resources depending on the iconId,
 * either for CR or SV
 */
fun Int.getSourceIncomeIconDrawable() = when (this) {
    SourceIncomeType.Salaried.iconId,
    SourceIncomeType.FormalSalaried.iconId -> R.drawable.ic_salaried
    SourceIncomeType.FreeLancer.iconId,
    SourceIncomeType.OwnBusinessOnPersonalBasis.iconId -> R.drawable.ic_freelancer
    SourceIncomeType.OwnBusiness.iconId,
    SourceIncomeType.OwnBusinessInPartnership.iconId -> R.drawable.ic_own_business
    SourceIncomeType.Retired.iconId -> R.drawable.ic_retired
    SourceIncomeType.Other.iconId -> R.drawable.ic_other
    else -> R.drawable.ic_other
}

// Currency
fun Int.getCurrencyFromId(): CurrencyType {
    return when (this) {
        Colon.id -> Colon
        Dollar.id -> Dollar
        Quetzal.id -> Quetzal
        else -> All
    }
}

fun String.getCurrencyFromValue(): CurrencyType {
    return when (this.lowercase()) {
        Colon.value.lowercase() -> Colon
        Dollar.value.lowercase() -> Dollar
        Quetzal.value.lowercase() -> Quetzal
        else -> All
    }
}

/**
 * get currency symbol by idBrand
 */
fun Int.getCurrencySymbol(): Int {
    return when (this) {
        Brand.ElSalvador.id -> R.string.dollar_symbol
        Brand.CostaRica.id -> R.string.colon_symbol
        Brand.Guatemala.id -> R.string.quetzal_symbol
        else -> R.string.empty
    }
}

fun String.getCurrencySymbolValue(): Int {
    return when (this) {
        Colon.value -> R.string.colon_symbol_value
        Dollar.value -> R.string.dollar_symbol_value
        Quetzal.value -> R.string.quetzal_symbol_value
        else -> R.string.empty
    }
}

fun String.getCurrencySymbol(): Int {
    return when (this) {
        Colon.value -> R.string.colon_symbol
        Dollar.value -> R.string.dollar_symbol
        Quetzal.value -> R.string.quetzal_symbol
        else -> R.string.empty
    }
}

fun String.getCurrencyFromId(): CurrencyType {
    return when (this) {
        Colon.currency -> Colon
        Dollar.currency -> Dollar
        Quetzal.currency -> Quetzal
        else -> All
    }
}

// Payment
fun String.getPaymentMethodType(): PaymentMethodType {
    return when (this) {
        VisaDirect.value -> VisaDirect
        TransferBank.value -> TransferBank
        else -> CashPaymentPoint
    }
}

fun String.getMaskedText(
    maskSymbol: String,
    firstDigits: Int,
    lastDigits: Int
) = take(firstDigits).plus(maskSymbol).plus(takeLast(lastDigits))

// Formatted strings
fun String.hasNumbersAndSpecialCharacters() =
    !this.contains(NUMBER_REGEX.toRegex()) && !this.contains(SPECIAL_CHARACTER_REGEX.toRegex())

fun Color.toHexCode(): String {
    val red = this.red * 255
    val green = this.green * 255
    val blue = this.blue * 255
    return String.format(HEX_FORMAT, red.toInt(), green.toInt(), blue.toInt())
}

val Int.boolean
    get() = this == 1

fun getNavParam(param: String, value: Any?) = "?$param=$value"

private const val HEX_FORMAT = "#%02x%02x%02x"
private const val SPECIAL_CHARACTER_REGEX = "[!\"#\$%&'()*+,-./:;\\\\<=>?@^_`{|}~]"
private const val NUMBER_REGEX = "[0-9]"
