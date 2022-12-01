package com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.phone

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.security.MutationSendPinProcessUseCase
import com.multimoney.domain.model.security.SendPinProcess
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.SEND_METHOD
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel
import com.multimoney.multimoney.presentation.util.OTP_MESSAGE_REGEX
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.format
import com.multimoney.multimoney.presentation.util.tickerFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import java.time.LocalDateTime
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

@HiltViewModel
class ValidateOTPViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val mutationSendPinProcessUseCase: MutationSendPinProcessUseCase,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    val onCallMutationSendPinProcessEvent = MutableSharedFlow<MultimoneyResult<SendPinProcess?>>()
    private var linkWhatsapp = ""
    var userBlockedForMaxAttend = ""

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    data class UIState(

        val idBrand: Int? = null,
        val identification: String? = null,
        val email: String? = null,
        val pkUser: String? = null,
        val userName: String? = null,
        val phoneNumber: String? = null,
        val firstName: String? = null,
        val sendMethod: String? = null,
        val isLoading: Boolean = true,

        // Fields
        val otp: String = "",
        val otpResend: String? = "",
        val otpError: Pair<Boolean, Int> = Pair(false, R.string.sign_up_otp_code_not_valid),

        val isTimerRunning: Boolean = false,
        // Interactions
        val phaseCount: Int = SignUpOtpViewModel.PHASE_ONE,
        val remainingTime: Duration = TIMER_DURATION.seconds,
        val remainingTimeText: String = remainingTime.format(),
        val isOtpFromSms: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),


    )

    init {
        uiState = uiState.copy(
            idBrand = savedStateHandle[ID_BRAND],
            identification = savedStateHandle[IDENTIFICATION],
            email = savedStateHandle[EMAIL],
            phoneNumber = savedStateHandle[PHONE_NUMBER],
            pkUser = savedStateHandle[PK_USER],
            firstName = savedStateHandle[FIRST_NAME],
            sendMethod = savedStateHandle[SEND_METHOD],
            userName = savedStateHandle[USER]
        )
    }

    private fun initializeTimer(
        phaseCount: Int = uiState.phaseCount,
        totalTime: Long = SignUpOtpViewModel.TIMER_DURATION,
    ) {
        val newRemainingTime = totalTime.seconds
        uiState = uiState.copy(
            phaseCount = phaseCount,
            isTimerRunning = true,
            remainingTime = newRemainingTime,
            remainingTimeText = newRemainingTime.format(),
            otp = ""
        )
    }

    fun isFormValid() =

            uiState.otp.trim()
                .isNotEmpty() && uiState.otp.trim().length == SignUpOtpViewModel.TOTAL_DIGITS



    private fun onOtpValueChange(value: String) {
        uiState = uiState.copy(
            otp = value, isOtpFromSms = false, otpError = Pair(false, R.string.error_empty)
        )

        isFormValid()
    }

    private fun callMutationSendPinProcess(
        identification: String,
        firstName: String,
        email: String,
        cellphone: String,
        sendMethod: String,
        pkUser: String,
        idBrand: Int,
        user: String,
    ) = executeUseCase {
        uiState = uiState.copy(isTimerRunning = false)
        mutationSendPinProcessUseCase.invoke(
            identification, firstName, email, cellphone, sendMethod, pkUser, idBrand, user
        ).collectLatest { result ->
            onCallMutationSendPinProcessEvent.emit(result)
        }
    }

    init {
        uiState = uiState.copy(
            idBrand = savedStateHandle[ID_BRAND]
        )
    }

    private fun getOtpFromMessage(message: String) {
        val otpMatcher = Pattern.compile(OTP_MESSAGE_REGEX).matcher(message)
        if (otpMatcher.find()) {
            uiState = uiState.copy(
                otp = otpMatcher.group(0)?.toString() ?: "",
                isOtpFromSms = true,
                otpError = Pair(false, R.string.error_empty)
            )
        }
    }

    private fun isTimerTick() = uiState.remainingTime.inWholeSeconds > 0 && uiState.isTimerRunning
    private fun onTimerTick() {
        val newRemainingTime = uiState.remainingTime.minus(SignUpOtpViewModel.TIMER_DELAY.seconds)
        uiState = uiState.copy(
            remainingTime = newRemainingTime, remainingTimeText = newRemainingTime.format()
        )
    }

    private fun onTimerFinish() {
        val newRemainingTime = uiState.remainingTime.plus(SignUpOtpViewModel.TIMER_DURATION.seconds)
        uiState = uiState.copy(
            isTimerRunning = false,
            remainingTime = newRemainingTime,
            remainingTimeText = newRemainingTime.format(),
            phaseCount = uiState.phaseCount.plus(1)
        )
    }

    private fun onExecuteTimer() {
        tickerFlow(
            period = SignUpOtpViewModel.TIMER_DELAY.seconds,
            initialDelay = SignUpOtpViewModel.TIMER_DELAY.seconds,
            duration = uiState.remainingTime.toLong(DurationUnit.SECONDS).seconds
        ).takeWhile { uiState.isTimerRunning }.map {
            LocalDateTime.now()
        }.distinctUntilChanged { old, new ->
            old.second == new.second
        }.onEach {
            if (isTimerTick()) {
                onTimerTick()
            } else if (uiState.isTimerRunning) {
                onTimerFinish()
            }
        }.launchIn(viewModelScope)
    }

    private fun onCallMutationSendPinProcessSuccess(pinProcess: SendPinProcess?) {
        uiState = uiState.copy(otpResend = pinProcess?.nextType)
        initializeTimer(
            totalTime = pinProcess?.pinExpirationTime?.toLong() ?: SignUpOtpViewModel.TIMER_DURATION
        )
        onExecuteTimer()
    }

    private fun onStart(linkWhatsapp: String, userBlockedForMaxAttend: String) {
        this.linkWhatsapp = linkWhatsapp
        this.userBlockedForMaxAttend = userBlockedForMaxAttend
    }
    private fun onNavigateToLogin(){
        //TODO
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnStart -> onStart(event.linkWhatsapp, event.userBlockedForMaxAttends)
            is UIEvent.OnNavigateBack -> navigateBack(Screen.HomeScreen.route, isRestart = true)
            is UIEvent.OnNavigateToLogin -> onNavigateToLogin()
            is UIEvent.OnGetOtpFromMessage -> getOtpFromMessage(event.message)
            is UIEvent.OnCallMutationSendPinProcess -> callMutationSendPinProcess(
                event.identification,
                event.firstName,
                event.email,
                event.cellphone,
                event.sendMethod,
                event.pkUser,
                event.idBrand,
                event.user
            )
            is UIEvent.OnValidateForm -> isFormValid()
            is UIEvent.OnCallMutationSendPinProcessSuccess -> onCallMutationSendPinProcessSuccess(
                event.pinProcess
            )
            is UIEvent.OnOtpValueChange -> onOtpValueChange(event.value)
            is UIEvent.OnInitializeTimer -> initializeTimer(event.phaseCount, event.time)
            is UIEvent.OnLoadingValueChange -> uiState = uiState.copy(isLoading = event.isLoading)
            is UIEvent.OnFailureWithDialog -> uiState =
                uiState.copy(isLoading = event.isLoading, openDialog = event.openDialog)
        }
    }

    sealed class UIEvent {
        data class OnStart(
            val linkWhatsapp: String,
            val userBlockedForMaxAttends: String,
        ) : UIEvent()

        object OnNavigateToLogin : UIEvent()

        data class OnGetOtpFromMessage(val message: String) : UIEvent()
        data class OnCallMutationSendPinProcess(
            val identification: String,
            val firstName: String,
            val email: String,
            val cellphone: String,
            val sendMethod: String,
            val pkUser: String,
            val idBrand: Int,
            val user: String,
        ) : UIEvent()

        data class OnCallMutationSendPinProcessSuccess(
            val pinProcess: SendPinProcess?,
        ) : UIEvent()

        data class OnFailureWithDialog(val isLoading: Boolean, val openDialog: DialogParameters) :
            UIEvent()

        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()


        data class OnInitializeTimer(val phaseCount: Int, val time: Long) : UIEvent()

        data class OnOtpValueChange(val value: String) : UIEvent()

        object OnValidateForm : UIEvent()

        object OnNavigateBack : UIEvent()

    }

    companion object {
        const val TIMER_DURATION = 0L

    }
}
