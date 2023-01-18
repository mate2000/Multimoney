package com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.viewModelScope
import com.multimoney.domain.model.util.catalog.SmartSinpeTransferType
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class SmartTransferAmountViewModel @Inject constructor(): BaseSmartEditAmountViewModel() {

    // stateless
    var fromSmartLabel: Int = R.string.smart_iban_transfer_smart_account_colon
    var totalBalanceLabel: String = ""

    override fun onStart() {
        viewModelScope.launch {
            initializeValues()
            amountUIState = amountUIState.copy(
                currency = ibanCurrency?.symbol ?: CurrencyType.Dollar.symbol,
                placeholder = if (ibanCurrency == CurrencyType.Dollar) {
                    R.string.smart_dollar_placeholder
                } else {
                    R.string.smart_colon_placeholder
                }
            )
            fromSmartLabel = if (smartCurrency == CurrencyType.Colon) {
                R.string.smart_iban_transfer_smart_account_colon
            } else {
                R.string.smart_iban_transfer_smart_account_dolar
            }
            totalBalanceLabel =
                amountUIState.currency + smartAccount?.totalBalance.toString()
            getExchangeOnCompleted(
                isStart = true,
                abbreviation = smartCurrency?.disbursementValue ?: "",
                idOriginCurrency = ibanCurrency?.id.toString(),
                idDestinationCurrency = smartCurrency?.id.toString()
            )
        }
    }

    override fun onAmountCompleted() {
        getExchangeOnCompleted(
            abbreviation = smartCurrency?.disbursementValue ?: "",
            idOriginCurrency = ibanCurrency?.id.toString(),
            idDestinationCurrency = smartCurrency?.id.toString()
        )
    }

    override fun onContinueClick() {
        val currentAmount = amountUIState.exchangeConvertedAmount
        val isValidAmount = (currentAmount) <= (smartAccount?.totalBalance ?: 0.0)
        amountUIState = if (isValidAmount) {
            amountUIState.copy(
                isAmountValid = true,
                bottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded)
            )
        } else {
            amountUIState.copy(isAmountValid = false)
        }
    }

    override fun onProcessTransfer() {
        onCallProcessSinpeTransfer(
            originIdentification = identification,
            originAccountNumber = smartAccount?.ibanAccountNumber ?: "",
            originCustomerName = userName,
            originCurrency = smartCurrency?.id.toString(),
            destinationCustomerName = ibanAccount?.nameAccount ?: "",
            destinationAccountNumber = ibanAccount?.sinpeAccount ?: "",
            destinationCurrency = ibanCurrency?.id.toString(),
            transferType = SmartSinpeTransferType.SEND,
            destinationIdentification = ibanAccount?.clientIdentification ?: ""
        )
    }

    override fun onNavigateBack() {
        navigateBack(popTo = Screen.SmartTransferIbanAccountScreen.route, isRestart = false)
    }
}
