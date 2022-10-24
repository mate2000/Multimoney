package com.multimoney.multimoney.presentation.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.util.catalog.SourceIncomeIconType
import kotlin.time.Duration
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

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
