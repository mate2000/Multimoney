package com.multimoney.multimoney.presentation.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlin.time.Duration
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

fun Context.openWhatsAppDeepLink(link: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(link)
    this.startActivity(intent)
}

fun Context.sendAccount(client: String, accountNumber: String) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Cliente: $client\nNumero de cuenta IBAN: $accountNumber")
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