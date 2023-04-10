package com.multimoney.multimoney.presentation.ui.login.signin

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.security.MutationChangeDeviceUseCase
import com.multimoney.domain.interaction.security.MutationRequestChangeDeviceUseCase
import com.multimoney.domain.model.metrics.EmailDto
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.APP_VERSION
import com.multimoney.multimoney.presentation.navigation.DEVICE_BRAND
import com.multimoney.multimoney.presentation.navigation.DEVICE_ID
import com.multimoney.multimoney.presentation.navigation.DEVICE_MODEL
import com.multimoney.multimoney.presentation.navigation.DEVICE_NAME
import com.multimoney.multimoney.presentation.navigation.DEVICE_TYPE
import com.multimoney.multimoney.presentation.navigation.FORCE_CHANGE_DEVICE
import com.multimoney.multimoney.presentation.navigation.IP_ADDRESS
import com.multimoney.multimoney.presentation.navigation.IS_EMULATOR
import com.multimoney.multimoney.presentation.navigation.PASSWORD
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.UNIQUE_ID
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.validateotp.ValidateOTPViewModel
import com.multimoney.multimoney.presentation.ui.login.signin.SignInOTPViewModel.UIEvent.OnGetWhatsAppLink
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel
import com.multimoney.multimoney.presentation.util.OTP_MESSAGE_REGEX
import com.multimoney.multimoney.presentation.util.ResendOtp
import com.multimoney.multimoney.presentation.util.SIM_CODE_EL_SALVADOR
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.OTPMessageStatus
import com.multimoney.multimoney.presentation.util.format
import com.multimoney.multimoney.presentation.util.getNavParam
import com.multimoney.multimoney.presentation.util.getUserCountry
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink
import com.multimoney.multimoney.presentation.util.tickerFlow
import com.multimoney.multimoney.presentation.util.toJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

@HiltViewModel
class SignInOTPViewModel @Inject constructor(
    private val mutationRequestChangeDeviceUseCase: MutationRequestChangeDeviceUseCase,
    private val mutationChangeDeviceUseCase: MutationChangeDeviceUseCase,
    private val dataStorePreferences: DataStorePreferences,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(false) {

    private var email: String = ""
    private var password: String = ""
    private var deviceId = ""
    private var uniqueId = ""
    private var ipAddress = ""
    private var deviceType = ""
    private var deviceName = ""
    private var appVersion = ""
    private var deviceBrand = ""
    private var deviceModel = ""
    private var isEmulator = ""
    var whatsAppLink: String? = ""

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    init {
        email = savedStateHandle[EMAIL] ?: ""
        password = savedStateHandle[PASSWORD] ?: ""
        deviceId = savedStateHandle[DEVICE_ID] ?: ""
        uniqueId = savedStateHandle[UNIQUE_ID] ?: ""
        ipAddress = savedStateHandle[IP_ADDRESS] ?: ""
        deviceType = savedStateHandle[DEVICE_TYPE] ?: ""
        deviceName = savedStateHandle[DEVICE_NAME] ?: ""
        appVersion = savedStateHandle[APP_VERSION] ?: ""
        deviceBrand = savedStateHandle[DEVICE_BRAND] ?: ""
        deviceModel = savedStateHandle[DEVICE_MODEL] ?: ""
        isEmulator = savedStateHandle[IS_EMULATOR] ?: ""
    }

    private fun initializeTimer(
        phaseCount: Int,
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

    fun isFormValid() =
        uiState.otp.trim()
            .isNotEmpty() && uiState.otp.trim().length == TOTAL_DIGITS

    private fun onOtpValueChange(value: String) {
        uiState = uiState.copy(
            otp = value,
            isOtpFromSms = false,
            otpError = Pair(false, R.string.error_empty)
        )
        isFormValid()
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
        val newRemainingTime = uiState.remainingTime.minus(ValidateOTPViewModel.TIMER_DELAY.seconds)
        uiState = uiState.copy(
            remainingTime = newRemainingTime,
            remainingTimeText = newRemainingTime.format()
        )
    }

    private fun onTimerFinish() {
        val newRemainingTime =
            uiState.remainingTime.plus(TIMER_DURATION.seconds)
        uiState = uiState.copy(
            isTimerRunning = false,
            remainingTime = newRemainingTime,
            remainingTimeText = newRemainingTime.format(),
            phaseCount = uiState.phaseCount.plus(1)
        )
        if (uiState.phaseCount > PHASE_FIVE) {
            onShowBlockedDialog()
        }
    }

    private fun onShowBlockedDialog() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = R.string.sign_in_verify_otp_blocked_title,
                descriptionResource = uiState.dialogTextResource,
                isActive = mutableStateOf(true),
                positiveResource = R.string.contact,
                negativeResource = R.string.cancel
            ),
            isButtonEnabled = false
        )
    }

    private fun resend() {
        if (uiState.otpResend == ResendOtp.SMS.option) {
            registerAdjustEvent(
                adjustEventType = AdjustEventType.SECURITY_LOGIN_RESEND_OTP_CHANGE_DEVICE_9004,
                isLoggedIn = false,
                data = EmailDto(email).toJson(),
                applyAdjust = false
            )
        } else {
            registerAdjustEvent(
                adjustEventType = AdjustEventType.SECURITY_LOGIN_OTP_BY_CALL_CHANGE_DEVICE_9005,
                isLoggedIn = false,
                data = EmailDto(email).toJson(),
                applyAdjust = false
            )
        }
        uiState = uiState.copy(
            isTimerRunning = true,
            phaseCount = uiState.phaseCount.plus(1)
        )
    }

    private fun getPhaseAction() {
        when (uiState.phaseCount) {
            SignUpOtpViewModel.PHASE_TWO, SignUpOtpViewModel.PHASE_FOUR -> resend()
        }
    }

    fun getPhaseResourceString() = when (uiState.phaseCount) {
        PHASE_ONE -> R.string.sign_in_otp_expiration_time_phase_one
        PHASE_THREE -> R.string.sign_in_otp_expiration_time_phase_three
        PHASE_TWO, PHASE_FOUR -> R.string.profile_otp_resend
        PHASE_FIVE -> R.string.sign_in_otp_expiration_time_phase_three
        else -> R.string.profile_couldnt_verify_identity
    }

    private fun onExecuteTimer() {
        tickerFlow(
            period = ValidateOTPViewModel.TIMER_DELAY.seconds,
            initialDelay = ValidateOTPViewModel.TIMER_DELAY.seconds,
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

    private fun onCallMutationRequestChangeDevice() = executeUseCase {
        mutationRequestChangeDeviceUseCase.invoke(email).collectLatest { result ->
            result.onSuccess {
                initializeTimer(
                    if (uiState.phaseCount == PHASE_ONE) PHASE_ONE else uiState.phaseCount.plus(1),
                    totalTime = it.otpTime?.toLong() ?: DEFAULT_OTP_DURATION
                )
                getPhaseAction()
                onExecuteTimer()
                uiState = uiState.copy(
                    isLoading = false,
                    phoneNumber = it.phoneNumber ?: ""
                )
            }.onFailure {
                uiState = uiState.copy(isAlertResultVisible = true, isLoading = false)
            }.onMessage {
                uiState = uiState.copy(isAlertResultVisible = true, isLoading = false)
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun onCallMutationChangeDevice() = executeUseCase {
        mutationChangeDeviceUseCase.invoke(email, uiState.otp).collectLatest { result ->
            result.onSuccess {
                uiState = uiState.copy(isLoading = false)
                when (it.status) {
                    SUCCESS_STATUS -> {
                        registerAdjustEvent(
                            AdjustEventType.SECURITY_LOGIN_SUCCESS_CHANGE_DEVICE_9003,
                            isLoggedIn = false,
                            applyAdjust = false,
                            data = EmailDto(email).toJson()
                        )
                        onNavigateToLogin()
                    }
                    WRONG_CODE -> {
                        uiState =
                            uiState.copy(otpError = Pair(true, R.string.sign_in_otp_wrong_code))
                    }
                    EXPIRED_CODE -> {
                        uiState =
                            uiState.copy(otpError = Pair(true, R.string.sign_in_otp_expired_code))
                    }
                }
            }.onFailure {
                uiState = uiState.copy(isAlertResultVisible = true, isLoading = false)
            }.onMessage {
                uiState = uiState.copy(isAlertResultVisible = true, isLoading = false)
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun onNavigateBack() {
        popAndNavigateTo(
            route = Screen.SignInScreen.route,
            popTo = Screen.SignInOTPScreen.route
        )
    }

    private fun onNavigateToLogin() = popAndNavigateTo(
        route = Screen.SignInScreen.baseRoute.plus(getNavParam(FORCE_CHANGE_DEVICE, true)),
        popTo = Screen.SignInOTPScreen.route
    )

    private fun onResendOTP() {
        onCallMutationRequestChangeDevice()
    }

    private fun openWhatsAppLink(context: Context) {
        context.openWhatsAppDeepLink(whatsAppLink ?: "")
    }

    private fun onSetupResources(context: Context) {
        uiState = when (context.getUserCountry()) {
            SIM_CODE_EL_SALVADOR -> {
                uiState.copy(
                    weSentYouACodeTextResource = R.string.sign_in_we_sent_you_a_code_template_gt,
                    dialogTextResource = R.string.sign_in_verify_otp_blocked_subtitle
                )
            }
            else -> {
                uiState.copy(
                    weSentYouACodeTextResource = R.string.sign_in_we_sent_you_a_code_template,
                    dialogTextResource = R.string.sign_in_verify_otp_blocked_subtitle_cr
                )
            }
        }
    }

    private fun onGetWhatsAppLink() {
        viewModelScope.launch {
            whatsAppLink = dataStorePreferences.getWhatsAppLink().first()
        }
    }

    data class UIState(

        val isLoading: Boolean = true,
        var isAlertResultVisible: Boolean = false,
        val isButtonEnabled: Boolean = true,

        // Fields
        val otp: String = "",
        val otpResend: String? = "",
        val otpError: Pair<Boolean, Int> = Pair(false, R.string.sign_up_otp_code_not_valid),
        val isTimerRunning: Boolean = false,
        // Interactions
        val phaseCount: Int = PHASE_ONE,
        val messageStatus: OTPMessageStatus? = null,
        val remainingTime: Duration = ValidateOTPViewModel.TIMER_DURATION.seconds,
        val remainingTimeText: String = remainingTime.format(),
        val isOtpFromSms: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val dialogTextResource: Int = R.string.empty,
        val alertTextResource: Int = R.string.empty,
        val weSentYouACodeTextResource: Int = R.string.empty,
        val statusTextResource: Int = R.string.empty,
        val phoneNumber: String = ""
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnCallMutationRequestChangeDevice -> onCallMutationRequestChangeDevice()
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnValidateOTP -> onCallMutationChangeDevice()
            is UIEvent.OnOTPValueChange -> onOtpValueChange(uiEvent.otp)
            is UIEvent.OnResendOTP -> onResendOTP()
            is UIEvent.OnGetOtpFromMessage -> getOtpFromMessage(uiEvent.message)
            is UIEvent.OnOpenWhatsappLink -> openWhatsAppLink(
                uiEvent.context
            )
            is UIEvent.OnShowBlockedDialog -> onShowBlockedDialog()
            is UIEvent.OnSetupResources -> onSetupResources(uiEvent.context)
            is OnGetWhatsAppLink -> onGetWhatsAppLink()
        }
    }

    sealed class UIEvent {
        object OnCallMutationRequestChangeDevice : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnValidateOTP : UIEvent()
        object OnShowBlockedDialog : UIEvent()
        data class OnOTPValueChange(val otp: String) : UIEvent()
        data class OnGetOtpFromMessage(val message: String) : UIEvent()
        object OnResendOTP : UIEvent()
        data class OnOpenWhatsappLink(
            val context: Context
        ) : UIEvent()

        data class OnSetupResources(
            val context: Context
        ) : UIEvent()

        object OnGetWhatsAppLink : UIEvent()
    }

    companion object {
        private const val DEFAULT_OTP_DURATION = 300L

        const val SUCCESS_STATUS = 0
        const val WRONG_CODE = 2887
        const val EXPIRED_CODE = 2886
        const val TOTAL_DIGITS = 6
        const val TIMER_DURATION = 1L
        const val PHASE_ONE = 1
        const val PHASE_TWO = 2
        const val PHASE_THREE = 3
        const val PHASE_FOUR = 4
        const val PHASE_FIVE = 5
        const val PHASE_SIX = 6
        const val FOUR_DIGITS = 4
    }
}
