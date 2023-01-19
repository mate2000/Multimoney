package com.multimoney.multimoney.presentation.ui.crypto.currencydetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.multimoney.domain.interaction.crypto.GetCryptoCurrencyMovementsUseCase
import com.multimoney.domain.interaction.crypto.GetCurrencyHistoricalPricesUseCase
import com.multimoney.domain.model.balance.BalanceCryptoAccountItems
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.crypto.CurrencyHistoricPrice
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CRYPTO_ASSET
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ITEM_CRYPTO_CURRENCY
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.util.FilterDateByDays
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrentDateYMDPattern
import com.multimoney.multimoney.presentation.util.getPreviousDate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf

@HiltViewModel
class CryptoCurrencyMovementsViewModel @Inject constructor(
    private val queryGetCurrencyHistoricalPricesUseCase: GetCurrencyHistoricalPricesUseCase,
    private val cryptoMovementsUseCase: GetCryptoCurrencyMovementsUseCase,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    private var user = ""
    private var idBrand = 0
    private var identification = ""

    // UIState
    var uiState by mutableStateOf(UiState())
        private set

    private fun onGetUserInfo() {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        uiState = uiState.copy(cryptoItem = savedStateHandle[ITEM_CRYPTO_CURRENCY], idBrand = idBrand)
    }

    private fun callQueryAssetHistory() {
        executeUseCase {
            queryGetCurrencyHistoricalPricesUseCase(
                idBrand = idBrand,
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
                    idBrand = idBrand,
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

    private fun onNavigateToAllMovements() {
        navigateTo(
            "${Screen.CryptoMovementsAllScreen.baseRoute}/${idBrand}/$identification/$user/${Screen.CryptoCurrencyMovementsScreen.baseRoute}?$CRYPTO_ASSET=${uiState.cryptoItem?.asset}",
        )
    }

    private fun onNavigateToBuyCrypto(){
        navigateTo("${Screen.CryptoPurchaseListScreen.baseRoute}/$user/${uiState.idBrand}")
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is UIEvent.OnGetUserInfo -> onGetUserInfo()
            is UIEvent.OnGetMovements -> callQueryMovements()
            is UIEvent.OnGetAssetHistory -> callQueryAssetHistory()
            is UIEvent.OnSetDateRange -> onSetDateRange(event.startDate)
            is UIEvent.OnViewAllMovements -> onNavigateToAllMovements()
            is UIEvent.OnNavigateToBuyCrypto -> onNavigateToBuyCrypto()
        }
    }

    sealed interface UIEvent {
        object OnNavigateBack : UIEvent
        data class OnSetDateRange(val startDate: Long) : UIEvent
        object OnGetUserInfo : UIEvent
        object OnGetMovements : UIEvent
        object OnGetAssetHistory : UIEvent
        object OnViewAllMovements : UIEvent
        object OnNavigateToBuyCrypto : UIEvent
    }

    data class UiState(
        val idBrand: Int = 0,
        val startDate: Long? = null,
        val isLoading: Boolean = false,
        val historicalBalance: List<CurrencyHistoricPrice> = listOf(),
        val cryptoMovements: Flow<PagingData<CryptoCurrencyMovement>> = flowOf(),
        val cryptoItem: BalanceCryptoAccountItems? = null,
        val openDialog: DialogParameters = DialogParameters()
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