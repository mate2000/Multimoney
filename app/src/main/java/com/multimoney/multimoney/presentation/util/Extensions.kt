package com.multimoney.multimoney.presentation.util

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.database.Cursor
import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.net.Uri
import android.nfc.cardemulation.CardEmulation
import android.os.Build
import android.provider.ContactsContract
import android.provider.Settings.Secure
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.extension.findActivity
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType.All
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType.Colon
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType.Dollar
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType.Quetzal
import com.multimoney.multimoney.presentation.util.catalog.PaymentMethodType
import com.multimoney.multimoney.presentation.util.catalog.PaymentMethodType.CashPaymentPoint
import com.multimoney.multimoney.presentation.util.catalog.PaymentMethodType.TransferBank
import com.multimoney.multimoney.presentation.util.catalog.PaymentMethodType.VisaDirect
import com.multimoney.multimoney.presentation.util.catalog.PhoneCountryCode
import com.multimoney.multimoney.presentation.util.catalog.SourceIncomeType
import com.novopayment.sdk.vts.module.payment.apdu.PaymentService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Locale
import kotlin.time.Duration

fun Context.openWhatsAppDeepLink(link: String, onFailure: () -> Unit = {}) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(link)
        this.startActivity(intent)
    } catch (nullException: NullPointerException) {
        onFailure()
    } catch (security: SecurityException) {
        onFailure()
    } catch (noActivity: ActivityNotFoundException) {
        onFailure()
    }
}

fun Context.openMapsLink(locationAddress: String) {
    val mapsIntentUri = Uri.parse(locationAddress)
    val mapIntent = Intent(Intent.ACTION_VIEW, mapsIntentUri)
    mapIntent.setPackage(resources.getString(R.string.payment_location_intent_package))
    this.startActivity(mapIntent)
}

fun Context.openIntent(intent: Intent, onFailure: () -> Unit) {
    try {
        this.startActivity(intent)
    } catch (security: SecurityException) {
        onFailure()
    } catch (noActivity: ActivityNotFoundException) {
        onFailure()
    }
}

/**
 * checkPermission
 *
 * This function helps to decide what action to take when requesting a system permission
 *
 * Params:
 * @param permission: the permission to be requested
 * @param permissionGrantedAction: the action to perform if the permission is already granted
 * @param showRationaleAction: the action for showing an UI to the user explaining why the permission
 * is needed in case the user denies (or has already denied) the permission
 * @param launcher: the activity for result launcher to launch the request permission dialog if the
 * permission is not granted,
 * @param comesFromRationale: a flag to know if the launcher is launched from a rationale
 * @param launchFromRationale: an action to perform before launching the laucher
 * @param isFirstRequest: flag to know if it is the first time the permission is requested
 */

fun Context.checkPermission(
    permission: String,
    permissionGrantedAction: () -> Unit,
    showRationaleAction: (isPermanentlyDenied: Boolean) -> Unit,
    launchFromRationale: (isLastRetry: Boolean) -> Unit,
    comesFromRationale: Boolean = false,
    isFirstRequest: Boolean = false,
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
    val isGranted = ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED
    val showRationale = this.findActivity()
        ?.let { ActivityCompat.shouldShowRequestPermissionRationale(it, permission) }
    when {
        isGranted -> permissionGrantedAction()
        comesFromRationale && showRationale == true -> {
            launchFromRationale(true)
            launcher.launch(permission)
        }
        comesFromRationale.not() && showRationale == true -> showRationaleAction(false)
        comesFromRationale.not() && showRationale == false && isFirstRequest.not() -> showRationaleAction(true)
        isFirstRequest && showRationale == false -> launcher.launch(permission)
        else -> launcher.launch(permission)
    }
}

fun Context.getPhoneNumbers(): List<String> {
    val context = this
    val numbers = mutableListOf<String>()
    val contentResolver = context.contentResolver
    val phones: Cursor? = contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null,
        null,
        null,
        null
    )
    if (phones != null) {
        while (phones.moveToNext()) {
            val index = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            if (index >= 0) {
                numbers.add(phones.getString(index))
            }
        }
        phones.close()
    }
    return numbers
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

fun String?.getCurrencySymbol(): Int {
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

fun getDeviceManufacture(): String = (
    if (Build.MODEL.startsWith(Build.MANUFACTURER, ignoreCase = true)) {
        Build.MODEL
    } else {
        "${Build.MANUFACTURER} ${Build.MODEL}"
    }
    ).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

fun Context.getAndroidId(): String {
    return Secure.getString(
        this.contentResolver,
        Secure.ANDROID_ID
    )
}

fun Char.isValidAmountCharacter() =
    this.isDigit() || this == DECIMAL_SEPARATOR

fun String.filterInvalidAmountInput() = this.filter { it.isValidAmountCharacter() }

fun Double.roundToTwoDecimalPlaces() = String.format(TWO_DECIMALS_FORMAT, this)
fun Double.roundToTwoDecimalPlacesWithoutNegatives() =
    String.format(TWO_DECIMALS_FORMAT, this).replace("-", "")

fun Double.toCurrencyFormat(
    symbol: String = Dollar.symbol,
    amountOfDecimals: Int = DEFAULT_AMOUNT_OF_DECIMALS
): String {
    val formatter = NumberFormat.getCurrencyInstance()
    formatter.maximumFractionDigits = amountOfDecimals
    //remove the default dollar symbol from the custom symbol property
    return "$symbol${formatter.format(this).replace(Dollar.symbol, "")}"
}

fun Double.toCurrencyFormatWithoutNegatives(
    symbol: String = Dollar.symbol,
    amountOfDecimals: Int = DEFAULT_AMOUNT_OF_DECIMALS
): String {
    val formatter = NumberFormat.getCurrencyInstance()
    formatter.maximumFractionDigits = amountOfDecimals
    //remove the default dollar symbol from the custom symbol property
    return "$symbol${formatter.format(this)
        .replace(Dollar.symbol, "")
        .replace("-", "")
    }"
}

fun String.getCardNumberOne() = this.substring(0, 4)
fun String.getCardNumberTwo() = this.substring(4, 8)
fun String.getCardNumberThree() = this.substring(8, 12)
fun String.getCardNumberFour() = this.substring(12, 16)
fun String.formatExpirationDate() = if (this.length == 3) {
    this.plus("0").plus(this.first()).plus("/").plus(this.takeLast(2))
} else if (this.length == 4) {
    this.take(2).plus("/").plus(this.takeLast(2))
} else {
    this
}

fun String.encodeURLToUTF(): String {
    return URLEncoder.encode(this, StandardCharsets.UTF_8.toString())
}

fun String.decodeURLFromUTF(): String {
    return URLDecoder.decode(this, StandardCharsets.UTF_8.toString())
}

/**
 * split a string by whitespace character ' '
 */
fun String.splitByWhiteSpace() = split(WHITE_SPACE_SEPARATOR)

fun Context.getTapAndPayIntent(): Intent {
    val intent = Intent(CardEmulation.ACTION_CHANGE_DEFAULT)
    intent.putExtra(CardEmulation.EXTRA_CATEGORY, CardEmulation.CATEGORY_PAYMENT)
    intent.putExtra(
        CardEmulation.EXTRA_SERVICE_COMPONENT,
        ComponentName(this, PaymentService::class.java)
    )
    return intent
}

fun String.addTextStyleToTextPortion(textToStyle: String, style: TextStyle): AnnotatedString {
    val fullText = this
    val startIndex = fullText.indexOf(textToStyle)
    val endIndex = startIndex + textToStyle.length
    return buildAnnotatedString {
        append(fullText)
        addStyle(
            style = style.toSpanStyle(),
            start = startIndex,
            end = endIndex
        )
    }
}

fun CharSequence.replaceNumbersToZero() = replace(Regex(DIGITS_REGEX), ZERO_STRING)

fun getCountryCodeByIdBrand(idBrand: Int): String {
    return when (idBrand) {
        Brand.ElSalvador.id -> PhoneCountryCode.EL_SALVADOR.code
        Brand.CostaRica.id -> PhoneCountryCode.COSTA_RICA.code
        Brand.ElSalvador.id -> PhoneCountryCode.GUATEMALA.code
        else -> ""
    }
}

fun String.capitalizedAllWords(): String =
    splitByWhiteSpace().joinToString(WHITE_SPACE_SEPARATOR.toString()) { it.capitalized() }

private const val HEX_FORMAT = "#%02x%02x%02x"
private const val SPECIAL_CHARACTER_REGEX = "[!\"#\$%&'()*+,-./:;\\\\<=>?@^_`{|}~]"
private const val NUMBER_REGEX = "[0-9]"
private const val DECIMAL_SEPARATOR = '.'
private const val WHITE_SPACE_SEPARATOR = ' '
private const val DIGITS_REGEX = "\\d"
private const val ZERO_STRING = "0"
private const val DEFAULT_AMOUNT_OF_DECIMALS = 2
