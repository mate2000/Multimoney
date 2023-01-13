package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.amount

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.viewModelScope
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class OwnTransferAmountViewModel @Inject constructor() : BaseSmartEditAmountViewModel() {

    // stateless
    var fromSmartLabel: Int = R.string.smart_iban_transfer_smart_account_colon
    var totalBalanceLabel: String = ""

    override fun onStart() {
        viewModelScope.launch {
            initializeValues()
            maskedCardNumber = smartAccount?.ibanAccountNumber ?: ""
            shouldDisplayExchange = true
            fromSmartLabel = if (smartCurrency == CurrencyType.Colon) {
                R.string.smart_iban_transfer_smart_account_colon
            } else {
                R.string.smart_iban_transfer_smart_account_dolar
            }
            baseUIState = baseUIState.copy(
                currency = smartCurrency?.symbol ?: CurrencyType.Dollar.symbol,
                placeholder = if (smartCurrency == CurrencyType.Dollar) {
                    R.string.smart_dollar_placeholder
                } else {
                    R.string.smart_colon_placeholder
                }
            )
            totalBalanceLabel =
                baseUIState.currency + smartAccount?.totalBalance.toString()
            getExchangeOnCompleted(true)
        }
    }

    override fun onProcessTransfer() {
        TODO("Not yet implemented")
    }


    override fun onContinueClick() {
        val isValidAmount = (baseUIState.currentAmountValueString?.toDoubleOrNull() ?: 0.0) <=
                (smartAccount?.totalBalance ?: 0.0)
        baseUIState = if (isValidAmount) {
            baseUIState.copy(
                isAmountValid = true,
                bottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded)
            )
        } else {
            baseUIState.copy(isAmountValid = false)
        }
    }

    private fun onCallProcessSinpeTransfer() {
        TODO("Not yet implemented")
    }

    override fun onRetryTransfer() {
        baseUIState = baseUIState.copy(
            showErrorScreen = false,
            showLoadingScreen = true,
            paymentSuccess = false
        )
        onCallProcessSinpeTransfer()
    }

    override fun onNavigateBack() {
        navigateBack(popTo = Screen.SmartSelectSendingTypeScreen.route, isRestart = false)
    }
}