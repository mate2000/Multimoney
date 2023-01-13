package com.multimoney.multimoney.presentation.ui.crypto.buycrypto

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.accountsmart.AccountSmartForBuyCrypto
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CURRENCY_NAME
import com.multimoney.multimoney.presentation.navigation.SMART_ACCOUNTS_FOR_BUY_CRYPTO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectSmartAccountViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel(true) {
    var uiState by mutableStateOf(UIState())
        private set

    init {
        uiState = uiState.copy(
            accounts = savedStateHandle[SMART_ACCOUNTS_FOR_BUY_CRYPTO] ?: listOf(),
            currency = savedStateHandle[CURRENCY_NAME] ?: ""
        )
    }

    private fun onNavigateBack() {
        //TODO navigate to home
    }

    private fun onContinueClick() {
        //TODO navigate to next screen
    }

    data class UIState(
        val accounts: List<AccountSmartForBuyCrypto> = listOf(),
        val currency: String = "",
        val isBottomSheetVisible: Boolean = false
    )

    fun onUIEvent(event: SelectSmartAccountViewModel.UIEvent) {
        when (event) {
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnContinueButtonClick -> onContinueClick()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnContinueButtonClick : UIEvent()
    }
}