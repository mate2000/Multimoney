package com.multimoney.multimoney.presentation.ui.login.signin.otp

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
import com.multimoney.multimoney.presentation.navigation.IP_ADDRESS
import com.multimoney.multimoney.presentation.navigation.IS_EMULATOR
import com.multimoney.multimoney.presentation.navigation.PASSWORD
import com.multimoney.multimoney.presentation.navigation.UNIQUE_ID
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.validateotp.ValidateOTPViewModel
import com.multimoney.multimoney.presentation.ui.login.signin.otp.SignInOTPViewModel.BaseEvent.ShowSignInScreen
import com.multimoney.multimoney.presentation.ui.login.signin.otp.SignInOTPViewModel.UIEvent.OnCallMutationRequestChangeDevice
import com.multimoney.multimoney.presentation.ui.login.signin.otp.SignInOTPViewModel.UIEvent.OnGetOtpFromMessage
import com.multimoney.multimoney.presentation.ui.login.signin.otp.SignInOTPViewModel.UIEvent.OnGetWhatsAppLink
import com.multimoney.multimoney.presentation.ui.login.signin.otp.SignInOTPViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.login.signin.otp.SignInOTPViewModel.UIEvent.OnOTPValueChange
import com.multimoney.multimoney.presentation.ui.login.signin.otp.SignInOTPViewModel.UIEvent.OnOpenWhatsappLink
import com.multimoney.multimoney.presentation.ui.login.signin.otp.SignInOTPViewModel.UIEvent.OnResendOTP
import com.multimoney.multimoney.presentation.ui.login.signin.otp.SignInOTPViewModel.UIEvent.OnSetupResources
import com.multimoney.multimoney.presentation.ui.login.signin.otp.SignInOTPViewModel.UIEvent.OnShowBlockedDialog
import com.multimoney.multimoney.presentation.ui.login.signin.otp.SignInOTPViewModel.UIEvent.OnValidateOTP
import com.multimoney.multimoney.presentation.ui.login.signin.otp.SignInOTPViewModel.UIEvent.OnSetArguments
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel
import com.multimoney.multimoney.presentation.util.OTP_MESSAGE_REGEX
import com.multimoney.multimoney.presentation.util.ResendOtp
import com.multimoney.multimoney.presentation.util.SIM_CODE_EL_SALVADOR
import com.multimoney.multimoney.presentation.util.SIM_CODE_GUATEMALA
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.OTPMessageStatus
import com.multimoney.multimoney.presentation.util.format
import com.multimoney.multimoney.presentation.util.getUserCountry
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink
import com.multimoney.multimoney.presentation.util.tickerFlow
import com.multimoney.multimoney.presentation.util.toJson
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

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

    private fun onSetArguments(
        email: String?,
        password: String?,
        deviceId: String?,
        uniqueId: String?,
        ipAddress: String?,
        deviceType: String?,
        deviceName: String?,
        appVersion: String?,
        deviceBrand: String?,
        deviceModel: String?,
        isEmulator: String?
    ) {
        this.email = email ?: ""
        this.password = password ?: ""
        this.deviceId = deviceId ?: ""
        this.uniqueId = uniqueId ?: ""
        this.ipAddress = ipAddress ?: ""
        this.deviceType = deviceType ?: ""
        this.deviceName = deviceName ?: ""
        this.appVersion = appVersion ?: ""
        this.deviceBrand = deviceBrand ?: ""
        this.deviceModel = deviceModel ?: ""
        this.isEmulator = isEmulator ?: ""
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

    private fun onCallMutationChangeDevice(onSuccess: () -> Unit) = executeUseCase {
        mutationChangeDeviceUseCase.invoke(email, uiState.otp).collectLatest { result ->
            result.onSuccess {
                when (it.status) {
                    SUCCESS_STATUS -> {
                        registerAdjustEvent(
                            AdjustEventType.SECURITY_LOGIN_SUCCESS_CHANGE_DEVICE_9003,
                            isLoggedIn = false,
                            applyAdjust = false,
                            data = EmailDto(email).toJson()
                        )
                        onSuccess.invoke()
                    }
                    WRONG_CODE -> {
                        uiState =
                            uiState.copy(
                                otpError = Pair(true, R.string.sign_in_otp_wrong_code),
                                isLoading = false
                            )
                    }
                    EXPIRED_CODE -> {
                        uiState =
                            uiState.copy(
                                otpError = Pair(true, R.string.sign_in_otp_expired_code),
                                isLoading = false
                            )
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
        emitBaseEvent(ShowSignInScreen)
    }

    private fun onResendOTP() {
        onCallMutationRequestChangeDevice()
    }

    private fun openWhatsAppLink(context: Context) {
        context.openWhatsAppDeepLink(whatsAppLink ?: "")
    }

    private fun onSetupResources(context: Context) {
        uiState = when (context.getUserCountry()) {
            SIM_CODE_EL_SALVADOR, SIM_CODE_GUATEMALA -> {
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
            is OnSetArguments -> onSetArguments(
                uiEvent.email,
                uiEvent.password,
                uiEvent.deviceId,
                uiEvent.uniqueId,
                uiEvent.ipAddress,
                uiEvent.deviceType,
                uiEvent.deviceName,
                uiEvent.appVersion,
                uiEvent.deviceBrand,
                uiEvent.deviceModel,
                uiEvent.isEmulator
            )
            is OnCallMutationRequestChangeDevice -> onCallMutationRequestChangeDevice()
            is OnNavigateBack -> onNavigateBack()
            is OnValidateOTP -> onCallMutationChangeDevice(uiEvent.onSuccess)
            is OnOTPValueChange -> onOtpValueChange(uiEvent.otp)
            is OnResendOTP -> onResendOTP()
            is OnGetOtpFromMessage -> getOtpFromMessage(uiEvent.message)
            is OnOpenWhatsappLink -> openWhatsAppLink(
                uiEvent.context
            )
            is OnShowBlockedDialog -> onShowBlockedDialog()
            is OnSetupResources -> onSetupResources(uiEvent.context)
            is OnGetWhatsAppLink -> onGetWhatsAppLink()
        }
    }

    sealed class UIEvent {
        data class OnSetArguments(
            val email: String?,
            val password: String?,
            val deviceId: String?,
            val uniqueId: String?,
            val ipAddress: String?,
            val deviceType: String?,
            val deviceName: String?,
            val appVersion: String?,
            val deviceBrand: String?,
            val deviceModel: String?,
            val isEmulator: String?
        ) : UIEvent()

        object OnCallMutationRequestChangeDevice : UIEvent()
        object OnNavigateBack : UIEvent()
        data class OnValidateOTP(val onSuccess: () -> Unit) : UIEvent()
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

    sealed class BaseEvent {
        object ShowSignInScreen : BaseEvent()
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