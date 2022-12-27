package com.multimoney.multimoney.presentation.ui.login.signin

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.cognitoidentityprovider.util.CognitoJWTParser
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.cognito.options.AWSCognitoAuthSignInOptions
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.security.MutationChangeDeviceUseCase
import com.multimoney.domain.interaction.security.MutationRequestChangeDeviceUseCase
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
import com.multimoney.multimoney.presentation.ui.login.signup.password.SignUpPasswordViewModel
import com.multimoney.multimoney.presentation.util.OTP_MESSAGE_REGEX
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.OTPMessageStatus
import com.multimoney.multimoney.presentation.util.format
import com.multimoney.multimoney.presentation.util.getNavParam
import com.multimoney.multimoney.presentation.util.tickerFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
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
    private val dataStorePreferences: DataStorePreferences,
    private val mutationRequestChangeDeviceUseCase: MutationRequestChangeDeviceUseCase,
    private val mutationChangeDeviceUseCase: MutationChangeDeviceUseCase,
    private val savedStateHandle: SavedStateHandle
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

    private fun onRequestChangeDevice() = executeUseCase {
        mutationRequestChangeDeviceUseCase.invoke(email = email).collectLatest { result ->
            result.onSuccess {
                if (it.status == SUCCESS_STATUS) {

                }
            }.onFailure {

            }.onMessage {

            }.onLoading {

            }
        }
    }

    private fun initializeTimer(
        phaseCount: Int?,
        totalTime: Long = ValidateOTPViewModel.TIMER_DURATION
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
            uiState.remainingTime.plus(ValidateOTPViewModel.TIMER_DURATION.seconds)
        uiState = uiState.copy(
            isTimerRunning = false,
            remainingTime = newRemainingTime,
            remainingTimeText = newRemainingTime.format()
        )
        updateMessageStatus()
    }

    private fun updateMessageStatus() {
        when (uiState.phaseCount) {
            ValidateOTPViewModel.PHASE_ONE -> uiState =
                uiState.copy(messageStatus = OTPMessageStatus.RESEND_OTP)
            ValidateOTPViewModel.PHASE_TWO -> uiState =
                uiState.copy(messageStatus = OTPMessageStatus.RESEND_OTP_AGAIN)
            ValidateOTPViewModel.PHASE_THREE, null ->
                uiState =
                    uiState.copy(messageStatus = OTPMessageStatus.COULD_NOT_VERIFY_ID)
        }
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

    private fun onStart() {

    }

    private fun onCallMutationRequestChangeDevice() = executeUseCase {
        mutationRequestChangeDeviceUseCase.invoke(email).collectLatest { result ->
            result.onSuccess {
                if (it.status == SUCCESS_STATUS) {
                    uiState = uiState.copy(isLoading = false)
                    Log.e("success ", it.toString())
                }
            }.onFailure {
                uiState = uiState.copy(isAlertResultVisible = false, isLoading = false)
                Log.e("failure ", it.toString())
            }.onMessage {
                uiState = uiState.copy(isLoading = false)
                Log.e("message ", it.toString())
            }.onLoading {
                Log.e("loading ", "Loading")
            }
        }
    }


    private fun onCallMutationChangeDevice() = executeUseCase {
        mutationChangeDeviceUseCase.invoke(email, uiState.otp).collectLatest { result ->
            result.onSuccess {

                onNavigateToLogin()
                if (it.status == SUCCESS_STATUS) {
                    uiState = uiState.copy(isLoading = false)
                    Log.e("success ", it.toString())
                }
            }.onFailure {
                onNavigateToLogin()

                uiState = uiState.copy(isAlertResultVisible = true, isLoading = false)
                Log.e("failure ", it.toString())

            }.onMessage {
                uiState = uiState.copy(isLoading = false)
                Log.e("message ", it.toString())
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
                Log.e("loading ", "Loading")
            }
        }
    }

    private fun onNavigateBack() {
        popAndNavigateTo(
            route = Screen.SignInScreen.route,
            popTo = Screen.SignInOTPScreen.route
        )
    }

   

    private fun cognitoError() {
//        uiState = uiState.copy(
//            userEmailError = Pair(true, R.string.error_empty),
//            userPasswordError = Pair(true, R.string.sign_in_validation),
//            isLoading = false
//        )
    }

    private fun onNavigateToLogin() = popAndNavigateTo(
        route = Screen.SignInScreen.baseRoute.plus(getNavParam(FORCE_CHANGE_DEVICE,true)),
        popTo = Screen.SignInOTPScreen.route
    )

    private suspend fun saveUserData(
        session: AWSCognitoAuthSession,
        authUserAttribute: List<AuthUserAttribute>
    ) {
        val payload = CognitoJWTParser.getPayload(session.userPoolTokens.value?.idToken)
        dataStorePreferences.setUserName("${authUserAttribute.firstOrNull { it.key == AuthUserAttributeKey.name() }?.value.orEmpty()} ${authUserAttribute.firstOrNull { it.key == AuthUserAttributeKey.familyName() }?.value.orEmpty()}")
        dataStorePreferences.setAuthToken(session.userPoolTokens.value?.idToken ?: "")
        dataStorePreferences.setIdBrand(payload.getString(SignUpPasswordViewModel.COGNITO_CUSTOM_ID_BRAND))
        dataStorePreferences.setPkUser(payload.getString(SignUpPasswordViewModel.COGNITO_CUSTOM_PK_USER))
        dataStorePreferences.setIdentification(payload.getString(SignUpPasswordViewModel.COGNITO_CUSTOM_IDENTIFICATION))
        dataStorePreferences.setUserEmail(email)
    }


    data class UIState(

        val isLoading: Boolean = true,
        var isAlertResultVisible: Boolean = false,

        // Fields
        val otp: String = "",
        val otpResend: String? = "",
        val otpError: Pair<Boolean, Int> = Pair(false, R.string.sign_up_otp_code_not_valid),
        val isTimerRunning: Boolean = false,
        // Interactions
        val phaseCount: Int? = null,
        val messageStatus: OTPMessageStatus? = null,
        val remainingTime: Duration = ValidateOTPViewModel.TIMER_DURATION.seconds,
        val remainingTimeText: String = remainingTime.format(),
        val isOtpFromSms: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val dialogTextResource: Int = R.string.empty,
        val alertTextResource: Int = R.string.empty,
        val enterTheCodeTextResource: Int = R.string.empty,
        val statusTextResource: Int = R.string.empty,

        )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnStart -> onStart()
            is UIEvent.OnCallMutationRequestChangeDevice -> onCallMutationRequestChangeDevice()
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnValidateOTP -> onCallMutationChangeDevice()
            is UIEvent.OnOTPValueChange -> onOtpValueChange(event.otp)
        }
    }

    sealed class UIEvent {
        object OnStart : UIEvent()
        object OnCallMutationRequestChangeDevice : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnValidateOTP : UIEvent()
        data class OnOTPValueChange(val otp: String) : UIEvent()
    }

    companion object {
        const val SUCCESS_STATUS = 0
        const val WRONG_CODE = "2708"
        const val TOTAL_DIGITS = 6

    }
}
