package com.multimoney.multimoney.presentation.util

import java.time.Duration
import kotlin.math.abs

private const val TIME_FORMAT = "%02d:%02d"

//convert time to milli seconds
fun Duration.format(): String {
    val seconds = abs(seconds)
    val value = String.format(
        "%02d:%02d",
        seconds % 3600 / 60,
        seconds % 60
    )
    return value
}
