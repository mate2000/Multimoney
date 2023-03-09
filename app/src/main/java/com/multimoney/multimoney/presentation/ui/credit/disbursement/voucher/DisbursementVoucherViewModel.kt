package com.multimoney.multimoney.presentation.ui.credit.disbursement.voucher

import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.AMOUNT_ORIGINAL_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.CLIENT_BANK_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.DISBURSEMENT_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.EXCHANGE_RATE_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.REFERENCE_NUMBER
import com.multimoney.multimoney.presentation.navigation.navgraph.SHOULD_DISPLAY_EXCHANGE_RATE
import com.multimoney.multimoney.presentation.ui.credit.disbursement.voucher.DisbursementVoucherViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.disbursement.voucher.DisbursementVoucherViewModel.UIEvent.OnSharedVoucherImage
import com.multimoney.multimoney.presentation.util.ShareHelper
import com.multimoney.multimoney.presentation.util.getCurrentDate
import com.multimoney.multimoney.presentation.util.getCurrentTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DisbursementVoucherViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val shareHelper: ShareHelper,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var reference: String? = null
    var clientBankAccount: ClientBankAccount? = null
    var currentDate: String = ""
    var currentTime: String = ""
    var exchangeRateLabel: String? = null
    var amountInCurrencyLabel: String? = null
    var disbursementLabel: String? = null
    var shouldDisplayExchangeRate: Boolean? = null

    init {
        reference = savedStateHandle[REFERENCE_NUMBER] ?: ""
        clientBankAccount = savedStateHandle[CLIENT_BANK_ACCOUNT]
        exchangeRateLabel = savedStateHandle[EXCHANGE_RATE_LABEL]
        disbursementLabel = savedStateHandle[DISBURSEMENT_LABEL]
        shouldDisplayExchangeRate = savedStateHandle[SHOULD_DISPLAY_EXCHANGE_RATE]
        amountInCurrencyLabel = savedStateHandle[AMOUNT_ORIGINAL_LABEL]
        currentDate = getCurrentDate(Calendar.getInstance().time)
        currentTime = getCurrentTime(Calendar.getInstance().time)
    }

    private fun onShareVoucherImage(
        view: View,
        capturingBounds: Rect
    ) {
        shareHelper.sharedScreenShot(view, capturingBounds)
    }

    private fun onNavigateToHome() {
        restartMetricsPreferences()
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = true)
    }

    private fun restartMetricsPreferences() {
        viewModelScope.launch {
            dataStorePreferences.isAdjustFirstDisbursementEventRegister(true)
            dataStorePreferences.isAdjustFirstDisbursementSuccessEventRegister(true)
        }
    }

    data class UIState(
        val accountCurrency: String = "$",
        val currentAmountValueString: String = "0",
        val currentAmountError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
        val enableButton: Boolean = false,
        val isAmountVisible: Boolean = true,
        val isAlertResultVisible: Boolean = false,
        val alertResultTitle: String = "",
        val alertResultDescription: String = "",
        val isLoading: Boolean = false
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnCloseClick -> onNavigateToHome()
            is OnSharedVoucherImage -> onShareVoucherImage(event.view, event.capturingBounds)
        }
    }

    sealed class UIEvent {
        object OnCloseClick : UIEvent()
        data class OnSharedVoucherImage(
            val view: View,
            val capturingBounds: Rect
        ) : UIEvent()
    }
}
