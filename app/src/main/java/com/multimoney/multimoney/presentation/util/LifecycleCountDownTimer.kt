package com.multimoney.multimoney.presentation.util

import android.os.CountDownTimer
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class LifecycleCountDownTimer() : DefaultLifecycleObserver {

    private var timer: CountDownTimer? = null
    private var milliInFuture: Long? = null
    private var countDownInterval: Long? = null
    private var onCountDownTimerFinish: OnCountDownTimerFinish? = null

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
                onCountDownTimerFinish?.onFinished()
                discardTimer()
            }
        }
        timer?.start()
    }

    fun restartTimer() {
        milliInFuture?.let {
            startTimer(it)
        }
    }

    fun stopTimer() {
        timer?.cancel()
    }

    fun discardTimer() {
        milliInFuture = null
        countDownInterval = null
        timer?.cancel()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        discardTimer()
    }

    interface OnCountDownTimerFinish {
        fun onFinished()
    }

    companion object {
        private const val COUNT_DOWN_INTERVAL = 1000L
    }
}
