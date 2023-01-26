package com.multimoney.multimoney.presentation.ui.crypto.movements

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.multimoney.domain.interaction.crypto.GetCryptoCurrencyMovementsUseCase
import com.multimoney.domain.model.balance.BalanceCryptoAccountItems
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CRYPTO_ASSET
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ITEM_CRYPTO_CURRENCY
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.crypto.currencydetail.CryptoCurrencyMovementsViewModel.Companion.USD_CURRENCY
import com.multimoney.multimoney.presentation.util.FilterDate
import com.multimoney.multimoney.presentation.util.PAGE_SIZE
import com.multimoney.multimoney.presentation.util.getCurrentDateYMDPattern
import com.multimoney.multimoney.presentation.util.getPreviousDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class CryptoMovementsAllViewModel  @Inject constructor(
    private val queryGetCryptoCurrencyMovementsUseCase: GetCryptoCurrencyMovementsUseCase,
    private val savedStateHandle: SavedStateHandle
): BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    private fun setUserData() {
        uiState = uiState.copy(
            cryptoItem = savedStateHandle[CRYPTO_ASSET],
            user = savedStateHandle[USER],
            idBrand = savedStateHandle[ID_BRAND],
            identification = savedStateHandle[IDENTIFICATION]
        )
    }

    private fun getCryptoMovements() = executeUseCase {
        val market: String = if (uiState.cryptoItem?.isNotEmpty() == true)
            uiState.cryptoItem.plus(USD_CURRENCY) else EMPTY_STRING
        uiState = uiState.copy(
            cryptoMovements = queryGetCryptoCurrencyMovementsUseCase.invoke(
                user = uiState.user ?: "",
                idBrand = uiState.idBrand ?: 0,
                identification = uiState.identification ?: "",
                market = market,
                order_time_begin = getPreviousDate(FilterDate.LAST_365_DAYS),
                order_time_end = getCurrentDateYMDPattern(),
                pagination_limit = PAGE_SIZE
            ).cachedIn(viewModelScope)
        )
    }

    data class UIState(
        val cryptoItem: String? = null,
        val user: String? = null,
        val idBrand: Int? = null,
        val identification: String? = null,
        val isLoading: Boolean = false,
        val cryptoMovements: Flow<PagingData<CryptoCurrencyMovement>> = flowOf()
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is UIEvent.OnGetUserInfo -> setUserData()
            is UIEvent.GetCryptoMovements -> getCryptoMovements()
        }
    }

    sealed interface UIEvent {
        object OnGetUserInfo : UIEvent
        object OnNavigateBack : UIEvent
        object GetCryptoMovements: UIEvent
    }

    companion object {
        private const val EMPTY_STRING = ""
    }
}