package com.multimoney.multimoney.presentation.ui.crypto.wallet

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.crypto.GetHistoricalClientBalanceUseCase
import com.multimoney.domain.model.crypto.HistoricalBalanceClient
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.GLOBAL_CRYPTO_BALANCE
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrentDateYMDPattern
import com.multimoney.multimoney.presentation.util.getPreviousDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class HomeWalletViewModel @Inject constructor(
    private val queryGetHistoricalClientBalanceUseCase: GetHistoricalClientBalanceUseCase,
    private val savedStateHandle: SavedStateHandle
): BaseViewModel(true) {

    var uiState by mutableStateOf(UiState())
        private set

    private fun onGetUserInfo() {
        uiState = uiState.copy(
            user = savedStateHandle[USER] ?: "",
            idBrand = savedStateHandle[ID_BRAND] ?: 0,
            identification = savedStateHandle[IDENTIFICATION] ?: "",
            globalCryptoBalance = savedStateHandle[GLOBAL_CRYPTO_BALANCE] ?: 0.0f
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
            startDate = getPreviousDate(uiState.startDate ?: 1),
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

    private fun onSetDateRange(startDate: Long) {
        uiState = uiState.copy(startDate = startDate)
        callQueryGetHistoricalBalanceUseCase(
            uiState.user ?: "",
            uiState.idBrand ?: 0,
            uiState.identification ?: ""
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
        val identification: String? = null,
        val globalCryptoBalance: Float? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val clientCryptoBalanceHistory: List<HistoricalBalanceClient> = emptyList(),
        val openDialog: DialogParameters = DialogParameters(),
        val startDate: Long? = null,
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnSetDateRange -> onSetDateRange(event.startDate)
            is UIEvent.OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is UIEvent.OnGetUserInfo -> onGetUserInfo()
        }
    }

    sealed interface UIEvent {
        object OnGetUserInfo : UIEvent
        object OnNavigateBack : UIEvent
        data class OnSetDateRange(val startDate: Long): UIEvent
    }
}