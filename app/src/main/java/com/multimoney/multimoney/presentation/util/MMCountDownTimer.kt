package com.multimoney.multimoney.presentation.util

import android.os.CountDownTimer

class MMCountDownTimer {

    private var timer: CountDownTimer? = null
    private var milliInFuture: Long? = null
    private var onCountDownTimerFinish: OnCountDownTimerEvents? = null
    private var isTimerStopped = false

    fun subscribe(listener: OnCountDownTimerEvents) {
        onCountDownTimerFinish = listener
    }

    fun startTimer(milliInFuture: Long?) {
        this.milliInFuture = milliInFuture
        timer?.cancel()
        timer = null

        timer = object : CountDownTimer(milliInFuture ?: 0, COUNT_DOWN_INTERVAL) {
            override fun onTick(millisMainUntilFinished: Long) {
                if (milliInFuture != null) {
                    if (isTimerStopped.not() && millisMainUntilFinished <= FINISHING_INTERVAL) {
                        onCountDownTimerFinish?.onMaxTimeUsed(millisMainUntilFinished.div(COUNT_DOWN_INTERVAL))
                    }
                }
            }
            override fun onFinish() {
                if (isTimerStopped.not()) {
                    discardTimer()
                    onCountDownTimerFinish?.onFinished()
                }
            }
        }
//        timer?.start()
    }

    fun restartTimer() {
        if (isTimerStopped.not()) {
            milliInFuture?.let {
                startTimer(it)
            }
        }
    }

    fun resumeTimer() {
        isTimerStopped = false
        milliInFuture?.let {
            startTimer(it)
        }
    }

    fun stopTimer() {
        timer?.cancel()
        timer = null
        isTimerStopped = true
    }

    fun discardTimer() {
        milliInFuture = null
        timer?.cancel()
    }

    interface OnCountDownTimerEvents {
        fun onFinished()
        fun onMaxTimeUsed(millisMainUntilFinished: Long)
    }

    companion object {
        private const val COUNT_DOWN_INTERVAL = 1000L
        private const val FINISHING_INTERVAL = 15000L
    }
}
