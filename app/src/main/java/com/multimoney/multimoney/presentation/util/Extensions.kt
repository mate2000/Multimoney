package com.multimoney.multimoney.presentation.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.uielement.IconType
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

fun Int.getIconDrawableById() = when (this) {
    IconType.Salaried.iconId -> R.drawable.ic_salaried
    IconType.FreeLancer.iconId -> R.drawable.ic_freelancer
    IconType.OwnBusiness.iconId -> R.drawable.ic_own_business
    IconType.Retired.iconId -> R.drawable.ic_retired
    else -> R.drawable.ic_other
}
