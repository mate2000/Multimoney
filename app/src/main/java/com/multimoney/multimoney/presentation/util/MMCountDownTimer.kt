package com.multimoney.multimoney.presentation.util

import android.os.CountDownTimer

class MMCountDownTimer {

    private var timer: CountDownTimer? = null
    private var milliInFuture: Long? = null
    private var onCountDownTimerFinish: OnCountDownTimerFinish? = null
    private var isTimerStopped = false

    fun subscribe(listener: OnCountDownTimerFinish) {
        onCountDownTimerFinish = listener
    }

    fun startTimer(milliInFuture: Long?) {
        this.milliInFuture = milliInFuture
        timer?.cancel()
        timer = null

        timer = object : CountDownTimer(milliInFuture ?: 0, COUNT_DOWN_INTERVAL) {
            override fun onTick(millisMainUntilFinished: Long) {}
            override fun onFinish() {
                if (isTimerStopped.not()) {
                    onCountDownTimerFinish?.onFinished()
                    discardTimer()
                }
            }
        }
        timer?.start()
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

    interface OnCountDownTimerFinish {
        fun onFinished()
    }

    companion object {
        private const val COUNT_DOWN_INTERVAL = 1000L
    }
}
