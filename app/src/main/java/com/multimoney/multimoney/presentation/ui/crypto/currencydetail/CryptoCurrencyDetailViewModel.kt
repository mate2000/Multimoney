package com.multimoney.multimoney.presentation.ui.crypto.currencydetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.crypto.GetCryptoCurrencyHistoryUseCase
import com.multimoney.domain.interaction.crypto.GetCryptoCurrencyMovementsUseCase
import com.multimoney.domain.model.balance.BalanceCryptoAccountItems
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.crypto.HistoricalBalanceClient
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ITEM_CRYPTO_CURRENCY
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrentDateYMDPattern
import com.multimoney.multimoney.presentation.util.getPreviousDate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class CryptoCurrencyDetailViewModel @Inject constructor(
    private val cryptoCurrencyHistoryUseCase: GetCryptoCurrencyHistoryUseCase,
    private val cryptoMovementsUseCase: GetCryptoCurrencyMovementsUseCase,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    var user = ""
    var idBrand = 0
    var identification = ""

    private fun onGetUserInfo() {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        uiState = uiState.copy(balanceItem = savedStateHandle[ITEM_CRYPTO_CURRENCY])
    }

    private fun callQueryAssetHistory() {
        executeUseCase {
            cryptoCurrencyHistoryUseCase(
                idBrand = idBrand,
                user = user,
                market = uiState.balanceItem?.asset.plus(USD_CURRENCY) ?: "",
                maxPoints = MAX_POINTS.toLong(),
                paginationLimit = PAGING_LIMIT,
                paginationOffset = PAGING_OFFSET,
                startDate = getPreviousDate(uiState.startDate ?: 1),
                endDate = getCurrentDateYMDPattern()
            ).collectLatest { result ->
                result.onSuccess {
                    uiState = uiState.copy(
                        historicalBalance = it?.historicalBalanceClient ?: listOf(),
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
            uiState = uiState.copy(isLoading = true)
            cryptoMovementsUseCase(
                user = user,
                idBrand = idBrand,
                identification = identification,
                market = uiState.balanceItem?.asset.plus(USD_CURRENCY),
                startDate = getPreviousDate(uiState.startDate ?: 1),
                endDate = getCurrentDateYMDPattern()
            ).collectLatest { result ->
                result.onSuccess {
                    uiState = uiState.copy(cryptoMovement = it, isLoading = false)
                }
                result.onFailure {
                    onFailure(it)
                }
            }
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

    // UIState
    var uiState by mutableStateOf(UiState())
        private set

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is UIEvent.OnGetUserInfo -> onGetUserInfo()
            is UIEvent.OnGetMovements -> callQueryMovements()
            is UIEvent.OnGetAssetHistory -> callQueryAssetHistory()
            is UIEvent.OnSetDateRange -> onSetDateRange(event.startDate)

        }
    }

    sealed interface UIEvent {
        object OnNavigateBack : UIEvent
        data class OnSetDateRange(val startDate: Long) : UIEvent
        object OnGetUserInfo : UIEvent
        object OnGetMovements : UIEvent
        object OnGetAssetHistory : UIEvent
    }

    data class UiState(
        val startDate: Long? = null,
        val isLoading: Boolean = false,
        val historicalBalance: List<HistoricalBalanceClient> = listOf(),
        val cryptoMovement: CryptoCurrencyMovement? = null,
        val balanceItem: BalanceCryptoAccountItems? = null,
        val openDialog: DialogParameters = DialogParameters()
    )

    companion object {
        const val USD_CURRENCY = "USD"
        const val PAGING_LIMIT = 100
        const val PAGING_OFFSET = 0
        const val MAX_POINTS = 24
    }
}