package com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.validateotp

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.FieldToChange
import com.multimoney.domain.interaction.security.MutationChangeEmailUseCase
import com.multimoney.domain.interaction.security.MutationChangePhoneUseCase
import com.multimoney.domain.interaction.security.MutationSendPinProcessUseCase
import com.multimoney.domain.interaction.security.MutationValidateOTPUseCase
import com.multimoney.domain.model.security.ChangeEmail
import com.multimoney.domain.model.security.ChangePhone
import com.multimoney.domain.model.security.SendPinProcess
import com.multimoney.domain.model.security.ValidateOTP
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CHANGING_FIELD
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.NEW_VALUE
import com.multimoney.multimoney.presentation.navigation.PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.SEND_METHOD
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.OTP_MESSAGE_REGEX
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.OTPMessageStatus
import com.multimoney.multimoney.presentation.util.format
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink
import com.multimoney.multimoney.presentation.util.tickerFlow
import com.multimoney.multimoney.util.CognitoHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import timber.log.Timber
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
    private val mutationValidateOTPUseCase: MutationValidateOTPUseCase,
    private val mutationChangePhoneUseCase: MutationChangePhoneUseCase,
    private val mutationChangeEmailUseCase: MutationChangeEmailUseCase,
    private val cognitoHelper: CognitoHelper,
    private val countDownTimer: MMCountDownTimer,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    val onCallMutationSendPinProcessEvent = MutableSharedFlow<MultimoneyResult<SendPinProcess?>>()
    var linkWhatsapp = ""
    var userBlockedForMaxAttend = ""

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    init {
        uiState = uiState.copy(
            idBrand = savedStateHandle[ID_BRAND],
            identification = savedStateHandle[IDENTIFICATION],
            email = savedStateHandle[EMAIL],
            phoneNumber = savedStateHandle[PHONE_NUMBER],
            pkUser = savedStateHandle[PK_USER],
            firstName = savedStateHandle[FIRST_NAME],
            userName = savedStateHandle[USER],
            sendMethod = savedStateHandle[SEND_METHOD],
            changingField = savedStateHandle[CHANGING_FIELD],
            newValue = savedStateHandle[NEW_VALUE],
            idClient = savedStateHandle[ID_CLIENT]
        )
        getTextResources()
    }

    private fun getTextResources() {
        uiState = uiState.copy(
            dialogTextResource = when (uiState.idBrand) {
                Brand.Guatemala.id ->  if (uiState.changingField == FieldToChange.PHONE.value) R.string.profile_otp_code_user_blocked_for_exceed_the_max_of_attend_phone_gt else  R.string.profile_otp_code_user_blocked_for_exceed_the_max_of_attend_email_gt
                else -> if (uiState.changingField == FieldToChange.PHONE.value) R.string.profile_otp_code_user_blocked_for_exceed_the_max_of_attend_phone else R.string.profile_otp_code_user_blocked_for_exceed_the_max_of_attend_email
            },
            alertTextResource = when(uiState.idBrand){
                Brand.Guatemala.id ->  R.string.profile_error_changing_phone_gt
                else -> R.string.profile_error_changing_phone
            },
            destination = if (uiState.sendMethod == SignUpOtpViewModel.SEND_METHOD_PHONE) uiState.phoneNumber else uiState.email,

            enterTheCodeTextResource = when(uiState.idBrand){
                Brand.Guatemala.id ->   R.string.profile_enter_the_code_sent_to_template_gt
                else ->  R.string.profile_enter_the_code_sent_to_template
            },
            statusTextResource = when (uiState.phaseCount){
                PHASE_ONE ->  R.string.profile_code_expires_in_template
                null ->  R.string.empty
                else -> R.string.profile_code_resend_expires_in_template
            }
        )
    }

    private fun initializeTimer(
        phaseCount: Int?,
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

    fun isFormValid() =
        uiState.otp.trim()
            .isNotEmpty() && uiState.otp.trim().length == TOTAL_DIGITS

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
            remainingTime = newRemainingTime, remainingTimeText = newRemainingTime.format()
        )
    }

    private fun onTimerFinish() {
        val newRemainingTime = uiState.remainingTime.plus(TIMER_DURATION.seconds)
        uiState = uiState.copy(
            isTimerRunning = false,
            remainingTime = newRemainingTime,
            remainingTimeText = newRemainingTime.format(),
        )
        updateMessageStatus()
    }

    private fun updateMessageStatus() {
        when (uiState.phaseCount) {
            PHASE_ONE -> uiState = uiState.copy(messageStatus = OTPMessageStatus.RESEND_OTP)
            PHASE_TWO -> uiState = uiState.copy(messageStatus = OTPMessageStatus.RESEND_OTP_AGAIN)
            PHASE_THREE, null -> uiState = uiState.copy(messageStatus = OTPMessageStatus.COULD_NOT_VERIFY_ID)
        }
    }

    private fun onExecuteTimer() {
        tickerFlow(
            period = TIMER_DELAY.seconds,
            initialDelay = TIMER_DELAY.seconds,
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
            totalTime = pinProcess?.pinExpirationTime?.toLong()
                ?: TIMER_DURATION,
            phaseCount = pinProcess?.numberOfPinForwards?.toInt() ?: 1
        )
        getTextResources()
        onExecuteTimer()
    }

    private fun onStart(linkWhatsapp: String, userBlockedForMaxAttend: String) {
        this.linkWhatsapp = linkWhatsapp
        this.userBlockedForMaxAttend = userBlockedForMaxAttend
    }

    private fun onLogout() {
        Timber.d("Closing session")
        Log.e("TAG","closing session")
        cognitoHelper.signOut(signOutError = {
                Timber.d("SignOut Error")
            })
            viewModelScope.launch {
                dataStorePreferences.setAuthToken("")
            }
            countDownTimer.discardTimer()
            popAndNavigateTo(
                Screen.SignInScreen.route,
                Screen.HomeScreen.route
            )
    }

    private fun openWhatsAppLink(context: Context, whatsAppLink: String) {
        context.openWhatsAppDeepLink(whatsAppLink)
        onNavigateBack()
    }

    private fun onValidateOTP(email: String?, otp: String) = executeUseCase {
        mutationValidateOTPUseCase.invoke(email.toString(), otp).collectLatest {
            processValidateOTPResult(it)
        }
    }

    private fun onChangePhone(identification: String, phone: String, pkUser: String, idBrand: Int) =
        executeUseCase {
            mutationChangePhoneUseCase.invoke(identification, phone, pkUser, idBrand)
                .collectLatest {
                    processChangePhoneResult(it)
                }
        }

    private fun onChangeEmail(
        idClient : Int,pkUser: Int, identification: String, email: String, registerId: Int,
        changeUser: Boolean, user: String, idBrand: Int
    ) =
        executeUseCase {
            mutationChangeEmailUseCase.invoke(
                idClient,
                pkUser,
                identification,
                email,
                registerId,
                changeUser,
                user,
                idBrand
            )
                .collectLatest {
                    processChangeEmailResult(it)
                }
        }

    private fun processChangePhoneResult(result: MultimoneyResult<ChangePhone>) {
        result.onSuccess {
            uiState = uiState.copy(isLoading = false)
            viewModelScope.launch {
                dataStorePreferences.setUserPhoneNumber(uiState.newValue ?: "")
                navigateBack(Screen.HomeScreen.route, isRestart = true)
                emitBaseEvent(HomeViewModel.BaseEvent.OnPhoneNumberChangedToastEvent)
            }
        }
            .onMessage { uiState = uiState.copy(isLoading = false) }
            .onFailure { uiState = uiState.copy(isLoading = false, isAlertResultVisible = true) }
            .onLoading { uiState = uiState.copy(isLoading = true) }
    }

    private fun processChangeEmailResult(result: MultimoneyResult<ChangeEmail>) {
        result.onSuccess {
            uiState = uiState.copy(isLoading = false)
            viewModelScope.launch {
                dataStorePreferences.setUserEmail(uiState.newValue ?: "")
                navigateBack(Screen.HomeScreen.route, isRestart = true)
                emitBaseEvent(HomeViewModel.BaseEvent.OnEmailChangedToastEvent)
            }
        }
            .onMessage { uiState = uiState.copy(isLoading = false) }
            .onFailure { uiState = uiState.copy(isLoading = false, isAlertResultVisible = true) }
            .onLoading { uiState = uiState.copy(isLoading = true) }
    }

    private fun processValidateOTPResult(result: MultimoneyResult<ValidateOTP?>) {
        result.onSuccess {
            when (uiState.changingField) {
                FieldToChange.PHONE.value -> {
                    onChangePhone(
                        uiState.identification.toString(),
                        uiState.newValue.toString(),
                        uiState.pkUser ?: "",
                        uiState.idBrand ?: 0
                    )
                }
                else -> {
                    onChangeEmail(
                        uiState.idClient?.toInt() ?: 0,
                        uiState.pkUser?.toInt() ?: 0,
                        uiState.identification.toString(),
                        uiState.newValue.toString(),
                        0,
                        false,
                        uiState.userName ?: "",
                        uiState.idBrand ?: 0
                    )
                }
            }

        }.onMessage {
            uiState = uiState.copy(
                isLoading = false,
                otpError = Pair(true, R.string.profile_error_phone_code_not_valid)
            )
        }
            .onFailure {
                uiState = uiState.copy(isLoading = false, isAlertResultVisible = true)
            }
            .onLoading { uiState = uiState.copy(isLoading = true) }
    }

    data class UIState(
        val idBrand: Int? = null,
        val identification: String? = null,
        val idClient : Int? = null,
        val email: String? = null,
        val pkUser: String? = null,
        val userName: String? = null,
        val phoneNumber: String? = null,
        val firstName: String? = null,
        val newValue: String? = null,
        val sendMethod: String? = null,
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
        val remainingTime: Duration = TIMER_DURATION.seconds,
        val remainingTimeText: String = remainingTime.format(),
        val isOtpFromSms: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val changingField: String? = null,
        val dialogTextResource: Int = R.string.empty,
        val alertTextResource: Int = R.string.empty,
        val enterTheCodeTextResource: Int = R.string.empty,
        val statusTextResource: Int = R.string.empty,
        val destination :String? = null

        )
    private fun onNavigateBack(){
        navigateBack(Screen.HomeScreen.route, isRestart = true)
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnStart -> onStart(event.linkWhatsapp, event.userBlockedForMaxAttends)
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnNavigateTLogOut -> onLogout()
            is UIEvent.OnOpenWhatsappLink -> openWhatsAppLink(event.context,event.whatsAppLink)
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
            is UIEvent.OnLoadingValueChange -> uiState = uiState.copy(isLoading = event.isLoading)
            is UIEvent.OnFailureWithDialog -> uiState =
                uiState.copy(
                    isLoading = event.isLoading,
                    openDialog = event.openDialog,
                    messageStatus = OTPMessageStatus.COULD_NOT_VERIFY_ID
                )
            is UIEvent.OnContinueButtonClicked -> onValidateOTP(uiState.email, uiState.otp)

        }
    }

    sealed class UIEvent {
        data class OnStart(
            val linkWhatsapp: String,
            val userBlockedForMaxAttends: String,
        ) : UIEvent()

        object OnNavigateTLogOut : UIEvent()
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
        data class OnOpenWhatsappLink(val context : Context, val whatsAppLink: String) : UIEvent()
        data class OnOtpValueChange(val value: String) : UIEvent()
        object OnValidateForm : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnContinueButtonClicked : UIEvent()

    }

    companion object {
        const val PHASE_ONE = 1
        const val PHASE_TWO = 2
        const val PHASE_THREE = 3
        const val TOTAL_DIGITS = 4
        const val TIMER_DURATION = 0L
        const val TIMER_DELAY = 1L
    }
}
