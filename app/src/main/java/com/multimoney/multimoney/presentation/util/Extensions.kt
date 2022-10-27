package com.multimoney.multimoney.presentation.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.multimoney.multimoney.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration

fun Context.openWhatsAppDeepLink(link: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(link)
    this.startActivity(intent)
}

fun Context.openMapsLink(latitude: String, longitude: String) {
    val mapsIntentUri = Uri.parse(String.format("geo:%s,%s", latitude, longitude))
    val mapIntent = Intent(Intent.ACTION_VIEW, mapsIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")
    this.startActivity(mapIntent)
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