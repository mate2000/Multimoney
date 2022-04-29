package com.multimoney.multimoney.presentation.ui.login.signup.otp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.format
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class SignUpOtpViewModel @Inject constructor() : BaseViewModel() {


    // Fields
    var otp by mutableStateOf("")
    var otpError by mutableStateOf(Pair(false, R.string.sign_up_otp_code_not_valid))

    // Interactions
    var phaseCount by mutableStateOf(PHASE_ONE)
    var remainingTime: Duration by mutableStateOf(Duration.ofSeconds(TIMER_DURATION))
    var isTimerRunning by mutableStateOf(true)
    var remainingTimeText by mutableStateOf(remainingTime.format())
    var isOtpFromSms by mutableStateOf(false)

    fun getOtpFromMessage(message: String) {
        val otpMatcher = Pattern.compile(OTP_MESSAGE_REGEX).matcher(message)
        if (otpMatcher.find()) {
            clearOtpError()
            otp = otpMatcher.group(0)?.toString() ?: ""
            isOtpFromSms = true
        }
    }

    fun isTimerTick() = remainingTime.seconds > 0 && isTimerRunning

    fun onTimerTick() {
        remainingTime = remainingTime.minusSeconds(1L)
        remainingTimeText = remainingTime.format()
    }

    fun onTimerFinish() {
        isTimerRunning = false
        remainingTime = remainingTime.plusSeconds(TIMER_DURATION)
        remainingTimeText = remainingTime.format()
        phaseCount++
    }

    fun getPhaseResourceString() = when (phaseCount) {
        PHASE_ONE -> R.string.sign_up_otp_expiration_time_phase_one
        PHASE_TWO -> R.string.sign_up_otp_resend
        PHASE_THREE -> R.string.sign_up_otp_expiration_time_phase_three
        PHASE_FOUR -> R.string.sign_up_otp_call
        PHASE_FIVE -> R.string.sign_up_otp_expiration_time_phase_five
        else -> R.string.sign_up_otp_expiration_time_phase_six
    }

    fun getPhaseAction() = when (phaseCount) {
        PHASE_TWO -> resend()
        else -> call()
    }

    private fun resend() {
        phaseCount++
        isTimerRunning = true
    }

    private fun call() {
        phaseCount++
        isTimerRunning = true
    }

    fun isFormValid() =
        otp.trim().isNotEmpty() && otp.trim().length == TOTAL_DIGITS && phaseCount < PHASE_FIVE

    fun clearOtpError() {
        otpError = Pair(false, R.string.error_empty)
    }

    companion object {
        const val PHASE_ONE = 1
        const val PHASE_TWO = 2
        const val PHASE_THREE = 3
        const val PHASE_FOUR = 4
        const val PHASE_FIVE = 5

        const val TOTAL_DIGITS = 4

        const val TIMER_DURATION = 59L
        const val TIMER_DELAY = 1000L

        const val OTP_MESSAGE_REGEX = "(|^)\\d{$TOTAL_DIGITS}"
    }
}