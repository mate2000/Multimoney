package com.multimoney.multimoney.presentation.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration

fun Context.openWhatsAppDeepLink(link: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(link)
    this.startActivity(intent)
}

fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO, duration: Duration = Duration.ZERO) = flow {
    var durationTime = duration
    delay(initialDelay)
    while (durationTime >= Duration.ZERO) {
        emit(Unit)
        delay(period)
        durationTime = durationTime.minus(period)
    }
}