package com.multimoney.multimoney.presentation.ui.login.signup.otp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.domain.interaction.security.MutationSendPinProcessUseCase
import com.multimoney.domain.interaction.security.QueryValidatePinUseCase
import com.multimoney.domain.model.security.SendPinProcess
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.UIEvent.OnCallMutationSendPinProcess
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.UIEvent.OnCallMutationSendPinProcessSuccess
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.UIEvent.OnGetOtpFromMessage
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.UIEvent.OnInitializeTimer
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.UIEvent.OnNavigateToSignIn
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.UIEvent.OnOtpValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.multimoney.multimoney.presentation.util.OTP_MESSAGE_REGEX
import com.multimoney.multimoney.presentation.util.ResendOtp
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

@HiltViewModel
class SignUpOtpViewModel @Inject constructor(
    private val mutationSendPinProcessUseCase: MutationSendPinProcessUseCase,
    private val queryValidatePinUseCase: QueryValidatePinUseCase
) : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var linkWhatsapp = ""
    var userBlockedForMaxAttend = ""

    // Events
    val onCallMutationSendPinProcessEvent = MutableSharedFlow<MultimoneyResult<SendPinProcess?>>()

    private fun onStart(linkWhatsapp: String, userBlockedForMaxAttend: String) {
        this.linkWhatsapp = linkWhatsapp
        this.userBlockedForMaxAttend = userBlockedForMaxAttend
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
        val newRemainingTime = uiState.remainingTime.minus(TIMER_DELAY.seconds)
        uiState = uiState.copy(
            remainingTime = newRemainingTime,
            remainingTimeText = newRemainingTime.format()
        )
    }

    private fun initializeTimer(
        phaseCount: Int = uiState.phaseCount,
        totalTime: Long = TIMER_DURATION,
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

    private fun onTimerFinish() {
        val newRemainingTime = uiState.remainingTime.plus(TIMER_DURATION.seconds)
        uiState = uiState.copy(
            isTimerRunning = false,
            remainingTime = newRemainingTime,
            remainingTimeText = newRemainingTime.format(),
            phaseCount = uiState.phaseCount.plus(1)
        )
    }

    fun getPhaseResourceString() = when (uiState.phaseCount) {
        PHASE_ONE -> R.string.sign_up_otp_expiration_time_phase_one
        PHASE_TWO -> R.string.sign_up_otp_resend
        PHASE_THREE -> R.string.sign_up_otp_expiration_time_phase_three
        PHASE_FOUR -> if (uiState.otpResend == ResendOtp.SMS.option) R.string.sign_up_otp_sms else R.string.sign_up_otp_call
        PHASE_FIVE -> R.string.sign_up_otp_expiration_time_phase_five
        else -> R.string.sign_up_otp_expiration_time_phase_six
    }

    fun getNextStep(isOnFidoVerified: Boolean) = if (isOnFidoVerified.not()) {
        SignUpStep.Five
    } else {
        SignUpStep.Six
    }

    private fun getPhaseAction() {
        when (uiState.phaseCount) {
            PHASE_TWO, PHASE_FOUR -> resend()
        }
    }

    private fun resend() {
        uiState = uiState.copy(
            isTimerRunning = true,
            phaseCount = uiState.phaseCount.plus(1)
        )
    }

    private fun isFormValid() = emitBaseEvent(
        OnFormValidateCompleted(
            uiState.otp.trim()
                .isNotEmpty() && uiState.otp.trim().length == TOTAL_DIGITS && uiState.phaseCount < PHASE_FIVE
        )
    )

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
            identification,
            firstName,
            email,
            cellphone,
            sendMethod,
            pkUser,
            idBrand,
            user
        ).collectLatest { result ->
            onCallMutationSendPinProcessEvent.emit(result)
        }
    }

    private fun navigateToSignIn() {
        popAndNavigateTo(
            route = Screen.SignInScreen.route,
            popTo = Screen.SignUpScreen.route
        )
    }

    private fun onNextActionClick(
        pkUser: String?,
        idBrand: Int?,
        phone: String?,
        name: String?,
        onUseDataValueChange: () -> Unit,
        onCallMutationUpdateUserRegisterUseCase: () -> Unit,
        onPhoneVerifiedChanged: () -> Unit,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit,
    ) {
        executeUseCase {
            queryValidatePinUseCase.invoke(
                idBrand = idBrand ?: 0,
                appSource = APP_SOURCE,
                pkUser = pkUser ?: "",
                pinSecurity = uiState.otp,
                telephone = phone,
                userCreate = name ?: ""
            ).collectLatest { result ->
                result.onSuccess {
                    onUseDataValueChange.invoke()
                    onPhoneVerifiedChanged.invoke()
                    onCallMutationUpdateUserRegisterUseCase.invoke()
                }
                    .onLoading {
                        onLoadingValueChange(true)
                    }
                    .onMessage {
                        uiState =
                            uiState.copy(otpError = Pair(true, R.string.sign_up_otp_code_not_valid))
                        onLoadingValueChange(false)
                    }
                    .onFailure {
                        onFailureWithDialog(
                            false,
                            DialogParameters(
                                description = it.getError().toString(),
                                isActive = mutableStateOf(true)
                            )
                        )
                    }
            }
        }
    }

    private fun onCallMutationSendPinProcessSuccess(
        onLoadingValueChange: () -> Unit,
        pinProcess: SendPinProcess?,
    ) {
        uiState = uiState.copy(otpResend = pinProcess?.type)
        initializeTimer(totalTime = pinProcess?.pinExpirationTime?.toLong() ?: TIMER_DURATION)
        getPhaseAction()
        onExecuteTimer()
        onLoadingValueChange.invoke()
    }

    private fun onOtpValueChange(value: String) {
        uiState = uiState.copy(
            otp = value,
            isOtpFromSms = false,
            otpError = Pair(false, R.string.error_empty)
        )
        isFormValid()
    }

    private fun onExecuteTimer() {
        tickerFlow(
            period = TIMER_DELAY.seconds,
            initialDelay = TIMER_DELAY.seconds,
            duration = TIMER_DURATION.seconds
        )
            .takeWhile { uiState.isTimerRunning }
            .map {
                LocalDateTime.now()
            }
            .distinctUntilChanged { old, new ->
                old.second == new.second
            }
            .onEach {
                if (isTimerTick()) {
                    onTimerTick()
                } else if (uiState.isTimerRunning) {
                    onTimerFinish()
                }
            }
            .launchIn(viewModelScope)
    }

    data class UIState(
        // Fields
        val otp: String = "",
        val otpResend: String? = "",
        val otpError: Pair<Boolean, Int> = Pair(false, R.string.sign_up_otp_code_not_valid),

        // Interactions
        val phaseCount: Int = PHASE_ONE,
        val remainingTime: Duration = TIMER_DURATION.seconds,
        val isTimerRunning: Boolean = false,
        val remainingTimeText: String = remainingTime.format(),
        val isOtpFromSms: Boolean = false,
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnStart -> onStart(event.linkWhatsapp, event.userBlockedForMaxAttends)
            is OnNextActionClick -> onNextActionClick(
                event.pkUser,
                event.idBrand,
                event.phone,
                event.name,
                event.onUseDataValueChange,
                event.onCallMutationUpdateUserRegisterUseCase,
                event.onPhoneVerifiedChanged,
                event.onLoadingValueChange,
                event.onFailureWithDialog
            )
            is OnGetOtpFromMessage -> getOtpFromMessage(event.message)
            is OnCallMutationSendPinProcess -> callMutationSendPinProcess(
                event.identification,
                event.firstName,
                event.email,
                event.cellphone,
                event.sendMethod,
                event.pkUser,
                event.idBrand,
                event.user
            )
            is OnValidateForm -> isFormValid()
            is OnNavigateToSignIn -> navigateToSignIn()
            is OnCallMutationSendPinProcessSuccess -> onCallMutationSendPinProcessSuccess(
                event.onLoadingValueChange,
                event.pinProcess
            )
            is OnOtpValueChange -> onOtpValueChange(event.value)
            is OnInitializeTimer -> initializeTimer(event.phaseCount, event.time)
        }
    }

    sealed class UIEvent {
        data class OnStart(
            val linkWhatsapp: String,
            val userBlockedForMaxAttends: String,
        ) : UIEvent()

        data class OnNextActionClick(
            val pkUser: String?,
            val idBrand: Int?,
            val phone: String?,
            val name: String?,
            val onUseDataValueChange: () -> Unit,
            val onCallMutationUpdateUserRegisterUseCase: () -> Unit,
            val onPhoneVerifiedChanged: () -> Unit,
            val onLoadingValueChange: (status: Boolean) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit,
        ) : UIEvent()

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
            val onLoadingValueChange: () -> Unit,
            val pinProcess: SendPinProcess?,
        ) : UIEvent()

        data class OnInitializeTimer(val phaseCount: Int, val time: Long) : UIEvent()

        data class OnOtpValueChange(val value: String) : UIEvent()

        object OnValidateForm : UIEvent()
        object OnNavigateToSignIn : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    companion object {
        const val PHASE_ONE = 1
        const val PHASE_TWO = 2
        const val PHASE_THREE = 3
        const val PHASE_FOUR = 4
        const val PHASE_FIVE = 5

        const val TOTAL_DIGITS = 4

        const val TIMER_DURATION = 59L
        const val TIMER_DELAY = 1L

        const val SEND_METHOD_PHONE = "PHONE"

        const val APP_SOURCE = 2
    }
}