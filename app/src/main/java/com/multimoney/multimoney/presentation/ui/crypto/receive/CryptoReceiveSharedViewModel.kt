package com.multimoney.multimoney.presentation.ui.crypto.receive

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.CryptoReceiveSteps
import com.multimoney.domain.model.crypto.MarketCryptoCoin
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CRYPTO_ASSET
import com.multimoney.multimoney.presentation.navigation.DESCRIPTION_CURRENCY
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.ITEM_CRYPTO_MARKET
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CryptoReceiveSharedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UiState())
        private set

    private var currentFlowStep: Int = CryptoReceiveSteps.One.pageNumber

    var idBrand = DEFAULT_ID_BRAND
    var pkUser = ""
    var identification = ""
    var email = ""
    private var marketCryptoCoin: MarketCryptoCoin? = savedStateHandle[ITEM_CRYPTO_MARKET]
    val comingFromDetails: Boolean = marketCryptoCoin != null

    private fun setUserData() {
        viewModelScope.launch {
            idBrand = dataStorePreferences.getIdBrand().first().toInt()
            pkUser = dataStorePreferences.getPkUser().first()
            identification = dataStorePreferences.getIdentification().first()
            email = dataStorePreferences.getUserEmail().first()
        }
    }

    private fun navigateBackToHome() =
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true,
        )

    private fun previousStep() {
        if (currentFlowStep == CryptoReceiveSteps.One.pageNumber) {
            navigateBackToHome()
        } else {
            currentFlowStep--
            uiState = uiState.copy(
                currentStep = currentFlowStep
            )
        }
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnGetUserInfo -> setUserData()
            is UIEvent.OnPreviousStep -> previousStep()
        }
    }

    data class UiState(
        val currentStep: Int = CryptoReceiveSteps.One.pageNumber,
        var asset: String = "",
        var assetDescription: String = ""
    )

    sealed interface UIEvent {
        object OnGetUserInfo : UIEvent
        object OnPreviousStep : UIEvent
    }

    companion object {
        private const val DEFAULT_ID_BRAND = -1
    }
}