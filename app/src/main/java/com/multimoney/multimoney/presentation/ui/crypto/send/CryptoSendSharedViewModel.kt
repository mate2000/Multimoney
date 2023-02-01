package com.multimoney.multimoney.presentation.ui.crypto.send

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CryptoSendSteps
import com.multimoney.domain.model.balance.BalanceCryptoAccountItems
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CRYPTO_ASSET
import com.multimoney.multimoney.presentation.navigation.DESCRIPTION_CURRENCY
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CryptoSendSharedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataStorePreferences: DataStorePreferences,
) : BaseViewModel(shouldObserveToken = true) {

    var uiState by mutableStateOf(UIState())
        private set

    //stateless
    private var overridePreviousAction: (() -> Unit)? = null
    private var nextStep: Int = CryptoSendSteps.One.id
    private var previousStep: Int = CryptoSendSteps.One.id

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
            if (comingFromCurrencyDetails){
                uiState = uiState.copy(asset = asset ?: "", assetDescription = assetDescription ?: "")
            }
        }
    }

    private fun onCloseClick() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = R.string.smart_close_origination_dialog_title,
                positiveResource = R.string.common_leave,
                negativeResource = R.string.button_continue,
                positiveAction = { navigateBackToHome() },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun navigateBackToHome() =
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true,
        )

    private fun previousStep() {
        if (overridePreviousAction != null) {
            overridePreviousAction?.invoke()
        } else {
            when {
                uiState.currentStep == CryptoSendSteps.Two.id && comingFromCurrencyDetails -> {
                    navigateBackToHome()
                }
                previousStep > CryptoSendSteps.One.id || uiState.currentStep == CryptoSendSteps.Two.id -> {
                    uiState = uiState.copy(
                        currentStep = previousStep
                    )
                }
                else -> {
                    navigateBackToHome()
                }
            }
        }
    }

    private fun nextStep() {
        if (nextStep <= getTotalSteps()) {
            uiState = uiState.copy(
                currentStep = nextStep
            )
        }
    }

    private fun getTotalSteps(): Int {
        return when {
            idBrand == Brand.CostaRica.id -> SEND_CRYPTO_TOTAL_STEPS_CR
            idBrand == Brand.CostaRica.id && comingFromCurrencyDetails -> SEND_CRYPTO_TOTAL_STEPS_CR_DETAILS
            else -> throw IllegalArgumentException("case not supported")
        }
    }

    private fun onSetCurrentStep(step: Int) {
        uiState = uiState.copy(
            currentStep = step
        )
    }

    private fun onSetNavigation(
        overridePreviousAction: (() -> Unit)?,
        nextStep: Int,
        previousStep: Int
    ) {
        this.overridePreviousAction = overridePreviousAction
        this.nextStep = nextStep
        this.previousStep = previousStep
    }

    private fun overridePreviousAction(overridePreviousAction: (() -> Unit)?) {
        this.overridePreviousAction = overridePreviousAction
    }

    private fun onCryptoSelected(cryptoAccount: BalanceCryptoAccountItems) {
        uiState = uiState.copy(
            asset = cryptoAccount.asset,
            assetDescription = cryptoAccount.descriptionCurrency
        )
    }

    data class UIState(
        val currentStep: Int = 1,
        val isLoading: Boolean = false,
        val accounts: List<Any> = listOf(),
        val openDialog: DialogParameters = DialogParameters(),
        var asset: String = "",
        var assetDescription: String = ""
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnGetUserInfo -> setUserData()
            is UIEvent.OnCloseClick -> onCloseClick()
            is UIEvent.OnPreviousStep -> previousStep()
            is UIEvent.OnNextStep -> nextStep()
            is UIEvent.SetCurrentStep -> onSetCurrentStep(event.step)
            is UIEvent.OnSetNavigation -> onSetNavigation(
                event.overridePreviousAction,
                event.nextStep,
                event.previousStep
            )
            is UIEvent.OverridePreviousAction -> overridePreviousAction(event.action)
            is UIEvent.OnCryptoSelected -> onCryptoSelected(event.cryptoAccount)
        }
    }

    sealed interface UIEvent {
        object OnGetUserInfo : UIEvent
        object OnCloseClick : UIEvent
        object OnPreviousStep : UIEvent
        object OnNextStep : UIEvent
        data class SetCurrentStep(val step: Int) : UIEvent
        data class OnSetNavigation(
            val overridePreviousAction: (() -> Unit)? = null,
            val nextStep: Int,
            val previousStep: Int
        ) : UIEvent
        data class OverridePreviousAction(val action: (() -> Unit)?) : UIEvent
        data class OnCryptoSelected(val cryptoAccount: BalanceCryptoAccountItems): UIEvent
        /*
        data class OnCryptoSelected(
            val selectedCrypto: MarketCryptoCoin
        ) : UIEvent
        object OnGetUserInfo : UIEvent*/
    }

    companion object {
        private const val DEFAULT_ID_BRAND = -1
        private const val SEND_CRYPTO_TOTAL_STEPS_CR = 4
        private const val SEND_CRYPTO_TOTAL_STEPS_CR_DETAILS = 3
    }
}