package com.multimoney.multimoney.presentation.ui.crypto.receive.listofcurrencies

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.domain.interaction.crypto.GetAvailableListOfCryptoCoinsUseCase
import com.multimoney.domain.model.balance.BalanceCryptoAccountItems
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
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CryptoReceiveCurrenciesListViewModel @Inject constructor(
    private val getAvailableListOfCryptoCoinsUseCase: GetAvailableListOfCryptoCoinsUseCase,
    private val savedStateHandle: SavedStateHandle,
    ) : BaseViewModel(true) {

    var uiState by mutableStateOf(UiState())
        private set

    private fun onGetUserInfo() {
        viewModelScope.launch {
            uiState = uiState.copy(
                user = savedStateHandle[USER],
                idBrand = savedStateHandle[ID_BRAND],
            )
        }
    }

    private fun onSetAssetBeforeNavigate(
        marketCryptoCoin: MarketCryptoCoin
    ) {
        uiState = uiState.copy(
            selectedCryptoCoin = marketCryptoCoin
        )
    }

    private fun getAvailableListOfCryptoCoins(
        user: String,
        idBrand: Int
    ) = executeUseCase {
        getAvailableListOfCryptoCoinsUseCase.invoke(user, idBrand)
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
                    onFailure(it)
                }
                result.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
    }

    private fun onGetAvailableListOfCryptoCoins() {
        getAvailableListOfCryptoCoins(
            user = uiState.user ?: "",
            idBrand = uiState.idBrand ?: 0
        )
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is UIEvent.OnGetUserInfo -> onGetUserInfo()
            is UIEvent.OnGetAvailableListOfCryptoCoins -> onGetAvailableListOfCryptoCoins()
        }
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
        val identification: String? = null,
        val isLoading: Boolean = false,
        val selectedCryptoCoin: MarketCryptoCoin? = null,
        val openDialog: DialogParameters = DialogParameters(),
        val availableCryptoCoins: GetListOfAvailableCryptoCoins? = null,
        val cryptoAccounts: List<BalanceCryptoAccountItems> = listOf(),
    )

    sealed class UIEvent {
        object OnGetUserInfo : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnGetAvailableListOfCryptoCoins : UIEvent()
    }
}