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

    data class UIState(
        val currentStep: Int = CryptoSendSteps.One.pageNumber,
        val isLoading: Boolean = false,
        val accounts: List<Any> = listOf(),
        val openDialog: DialogParameters = DialogParameters(),
        var asset: String = "",
        var assetDescription: String = "",
        var assetImg: String = "",
        var currencyDollarBalance: Double = 0.0,
        var cryptoCurrencyPrice: Double = 0.0,
        var cryptoNetwork: String = "",
        var destinationAddress: String = ""
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnGetUserInfo -> setUserData()
            is UIEvent.OnCloseClick -> onCloseClick()
            is UIEvent.OnPreviousStep -> previousStep()
            is UIEvent.OnNextStep -> nextStep()
            is UIEvent.OnCryptoSelected -> onCryptoSelected(event.cryptoAccount)
        }
    }

    sealed interface UIEvent {
        object OnGetUserInfo : UIEvent
        object OnCloseClick : UIEvent
        object OnPreviousStep : UIEvent
        object OnNextStep : UIEvent
        data class OnCryptoSelected(val cryptoAccount: BalanceCryptoAccountItems): UIEvent
    }

    companion object {
        private const val DEFAULT_ID_BRAND = -1
        private const val SEND_CRYPTO_TOTAL_STEPS_CR = 4
        private const val SEND_CRYPTO_TOTAL_STEPS_CR_DETAILS = 3
    }
}
