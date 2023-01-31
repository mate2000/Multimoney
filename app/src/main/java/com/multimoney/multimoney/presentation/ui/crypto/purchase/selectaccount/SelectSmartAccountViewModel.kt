package com.multimoney.multimoney.presentation.ui.crypto.purchase.selectaccount

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
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(true) {
    var uiState by mutableStateOf(UIState())
        private set

    init {
        uiState = uiState.copy(
            accounts = savedStateHandle[SMART_ACCOUNTS_FOR_BUY_CRYPTO] ?: listOf(),
            currency = savedStateHandle[CURRENCY_NAME] ?: ""
        )
    }

    private fun setAccounts(
        list: List<AccountSmartForBuyCrypto>,
        currency: String?,
        currencyDescription: String?
    ) {
        uiState = uiState.copy(
            accounts = list,
            currency = currency ?: "",
            currencyDescription = currencyDescription ?: ""
        )
    }

    data class UIState(
        val accounts: List<AccountSmartForBuyCrypto> = listOf(),
        val currency: String = "",
        val currencyDescription: String = "",
        val isBottomSheetVisible: Boolean = false
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnSetAccounts -> setAccounts(
                event.list,
                event.currency,
                event.currencyDescription
            )
        }
    }

    sealed class UIEvent {
        data class OnSetAccounts(
            val list: List<AccountSmartForBuyCrypto>,
            val currency: String?,
            val currencyDescription: String?
        ) : UIEvent()
    }
}