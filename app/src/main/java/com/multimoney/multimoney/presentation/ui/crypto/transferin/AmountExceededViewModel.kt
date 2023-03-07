package com.multimoney.multimoney.presentation.ui.crypto.transferin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.crypto.ReleaseTransferUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CRYPTO_ASSET
import com.multimoney.multimoney.presentation.navigation.ID_TRANSACTION
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AmountExceededViewModel @Inject constructor(
    val releaseTransferUseCase: ReleaseTransferUseCase,
    val dataStorePreferences: DataStorePreferences,
    val savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    private var identification: String = ""
    private var user: String = ""
    private var market: String = ""
    private var idTransaction: String = ""

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun onSetUserData() {
        viewModelScope.launch {
            identification = dataStorePreferences.getIdentification().first()
            user = dataStorePreferences.getUserEmail().first()
            market = savedStateHandle.get<String>(CRYPTO_ASSET) ?: ""
            idTransaction = savedStateHandle.get<String>(ID_TRANSACTION) ?: ""
        }
    }

    private fun isFormValid() {
        uiState = uiState.copy(
            isFormValid = when {
                uiState.name.isBlank() -> false
                uiState.platformName.isBlank() -> false
                uiState.reason.isBlank() -> false
                else -> true
            }
        )
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNameChange -> onNameChanged(event.name)
            is UIEvent.OnPlatformNameChange -> onPlatformNameChanged(event.platformName)
            is UIEvent.OnReasonChange -> onReasonChanged(event.reason)
            is UIEvent.OnReleaseDeposit -> onReleaseDeposit()
            is UIEvent.OnNavigateToHome -> navigateToHome()
            is UIEvent.OnCloseAlert -> {
                uiState = uiState.copy(isAlertResultVisible = false)
            }
            is UIEvent.OnSetUserData -> onSetUserData()
            is UIEvent.OnNavigateBack -> onNavigateBack()
        }
    }

    private fun onReleaseDeposit() = executeUseCase {
        uiState = uiState.copy(isLoading = true)
        releaseTransferUseCase(
            identification,
            user,
            market,
            uiState.name,
            uiState.reason,
            uiState.platformName,
            idTransaction
        ).collectLatest { result ->
            uiState = uiState.copy(isLoading = false)
            result.onSuccess {
                it.hasError?.let {
                    uiState = uiState.copy(isAlertResultVisible = true)
                }
                if (it.withHeld) {
                    uiState = uiState.copy(isAmountExceeded = true)
                } else {
                    navigateToHome(true)
                }
            }
            result.onFailure {
                uiState = uiState.copy(isAlertResultVisible = true)
            }
        }
    }

    private fun navigateToHome(showToast: Boolean = false) {
        popAndNavigateTo(
            Screen.HomeScreen.route,
            Screen.ReleaseTransactionScreen.route,
            showToast
        )
    }

    private fun onReasonChanged(reason: String) {
        uiState = uiState.copy(reason = reason)
        isFormValid()
    }

    private fun onPlatformNameChanged(platformName: String) {
        uiState = uiState.copy(platformName = platformName)
        isFormValid()
    }

    private fun onNameChanged(name: String) {
        uiState = uiState.copy(name = name)
        isFormValid()
    }

    private fun onNavigateBack() {
        val previousScreen = savedStateHandle.get<String>(PREVIOUS_SCREEN)
        when (previousScreen) {
            Screen.CryptoHomeAllMovementsScreen.baseRoute -> {
                navigateBack(
                    popTo = Screen.CryptoHomeAllMovementsScreen.route,
                    isRestart = false
                )
            }
            Screen.CryptoCurrencyDetailsAllMovementsScreen.baseRoute -> {
                navigateBack(
                    popTo = Screen.CryptoCurrencyDetailsAllMovementsScreen.route,
                    isRestart = false
                )
            }
            Screen.CryptoWalletDetailsScreen.baseRoute -> {
                navigateBack(
                    popTo = Screen.CryptoWalletDetailsScreen.route,
                    isRestart = false
                )
            }
            else -> {
                navigateBack(
                    popTo = Screen.HomeScreen.route,
                    isRestart = false
                )
            }
        }

    }

    sealed interface UIEvent {
        data class OnNameChange(val name: String) : UIEvent
        data class OnPlatformNameChange(val platformName: String) : UIEvent
        data class OnReasonChange(val reason: String) : UIEvent
        object OnReleaseDeposit : UIEvent
        data class OnNavigateToHome(val showToast: Boolean) : UIEvent
        object OnCloseAlert : UIEvent
        object OnSetUserData : UIEvent
        object OnNavigateBack : UIEvent
    }

    data class UIState(
        val name: String = "",
        val platformName: String = "",
        val reason: String = "",
        val isFormValid: Boolean = false,
        val isAlertResultVisible: Boolean = false,
        val isAmountExceeded: Boolean = false,
        val isLoading: Boolean = false
    )
}