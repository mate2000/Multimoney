package com.multimoney.multimoney.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class PurchaseCryptoTimerHelper(
    val coroutineScope: CoroutineScope ,
    val time: Int,
    val isBottomSheetOpen: Boolean = false,
    val onTick: (Int) -> Unit,
    val onFinished: () -> Unit
) {
    private var counter = time
    private var job: Job? = null

    fun startTimer() {
        job?.let {
            if (isBottomSheetOpen) {
                resetTimerWithSpecificTime()
            } else {
                resetTimer()
            }
        }
        job = coroutineScope.launch(Dispatchers.Default) {
            while (counter > 0) {
                delay(1.seconds)
                counter--
                onTick(counter)
            }
            counter = if (isBottomSheetOpen) TIME_WITH_BOTTOM_SHEET_OPEN else time
            onFinished()
        }
    }

    fun stopTimer() {
        job?.cancel()
    }

    private fun resetTimerWithSpecificTime() {
        stopTimer()
        counter = TIME_WITH_BOTTOM_SHEET_OPEN
    }

    private fun resetTimer() {
        stopTimer()
        counter = time
    }

    companion object {
        const val TIME_WITH_BOTTOM_SHEET_OPEN = 20
    }
}
