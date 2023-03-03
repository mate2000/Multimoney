package com.multimoney.multimoney.presentation.ui.crypto.market

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.crypto.GetAvailableListOfCryptoCoinsUseCase
import com.multimoney.domain.model.crypto.GetListOfAvailableCryptoCoins
import com.multimoney.domain.model.crypto.MarketCryptoCoin
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.crypto.CryptoProcessErrorCodes
import com.multimoney.multimoney.presentation.util.CryptoHelper
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class MarketScreenViewModel @Inject constructor(
    private val getAvailableListOfCryptoCoinsUseCase: GetAvailableListOfCryptoCoinsUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val cryptoHelper: CryptoHelper
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UiState())
        private set

    private fun onGetUserInfo() {
        uiState = uiState.copy(
            user = savedStateHandle[USER] ?: "",
            idBrand = savedStateHandle[ID_BRAND] ?: 0,
        )
    }

    private fun getAvailableListOfCryptoCoins(
        user: String,
        idBrand: Int
    ) = executeUseCase {
        val cryptoOrigin = cryptoHelper.getCryptoOrigin()
        getAvailableListOfCryptoCoinsUseCase.invoke(user, idBrand, cryptoOrigin)
            .collectLatest { result ->
                result.onSuccess { availableCryptoCoins ->
                    availableCryptoCoins.let {
                        uiState = uiState.copy(
                            isLoading = false,
                            availableCryptoCoins = it
                        )
                    }
                }
                result.onFailure {
                    if (it.errorCode == CryptoProcessErrorCodes.Maintenance.status) {
                        navigateToMaintenance()
                        return@onFailure
                    }
                    onFailure(it)
                }
                result.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
    }

    private fun navigateToMaintenance() {
        navigateTo(Screen.MaintenanceAlertScreen.route)
    }

    private fun onGetAvailableListOfCryptoCoins() {
        getAvailableListOfCryptoCoins(
            user = uiState.user ?: "",
            idBrand = uiState.idBrand ?: 0
        )
    }

    private fun onSetAssetBeforeNavigate(
        marketCryptoCoin: MarketCryptoCoin
    ) {
        uiState = uiState.copy(
            selectedCryptoCoin = marketCryptoCoin
        )
    }

    private fun onNavigateToCurrencyDetails() {
        navigateTo(
            "${Screen.CryptoCurrencyDetailsScreen.baseRoute}/${uiState.user}"
                    + "/${uiState.idBrand}/${encodeData(uiState.selectedCryptoCoin)}"
        )
    }


    private fun onFailure(error: HttpError) {
        uiState = uiState.copy(
            isLoading = false,
            openDialog = DialogParameters(
                description = error.getError() ?: "",
                isActive = mutableStateOf(true)
            )
        )
    }

    data class UiState(
        val user: String? = null,
        val idBrand: Int? = null,
        val isLoading: Boolean = false,
        val selectedCryptoCoin: MarketCryptoCoin? = null,
        val openDialog: DialogParameters = DialogParameters(),
        val availableCryptoCoins: GetListOfAvailableCryptoCoins? = null
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is UIEvent.OnGetUserInfo -> onGetUserInfo()
            is UIEvent.OnGetAvailableListOfCryptoCoins -> onGetAvailableListOfCryptoCoins()
            is UIEvent.OnNavigateToCurrencyDetails -> onNavigateToCurrencyDetails()
            is UIEvent.OnSetAssetBeforeNavigation -> onSetAssetBeforeNavigate(
                event.asset
            )
        }
    }

    sealed class UIEvent {
        object OnGetUserInfo : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnGetAvailableListOfCryptoCoins : UIEvent()
        object OnNavigateToCurrencyDetails : UIEvent()
        data class OnSetAssetBeforeNavigation(
            val asset: MarketCryptoCoin
        ) : UIEvent()
    }
}
