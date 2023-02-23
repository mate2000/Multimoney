package com.multimoney.multimoney.presentation.ui.crypto.send

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.CryptoSendSteps
import com.multimoney.data.util.catalog.SendCryptoStep
import com.multimoney.domain.model.balance.BalanceCryptoAccountItems
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CRYPTO_ASSET
import com.multimoney.multimoney.presentation.navigation.DESCRIPTION_CURRENCY
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrentDate
import com.multimoney.multimoney.presentation.util.getCurrentTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CryptoSendSharedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataStorePreferences: DataStorePreferences,
) : BaseViewModel(shouldObserveToken = true) {

    var uiState by mutableStateOf(UIState())
        private set

    //stateless
    private var currentFlowStep: Int = CryptoSendSteps.One.pageNumber

    //bundle parameters
    var idBrand = DEFAULT_ID_BRAND
    var pkUser = ""
    var identification = ""
    var email = ""
    var asset: String? = savedStateHandle[CRYPTO_ASSET]
    var assetDescription: String? = savedStateHandle[DESCRIPTION_CURRENCY] ?: ""
    val comingFromCurrencyDetails: Boolean = asset != null

    private fun setUserData() {
        viewModelScope.launch {
            idBrand = dataStorePreferences.getIdBrand().first().toInt()
            pkUser = dataStorePreferences.getPkUser().first()
            identification = dataStorePreferences.getIdentification().first()
            email = dataStorePreferences.getUserEmail().first()
            if (comingFromCurrencyDetails) {
                uiState =
                    uiState.copy(asset = asset ?: "", assetDescription = assetDescription ?: "")
            }
        }
    }

    private fun onCloseClick() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = R.string.crypto_send_abandon_dialog_title,
                descriptionResource = R.string.crypto_send_abandon_dialog_message,
                positiveResource = R.string.custom_dialog_default_negative_label,
                negativeResource = R.string.button_continue,
                negativeAction = { navigateBackToHome() },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun navigateBackToHome() =
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true,
            homeState = HomeState.COLLAPSED
        )

    private fun previousStep() {
        if (currentFlowStep == CryptoSendSteps.One.pageNumber) {
            navigateBackToHome()
        } else {
            currentFlowStep--
            uiState = uiState.copy(
                currentStep = currentFlowStep
            )
        }
    }

    private fun nextStep() {
        currentFlowStep++
        uiState = uiState.copy(
            currentStep = currentFlowStep
        )
    }

    private fun onCryptoSelected(cryptoAccount: BalanceCryptoAccountItems) {
        uiState = uiState.copy(
            asset = cryptoAccount.asset,
            assetDescription = cryptoAccount.descriptionCurrency,
            assetImg = cryptoAccount.url_image,
            currencyDollarBalance = cryptoAccount.balanceDollars,
            cryptoCurrencyPrice = cryptoAccount.priceOfTheDay,
            cryptoNetwork = cryptoAccount.cryptoNetwork
        )
    }

    fun shouldShowCloseButton(): Boolean = if (comingFromCurrencyDetails) {
        uiState.currentStepType != SendCryptoStep.CRYPTO_ADDRESS && uiState.currentStepType != SendCryptoStep.LOADING
    } else {
        uiState.currentStepType != SendCryptoStep.LIST_CRYPTO_CURRENCIES && uiState.currentStepType != SendCryptoStep.LOADING
    }

    private fun onShowMaintenanceAlert() {
        emitBaseEvent(BaseEvent.OnShowMaintenance)
    }

    data class UIState(
        val currentStepType: SendCryptoStep = SendCryptoStep.LIST_CRYPTO_CURRENCIES,
        val currentStep: Int = CryptoSendSteps.One.pageNumber,
        val isLoading: Boolean = false,
        val isPaxosInMaintenance: Boolean = false,
        val accounts: List<Any> = listOf(),
        var asset: String = "",
        var assetDescription: String = "",
        var assetImg: String = "",
        var currencyDollarBalance: Double = 0.0,
        var cryptoCurrencyPrice: Double = 0.0,
        var cryptoNetwork: String = "",
        var destinationAddress: String = "",
        var sendDollarAmount: String = "",
        var transferFee: String = "",
        var referenceNumber: String = "",
        var sendCryptoAmount: String = "",
        val sendCurrentDate: String? = null,
        val sendCurrentTime: String? = null,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnGetUserInfo -> setUserData()
            is UIEvent.OnPreviousStep -> previousStep()
            is UIEvent.OnNextStep -> nextStep()
            is UIEvent.OnCryptoSelected -> onCryptoSelected(event.cryptoAccount)
            is UIEvent.OnSetupVoucherDetails -> uiState = uiState.copy(
                sendDollarAmount = event.sendDollarAmount,
                sendCryptoAmount = event.sendCryptoAmount,
                transferFee = event.transferFee,
                referenceNumber = event.referenceNumber,
                sendCurrentDate = getCurrentDate(Calendar.getInstance().time),
                sendCurrentTime = getCurrentTime(Calendar.getInstance().time)
            )
            is UIEvent.OnSetFlowStep -> uiState = uiState.copy(currentStepType = event.step)
            is UIEvent.OnNavigateHome -> navigateBack(
                popTo = Screen.HomeScreen.route,
                isRestart = true,
                homeState = HomeState.COLLAPSED
            )
            is UIEvent.OnCloseClick -> onCloseClick()
            is BaseEvent.OnShowMaintenance -> onShowMaintenanceAlert()
            is UIEvent.SetPaxosMaintenanceState -> uiState = uiState.copy(isPaxosInMaintenance = event.isPaxosInMaintenance)
        }
    }

    sealed interface BaseEvent {
        object OnShowMaintenance : UIEvent
    }

    sealed interface UIEvent {
        object OnGetUserInfo : UIEvent
        object OnPreviousStep : UIEvent
        object OnNextStep : UIEvent
        data class OnCryptoSelected(val cryptoAccount: BalanceCryptoAccountItems) : UIEvent
        data class OnSetupVoucherDetails(
            val sendDollarAmount: String,
            val sendCryptoAmount: String,
            val transferFee: String,
            val referenceNumber: String
        ) : UIEvent

        data class OnSetFlowStep(val step: SendCryptoStep) : UIEvent
        object OnNavigateHome : UIEvent
        object OnCloseClick : UIEvent
        data class SetPaxosMaintenanceState(val isPaxosInMaintenance: Boolean) : UIEvent
    }

    companion object {
        private const val DEFAULT_ID_BRAND = -1
    }
}
