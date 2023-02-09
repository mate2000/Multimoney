package com.multimoney.multimoney.presentation.ui.crypto.purchase.selectaccount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.model.accountsmart.AccountSmartForBuyCrypto
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CURRENCY_NAME
import com.multimoney.multimoney.presentation.navigation.SMART_ACCOUNTS_FOR_BUY_CRYPTO
import com.multimoney.multimoney.presentation.ui.crypto.purchase.PurchaseCryptoSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectSmartAccountViewModel @Inject constructor(
    val dataStorePreferences: DataStorePreferences,
) : BaseViewModel(true) {
    var uiState by mutableStateOf(UIState())
        private set

    private fun setAccounts(
        currency: String?,
        currencyDescription: String?
    ) {
        uiState = uiState.copy(
            currency = currency ?: "",
            currencyDescription = currencyDescription ?: ""
        )
    }

    private fun updateValues(token: String, balance: Double) {
        uiState = uiState.copy(accountToken = token, accountBalance = balance)
    }

    data class UIState(
        val currency: String = "",
        val currencyDescription: String = "",
        val isBottomSheetVisible: Boolean = false,
        val accountToken: String = "",
        val accountBalance: Double = 0.0,

    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnSetAccounts -> setAccounts(
                event.currency,
                event.currencyDescription
            )
            is UIEvent.OnUpdateValues -> updateValues(event.accountToken,event.accountBalance)
        }
    }

    sealed class UIEvent {
        data class OnSetAccounts(
            val currency: String?,
            val currencyDescription: String?
        ) : UIEvent()
        data class OnUpdateValues(
            val accountToken: String,
            val accountBalance: Double
        ) : UIEvent()
    }
}