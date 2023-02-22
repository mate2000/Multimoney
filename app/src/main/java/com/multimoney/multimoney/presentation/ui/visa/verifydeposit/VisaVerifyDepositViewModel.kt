package com.multimoney.multimoney.presentation.ui.visa.verifydeposit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.domain.interaction.virtualcard.MutationMicroDepositVDUseCase
import com.multimoney.domain.interaction.virtualcard.MutationResendMicroDepositVDUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CARD
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.BaseEvent.OnOpenWhatsApp
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.UIEvent.OnAlertButtonClick
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.UIEvent.OnAlertCloseClick
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.UIEvent.OnDoNotSeeClick
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.UIEvent.OnMicroDepositValueChange
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.UIEvent.OnResendClick
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.format
import com.multimoney.multimoney.presentation.util.tickerFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit.SECONDS

@HiltViewModel
class VisaVerifyDepositViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mutationResendMicroDepositVDUseCase: MutationResendMicroDepositVDUseCase,
    private val mutationMicroDepositVDUseCase: MutationMicroDepositVDUseCase
) : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var identification: String = ""
    private var idCard: String = ""
    private var user: String = ""
    private var idBrand: Int = 0
    var linkWhatsapp = ""
    private var previousScreen = ""

    init {
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        idCard = savedStateHandle[ID_CARD] ?: ""
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
    }

    private fun onStart(linkWhatsapp: String) {
        this.linkWhatsapp = linkWhatsapp
        if (previousScreen == Screen.ProfileCardListScreen.baseRoute) {
            onResendClick()
        } else {
            initializeTimer()
            onExecuteTimer()
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

    private fun initializeTimer() {
        val newRemainingTime = TIMER_DURATION.seconds
        uiState = uiState.copy(
            isTimerRunning = true,
            remainingTime = newRemainingTime,
            remainingTimeText = newRemainingTime.format(),
            microDeposit = "",
            microDepositError = Pair(false, "")
        )
    }

    private fun onTimerFinish() {
        val newRemainingTime = uiState.remainingTime.plus(TIMER_DURATION.seconds)
        uiState = uiState.copy(
            isTimerRunning = false,
            remainingTime = newRemainingTime,
            remainingTimeText = newRemainingTime.format()
        )
    }

    private fun isFormValid() = uiState.microDeposit.trim()
        .isNotEmpty() && uiState.microDeposit.trim().length == TOTAL_DIGITS

    private fun onDoNotSeeClick() {
        uiState = uiState.copy(
            dialogParameters = DialogParameters(
                titleResource = string.visa_direct_verify_deposit_dialog_do_not_see_title,
                descriptionResource = R.string.visa_direct_verify_deposit_dialog_do_not_see_description,
                isActive = mutableStateOf(true),
                positiveResource = string.understood
            )
        )
    }

    private fun onResendClick() = executeUseCase {
        uiState = uiState.copy(isTimerRunning = false)
        mutationResendMicroDepositVDUseCase.invoke(
            identification = identification,
            idCard = idCard,
            user = user,
            idBrand = idBrand
        ).collectLatest { result ->
            result.onSuccess {
                uiState = uiState.copy(isLoading = false)
                initializeTimer()
                onExecuteTimer()
            }.onMessage {
                uiState = uiState.copy(
                    isLoading = false,
                    dialogParameters = DialogParameters(
                        description = it?.messageError?.message.orEmpty(),
                        isActive = mutableStateOf(true),
                        positiveResource = string.contact,
                        positiveAction = {
                            onContactClick()
                        },
                        negativeResource = string.exit
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

    private fun onContactClick() {
        emitBaseEvent(OnOpenWhatsApp(linkWhatsapp))
    }

    private fun onMicroDepositValueChange(value: String) {
        uiState = uiState.copy(
            microDeposit = value,
            microDepositError = Pair(false, "")
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
        popTo = Screen.VisaVerifyInformationScreen.route,
        isRestart = false
    )

    private fun onNavigateBackHome() = navigateBack(
        popTo = Screen.HomeScreen.route,
        isRestart = false
    )

    private fun onCloseClick() {
        uiState = uiState.copy(
            dialogParameters = DialogParameters(
                titleResource = R.string.visa_verified_dialog_title,
                descriptionResource = R.string.visa_verified_dialog_description,
                positiveResource = R.string.cancel,
                positiveAction = { onNavigateBackHome() },
                negativeResource = R.string.button_continue,
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onContinueClick() = executeUseCase {
        uiState = uiState.copy(isTimerRunning = false)
        mutationMicroDepositVDUseCase.invoke(
            identification = identification,
            idCard = idCard,
            code = uiState.microDeposit,
            user = user,
            idBrand = idBrand
        ).collectLatest { result ->
            result.onSuccess {
                setSuccessAlertResult()
            }.onMessage {
                if (it?.messageError?.status == MICRO_DEPOSIT_ERROR) {
                    uiState = uiState.copy(microDepositError = Pair(false, it.messageError.message.orEmpty()))
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        dialogParameters = DialogParameters(
                            description = it?.messageError?.message.orEmpty(),
                            isActive = mutableStateOf(true),
                            positiveResource = string.contact,
                            positiveAction = {
                                onContactClick()
                            },
                            negativeResource = string.exit
                        )
                    )
                }
            }.onFailure {
                setErrorAlertResult(it.getError())
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun setErrorAlertResult(
        alertResultDescription: String? = ""
    ) {
        uiState = uiState.copy(
            isAlertResultVisible = true,
            isAlertResultSuccess = false,
            alertResultIconResource = R.drawable.ic_error_symbol,
            alertResultTitleResource = R.string.visa_direct_verify_deposit_alert_error_title,
            alertResultDescription = alertResultDescription ?: "",
            alertResultDescriptionResource = if (alertResultDescription.isNullOrEmpty()) {
                R.string.visa_direct_verify_deposit_alert_error_description
            } else {
                R.string.empty
            },
            alertResultButtonResource = R.string.visa_direct_verify_deposit_alert_error_button,
            isLoading = false
        )
    }

    private fun setSuccessAlertResult() {
        uiState = uiState.copy(
            isAlertResultVisible = true,
            isAlertResultSuccess = true,
            alertResultIconResource = R.drawable.ic_success_symbol,
            alertResultTitleResource = R.string.visa_direct_verify_deposit_alert_success_title,
            alertResultDescription = "",
            alertResultDescriptionResource = R.string.empty,
            alertResultButtonResource = if (previousScreen == Screen.PaymentCardsListScreen.baseRoute) {
                R.string.visa_direct_verify_deposit_alert_success_button_from_payment
            } else {
                R.string.visa_direct_verify_deposit_alert_success_button_from_smart
            },
            isLoading = false
        )
    }

    data class UIState(
        // Fields
        val microDeposit: String = "",
        val microDepositError: Pair<Boolean, String> = Pair(false, ""),

        // Interactions
        val remainingTime: Duration = TIMER_DURATION.seconds,
        val isTimerRunning: Boolean = false,
        val remainingTimeText: String = remainingTime.format(),
        val isFormValid: Boolean = false,
        val dialogParameters: DialogParameters = DialogParameters(),
        val isLoading: Boolean = false,

        // Alert Result
        val isAlertResultVisible: Boolean = false,
        val isAlertResultSuccess: Boolean = false,
        val alertResultIconResource: Int = 0,
        val alertResultTitleResource: Int = R.string.empty,
        val alertResultDescription: String = "",
        val alertResultDescriptionResource: Int = R.string.empty,
        val alertResultButtonResource: Int = R.string.empty
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnStart -> onStart(event.linkWhatsapp)
            is OnMicroDepositValueChange -> onMicroDepositValueChange(event.value)
            is OnDoNotSeeClick -> onDoNotSeeClick()
            is OnResendClick -> onResendClick()
            is OnBackClick -> onBackClick()
            is OnCloseClick -> onCloseClick()
            is OnContinueClick -> onContinueClick()
            is OnAlertCloseClick -> onCloseClick()
            is OnAlertButtonClick -> onCloseClick()
        }
    }

    sealed class UIEvent {
        data class OnStart(val linkWhatsapp: String) : UIEvent()
        data class OnMicroDepositValueChange(val value: String) : UIEvent()
        object OnDoNotSeeClick : UIEvent()
        object OnResendClick : UIEvent()
        object OnBackClick : UIEvent()
        object OnCloseClick : UIEvent()
        object OnContinueClick : UIEvent()
        object OnAlertCloseClick : UIEvent()
        object OnAlertButtonClick : UIEvent()
    }

    sealed class BaseEvent {
        data class OnOpenWhatsApp(val linkWhatsapp: String) : BaseEvent()
    }

    companion object {

        const val TOTAL_DIGITS = 3

        const val TIMER_DURATION = 60L
        const val TIMER_DELAY = 1L
        const val MICRO_DEPOSIT_ERROR = 2997
    }
}
