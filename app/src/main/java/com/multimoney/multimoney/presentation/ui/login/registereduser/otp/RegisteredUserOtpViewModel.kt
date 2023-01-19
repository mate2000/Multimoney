package com.multimoney.multimoney.presentation.ui.login.registereduser.otp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.domain.interaction.security.MutationSendPinProcessUseCase
import com.multimoney.domain.interaction.security.QueryValidatePinUseCase
import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.USER_DATA
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.UIEvent.OnCallMutationSendPinProcess
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.UIEvent.OnGetOtpFromMessage
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.UIEvent.OnInitializeTimer
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.UIEvent.OnOtherPhoneNumberClick
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.UIEvent.OnOtpValueChange
import com.multimoney.multimoney.presentation.ui.login.registereduser.otp.RegisteredUserOtpViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.util.OTP_MESSAGE_REGEX
import com.multimoney.multimoney.presentation.util.ResendOtp
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.format
import com.multimoney.multimoney.presentation.util.getNavParam
import com.multimoney.multimoney.presentation.util.tickerFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit.SECONDS
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile

@HiltViewModel
class RegisteredUserOtpViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mutationSendPinProcessUseCase: MutationSendPinProcessUseCase,
    private val queryValidatePinUseCase: QueryValidatePinUseCase
) : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var previousScreen: String = ""
    private var idBrand: Int = 0
    var userData: UserData? = null
    var linkWhatsapp = ""

    init {
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        userData = savedStateHandle.get<UserData>(USER_DATA)
    }

    private fun onStart(linkWhatsapp: String) {
        this.linkWhatsapp = linkWhatsapp
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
        totalTime: Long = TIMER_DURATION
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
        PHASE_THREE -> R.string.sign_up_otp_expiration_time_phase_three
        PHASE_TWO, PHASE_FOUR -> if (uiState.otpResend == ResendOtp.SMS.option) R.string.sign_up_otp_sms else R.string.sign_up_otp_call
        PHASE_FIVE -> R.string.sign_up_otp_expiration_time_phase_five
        else -> R.string.sign_up_otp_expiration_time_phase_six
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

    private fun isFormValid() = uiState.otp.trim()
        .isNotEmpty() && uiState.otp.trim().length == TOTAL_DIGITS && uiState.phaseCount < PHASE_SIX

    private fun callMutationSendPinProcess() = executeUseCase {
        uiState = uiState.copy(isTimerRunning = false)
        mutationSendPinProcessUseCase.invoke(
            userData?.identification.orEmpty(),
            userData?.firstName.orEmpty(),
            userData?.email.orEmpty(),
            userData?.phoneNumber.orEmpty(),
            SEND_METHOD_PHONE,
            userData?.pkUser.orEmpty(),
            idBrand,
            userData?.email.orEmpty()
        ).collectLatest { result ->
            result.onSuccess { sendPinProcess ->
                uiState = uiState.copy(otpResend = sendPinProcess?.nextType, isLoading = false)
                initializeTimer(totalTime = sendPinProcess?.pinExpirationTime?.toLong() ?: TIMER_DURATION)
                getPhaseAction()
                onExecuteTimer()
            }.onMessage {
                uiState = uiState.copy(
                    isLoading = false,
                    dialogParameters = DialogParameters(
                        titleResource = string.sign_up_email_blocked_dialog_title,
                        descriptionResource = R.string.sign_up_otp_code_user_blocked_for_exceed_the_max_of_attempts,
                        isActive = mutableStateOf(true),
                        positiveResource = string.contact,
                        positiveAction = {
                            onUserBlocked()
                        },
                        negativeResource = string.cancel,
                        negativeAction = {
                            navigateToSignIn()
                        }
                    )
                )
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    dialogParameters = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun onUserBlocked() {
        navigateToSignIn()
        emitBaseEvent(BaseEvent.OnOpenWhatsApp(linkWhatsapp))
    }

    private fun navigateToSignIn() = popAndNavigateTo(
        route = Screen.SignInScreen.route,
        popTo = Screen.SignUpScreen.route
    )

    private fun onOtpValueChange(value: String) {
        uiState = uiState.copy(
            otp = value,
            isOtpFromSms = false,
            otpError = Pair(false, R.string.error_empty)
        )
        uiState = uiState.copy(isFormValid = isFormValid())
    }

    private fun onExecuteTimer() {
        tickerFlow(
            period = TIMER_DELAY.seconds,
            initialDelay = TIMER_DELAY.seconds,
            duration = uiState.remainingTime.toLong(SECONDS).seconds
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

    private fun onBackClick() = navigateBack(
        popTo = when (previousScreen) {
            Screen.RegisteredUserEmailScreen.baseRoute -> Screen.RegisteredUserEmailScreen.route
            else -> Screen.SignUpScreen.route
        },
        isRestart = false
    )

    private fun onContinueClick() {
        executeUseCase {
            queryValidatePinUseCase.invoke(
                idBrand = idBrand,
                appSource = APP_SOURCE,
                pkUser = userData?.pkUser ?: "",
                pinSecurity = uiState.otp,
                telephone = userData?.phoneNumber,
                userCreate = userData?.firstName ?: ""
            ).collectLatest { result ->
                result.onSuccess {
                    popAndNavigateTo(
                        route = Screen.RegisteredUserPassword.baseRoute.plus(
                            getNavParam(USER_DATA, encodeData(userData)).plus(
                                getNavParam(ID_BRAND, idBrand)
                            )
                        ),
                        popTo = Screen.SignUpScreen.route
                    )
                }.onMessage {
                    uiState =
                        uiState.copy(otpError = Pair(true, R.string.sign_up_otp_code_not_valid), isLoading = false)
                }.onFailure {
                    uiState = uiState.copy(
                        isLoading = false,
                        dialogParameters = DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                }.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        }
    }

    private fun onOtherPhoneNumberClick() {
        uiState = uiState.copy(
            isLoading = false,
            dialogParameters = DialogParameters(
                titleResource = string.registered_user_otp_other_phone_number_title,
                descriptionResource = R.string.registered_user_otp_other_phone_number_subtitle,
                isActive = mutableStateOf(true),
                positiveResource = string.contact,
                positiveAction = {
                    emitBaseEvent(BaseEvent.OnOpenWhatsApp(linkWhatsapp))
                }
            )
        )
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
        val isFormValid: Boolean = false,
        val dialogParameters: DialogParameters = DialogParameters(),
        val isLoading: Boolean = false
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnStart -> onStart(event.linkWhatsapp)
            is OnOtpValueChange -> onOtpValueChange(event.value)
            is OnBackClick -> onBackClick()
            is OnContinueClick -> onContinueClick()
            is OnGetOtpFromMessage -> getOtpFromMessage(event.message)
            is OnCallMutationSendPinProcess -> callMutationSendPinProcess()
            is OnInitializeTimer -> initializeTimer(event.phaseCount, event.time)
            is OnOtherPhoneNumberClick -> onOtherPhoneNumberClick()
        }
    }

    sealed class UIEvent {
        data class OnStart(val linkWhatsapp: String) : UIEvent()
        data class OnOtpValueChange(val value: String) : UIEvent()
        data class OnGetOtpFromMessage(val message: String) : UIEvent()
        data class OnInitializeTimer(val phaseCount: Int, val time: Long) : UIEvent()
        object OnCallMutationSendPinProcess : UIEvent()
        object OnBackClick : UIEvent()
        object OnContinueClick : UIEvent()
        object OnOtherPhoneNumberClick : UIEvent()
    }

    sealed class BaseEvent {
        data class OnOpenWhatsApp(val linkWhatsapp: String) : BaseEvent()
    }

    companion object {
        const val PHASE_ONE = 1
        const val PHASE_TWO = 2
        const val PHASE_THREE = 3
        const val PHASE_FOUR = 4
        const val PHASE_FIVE = 5
        const val PHASE_SIX = 6

        const val TOTAL_DIGITS = 4

        const val TIMER_DURATION = 0L
        const val TIMER_DELAY = 1L

        const val SEND_METHOD_PHONE = "PHONE"

        const val APP_SOURCE = 2
    }
}
