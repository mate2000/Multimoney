package com.multimoney.multimoney.presentation.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.util.catalog.SourceIncomeIconType
import kotlin.time.Duration
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

fun Context.openWhatsAppDeepLink(link: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(link)
    this.startActivity(intent)
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
    SourceIncomeIconType.Salaried.iconId,
    SourceIncomeIconType.FormalSalaried.iconId -> R.drawable.ic_salaried
    SourceIncomeIconType.FreeLancer.iconId,
    SourceIncomeIconType.OwnBusinessOnPersonalBasis.iconId -> R.drawable.ic_freelancer
    SourceIncomeIconType.OwnBusiness.iconId,
    SourceIncomeIconType.OwnBusinessInPartnership.iconId -> R.drawable.ic_own_business
    SourceIncomeIconType.Retired.iconId -> R.drawable.ic_retired
    SourceIncomeIconType.Other.iconId -> R.drawable.ic_other
    else -> R.drawable.ic_other
}

/**
 * get currency symbol by idBrand
 */
fun Int.getCurrencySymbol(): Int {
    return when (this) {
        Brand.ElSalvador.id -> R.string.credit_monthly_income_dollar_symbol
        Brand.CostaRica.id -> R.string.credit_monthly_income_colon_symbol
        Brand.Guatemala.id -> R.string.credit_monthly_income_quetzal_symbol
        else -> R.string.empty
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
