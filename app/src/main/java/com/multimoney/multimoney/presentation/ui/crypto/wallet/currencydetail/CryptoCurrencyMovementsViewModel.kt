@file:OptIn(ExperimentalMaterialApi::class)

package com.multimoney.multimoney.presentation.ui.crypto.wallet.currencydetail

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.crypto.GetCryptoCurrencyMovementsUseCase
import com.multimoney.domain.interaction.crypto.GetCurrencyHistoricalPricesUseCase
import com.multimoney.domain.model.balance.BalanceCryptoAccountItems
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.crypto.CurrencyHistoricPrice
import com.multimoney.domain.model.crypto.MarketCryptoCoin
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CRYPTO_ASSET
import com.multimoney.multimoney.presentation.navigation.DESCRIPTION_CURRENCY
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ITEM_CRYPTO_CURRENCY
import com.multimoney.multimoney.presentation.navigation.navgraph.ITEM_CRYPTO_MARKET
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.crypto.market.currencydetails.MarketCurrencyDetailsViewModel
import com.multimoney.multimoney.presentation.util.FilterDateByDays
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrentDateYMDPattern
import com.multimoney.multimoney.presentation.util.getPreviousDate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@HiltViewModel
class CryptoCurrencyMovementsViewModel @Inject constructor(
    private val queryGetCurrencyHistoricalPricesUseCase: GetCurrencyHistoricalPricesUseCase,
    private val cryptoMovementsUseCase: GetCryptoCurrencyMovementsUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(true) {

    private var user = ""
    private var identification = ""

    // UIState
    var uiState by mutableStateOf(UiState())
        private set

    private fun onGetUserInfo() {
        user = savedStateHandle[USER] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        uiState = uiState.copy(
            cryptoItem = savedStateHandle[ITEM_CRYPTO_CURRENCY],
            idBrand = savedStateHandle[ID_BRAND] ?: 0
        )
        viewModelScope.launch {
            uiState = uiState.copy(shouldDisplayDisclaimer = dataStorePreferences.isVolatileDialogVisible().first())
        }
    }

    private fun callQueryAssetHistory() {
        executeUseCase {
            queryGetCurrencyHistoricalPricesUseCase(
                idBrand = uiState.idBrand ?: 0,
                user = user,
                market = uiState.cryptoItem?.asset.plus(USD_CURRENCY),
                max_data_points = MAX_POINTS.toLong(),
                pagination_limit = PAGING_LIMIT,
                pagination_offset = PAGING_OFFSET,
                range_begin = getPreviousDate(uiState.startDate ?: FilterDateByDays.YESTERDAY.time),
                range_end = getCurrentDateYMDPattern()
            ).collectLatest { result ->
                result.onSuccess {
                    uiState = uiState.copy(
                        historicalBalance = it.cryptoHistoricalPrice.items,
                        isLoading = false
                    )
                }
                result.onFailure {
                    onFailure(it)
                }
            }
        }
    }

    private fun callQueryMovements() {
        executeUseCase {
            uiState = uiState.copy(
                cryptoMovements = cryptoMovementsUseCase(
                    user = user,
                    idBrand = uiState.idBrand ?: 0,
                    identification = identification,
                    market = uiState.cryptoItem?.asset.plus(USD_CURRENCY),
                    order_time_begin = getPreviousDate(uiState.startDate ?: FilterDateByDays.YESTERDAY.time),
                    order_time_end = getCurrentDateYMDPattern(),
                    pagination_limit = SINGLE_PAGE
                )
            )
        }
    }

    private fun onSetDateRange(startDate: Long) {
        uiState = uiState.copy(startDate = startDate)
        callQueryMovements()
        callQueryAssetHistory()
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

    private fun onDisclaimerChecked(checked: Boolean) {
        uiState = uiState.copy(dontShowAgainChecked = checked)
    }

    private fun updateShouldShowDisclaimer(value: Boolean) {
        viewModelScope.launch {
            dataStorePreferences.setVolatileDialogVisible(!value)
            uiState = uiState.copy(
                shouldDisplayDisclaimer = preferences.isVolatileDialogVisible().first()
            )
        }
    }

    private fun onNavigateToAllMovements() {
        navigateTo(
            "${Screen.CryptoMovementsAllScreen.baseRoute}/${uiState.idBrand}/$identification/$user?$CRYPTO_ASSET=${uiState.cryptoItem?.asset}"
        )
    }

    private fun onNavigateToSelectAccount(){
        navigateTo("${Screen.PurchaseCryptoFlow.baseRoute}?$ITEM_CRYPTO_MARKET=${encodeData(MarketCryptoCoin(
            description = uiState.cryptoItem?.descriptionCurrency ?: "",
            baseAsset = uiState.cryptoItem?.asset ?: "",
            url_image = uiState.cryptoItem?.url_image ?: "",
            cryptoNetwork = uiState.cryptoItem?.cryptoNetwork ?: ""
        ))}")
    }

    private fun onNavigateToSellCrypto(){
        navigateTo("${Screen.CryptoSellFlow.baseRoute}?$ITEM_CRYPTO_MARKET=${encodeData(MarketCryptoCoin(
            description = uiState.cryptoItem?.descriptionCurrency ?: "",
            baseAsset = uiState.cryptoItem?.asset ?: "",
            url_image = uiState.cryptoItem?.url_image ?: "",
            cryptoNetwork = uiState.cryptoItem?.cryptoNetwork ?: "",
        ))}")
    }

    private fun onNavigateToSendCrypto() {
        navigateTo("${Screen.CryptoSendFlow.baseRoute}?$CRYPTO_ASSET=${uiState.cryptoItem?.asset}&$DESCRIPTION_CURRENCY=${uiState.cryptoItem?.descriptionCurrency}")
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.CryptoWalletScreen.route, false)
            is UIEvent.OnGetUserInfo -> onGetUserInfo()
            is UIEvent.OnGetMovements -> callQueryMovements()
            is UIEvent.OnGetAssetHistory -> callQueryAssetHistory()
            is UIEvent.OnSetDateRange -> onSetDateRange(event.startDate)
            is UIEvent.OnViewAllMovements -> onNavigateToAllMovements()
            is UIEvent.OnNavigateToSelectAccount -> onNavigateToSelectAccount()
            is UIEvent.OnDisclaimerChecked -> onDisclaimerChecked(event.checked)
            is UIEvent.OnUpdateShouldShowDisclaimer -> updateShouldShowDisclaimer(event.checked)
            is UIEvent.OnShowDisclaimer -> uiState = uiState.copy(bottomSheetVisibleState = ModalBottomSheetState(ModalBottomSheetValue.Expanded))
            is UIEvent.OnHideDisclaimer -> uiState = uiState.copy(bottomSheetVisibleState = ModalBottomSheetState(ModalBottomSheetValue.Hidden))
            is UIEvent.OnNavigateToSendCrypto -> onNavigateToSendCrypto()
            is UIEvent.OnNavigateToSellCrypto -> onNavigateToSellCrypto()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        data class OnSetDateRange(val startDate: Long) : UIEvent()
        object OnGetUserInfo : UIEvent()
        object OnGetMovements : UIEvent()
        object OnGetAssetHistory : UIEvent()
        object OnViewAllMovements : UIEvent()
        object OnNavigateToSelectAccount : UIEvent()
        object OnNavigateToSellCrypto : UIEvent()
        data class OnDisclaimerChecked(val checked: Boolean) : UIEvent()
        data class OnUpdateShouldShowDisclaimer(val checked: Boolean) : UIEvent()
        object OnShowDisclaimer : UIEvent()
        object OnHideDisclaimer : UIEvent()
        object OnNavigateToSendCrypto : UIEvent()
    }


    data class UiState(
        val startDate: Long? = null,
        val isLoading: Boolean = false,
        val historicalBalance: List<CurrencyHistoricPrice> = listOf(),
        val cryptoMovements: Flow<PagingData<CryptoCurrencyMovement>> = flowOf(),
        val cryptoItem: BalanceCryptoAccountItems? = null,
        val openDialog: DialogParameters = DialogParameters(),
        val idBrand: Int? = null,
        val shouldDisplayDisclaimer: Boolean = true,
        val dontShowAgainChecked: Boolean = false,
        val bottomSheetVisibleState: ModalBottomSheetState = ModalBottomSheetState(
            ModalBottomSheetValue.Hidden)
    )

    companion object {
        const val USD_CURRENCY = "USD"
        const val TODAY_TEXT = "Hoy"
        const val PAGING_LIMIT = 100
        const val PAGING_OFFSET = 0
        const val MAX_POINTS = 24
        const val SINGLE_PAGE = 1
    }
}
