package com.multimoney.multimoney.presentation.ui.crypto.wallet

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.multimoney.domain.interaction.crypto.GetHistoricalClientBalanceUseCase
import com.multimoney.domain.model.accountsmart.AccountSmartData
import com.multimoney.domain.model.crypto.HistoricalBalanceClient
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.navgraph.COMING_FROM_CRYPTO
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.util.FilterDateByDays
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrentDateYMDPattern
import com.multimoney.multimoney.presentation.util.getPreviousDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class HomeWalletViewModel @Inject constructor(
    private val queryGetHistoricalClientBalanceUseCase: GetHistoricalClientBalanceUseCase,
    savedStateHandle: SavedStateHandle
): BaseViewModel(shouldObserveToken = true) {

    // bundle parameters
    val user = savedStateHandle[USER] ?: ""
    val idBrand = savedStateHandle[ID_BRAND] ?: ""
    val idBrandAsInt = idBrand.toIntOrNull() ?: SmartViewModel.DEFAULT_ID_BRAND_ERROR
    val identification: String = savedStateHandle[IDENTIFICATION] ?: ""

    var uiState by mutableStateOf(UiState())

    init {
        callQueryGetHistoricalBalanceUseCase(
            user = user,
            idBrand = idBrandAsInt,
            identification = identification
        )
    }

    private fun callQueryGetHistoricalBalanceUseCase(
        user: String,
        idBrand: Int,
        identification: String,
        baseAsset: String = ""
    ) = executeUseCase {
        queryGetHistoricalClientBalanceUseCase.invoke(
            user,
            idBrand,
            identification,
            baseAsset = baseAsset,
            startDate = getPreviousDate(uiState.startDate),
            endDate = getCurrentDateYMDPattern()
        ).collectLatest { result ->
            result.onSuccess { historicBalance ->
                historicBalance?.let {
                    uiState = uiState.copy(
                        isLoading = false,
                        clientCryptoBalanceHistory = it.historicalBalanceClient
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

    // todo function to navigate to coins details
    private fun onNavigateToCoinsDetails() {}

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
        val isLoading: Boolean = false,
        val error: String? = null,
        val clientCryptoBalanceHistory: List<HistoricalBalanceClient> = emptyList(),
        val openDialog: DialogParameters = DialogParameters(),
        val startDate: Long = FilterDateByDays.YESTERDAY.days,
    )

    fun onUiEvent(event: UiEvent) {
        when (event) {
            is UiEvent.OnSetDateRange -> uiState.copy(startDate = event.startDate)
        }
    }

    sealed interface UiEvent {
        data class OnSetDateRange(val startDate: Long): UiEvent
    }
}