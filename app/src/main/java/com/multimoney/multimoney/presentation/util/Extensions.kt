package com.multimoney.multimoney.presentation.util

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration

fun Context.openWhatsAppDeepLink(link: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(link)
    this.startActivity(intent)
}

fun Context.sendAccount(client: String, accountNumber: String) {
    val clientStringLabel = getString(R.string.credit_detail_client)
    val ibanAccountLabel = getString(R.string.credit_detail_iban_number)
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "$clientStringLabel: ${client.uppercase()}\n$ibanAccountLabel: $accountNumber"
        )
        type = "text/plain"
    }
    val chooser = Intent.createChooser(intent, "")
    this.startActivity(chooser)
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


// Currency
fun Int.getCurrency(): CurrencyType {
    return when (this) {
        Colon.id -> Colon
        Dollar.id -> Dollar
        Quetzal.id -> Quetzal
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

fun Int.getCurrencySymbolValue(): Int {
    return when (this) {
        Brand.ElSalvador.id -> R.string.dollar_symbol_value
        Brand.CostaRica.id -> R.string.colon_symbol_value
        Brand.Guatemala.id -> R.string.quetzal_symbol_value
        else -> R.string.empty
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
