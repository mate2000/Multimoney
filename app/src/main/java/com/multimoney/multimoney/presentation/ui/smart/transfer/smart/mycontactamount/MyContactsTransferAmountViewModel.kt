package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontactamount

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue.Expanded
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
class MyContactsTransferAmountViewModel @Inject constructor() : BaseSmartEditAmountViewModel() {
    // stateless
    var fromSmartLabel: Int = R.string.smart_iban_transfer_smart_account_colon
    var totalBalanceLabel: String = ""

    override fun onStart() {
        viewModelScope.launch {
            initializeValues()
            fromSmartLabel = if (originCurrency == CurrencyType.Colon) {
                R.string.smart_iban_transfer_smart_account_colon
            } else {
                R.string.smart_iban_transfer_smart_account_dolar
            }
            totalBalanceLabel = originCurrency?.symbol + smartAccount?.totalBalance.toString()
            getExchangeOnCompleted(
                true,
                abbreviation = originCurrency?.disbursementValue ?: "",
                idOriginCurrency = destinyCurrency?.id.toString(),
                idDestinationCurrency = originCurrency?.id.toString()
            )
        }
    }

    override fun onProcessTransfer() {
        onCallProcessSinpeTransfer(
            originIdentification = identification,
            originAccountNumber = smartAccount?.ibanAccountNumber.orEmpty(),
            originCustomerName = userName,
            originCurrency = originCurrency?.id.toString(),
            destinationCustomerName = phoneAccount?.titular.orEmpty(),
            destinationAccountNumber = phoneAccount?.ibanNumber.orEmpty(),
            destinationCurrency = destinyCurrency?.id.toString(),
            destinationIdentification = phoneAccount?.identification.orEmpty(),
            transferType = SmartSinpeTransferType.SEND
        )
    }

    override fun onAmountCompleted() {
        getExchangeOnCompleted(
            abbreviation = originCurrency?.disbursementValue ?: "",
            idOriginCurrency = destinyCurrency?.id.toString(),
            idDestinationCurrency = originCurrency?.id.toString()
        )
    }

    override fun onContinueClick() {
        val currentAmount = if (shouldDisplayExchange) {
            amountUIState.exchangeConvertedAmount
        } else {
            amountUIState.currentAmountValueString?.toDoubleOrNull() ?: 0.0
        }
        val isValidAmount = currentAmount <= (smartAccount?.totalBalance ?: 0.0)
        amountUIState = if (isValidAmount) {
            amountUIState.copy(
                isAmountValid = true,
                bottomSheetState = ModalBottomSheetState(Expanded)
            )
        } else {
            amountUIState.copy(isAmountValid = false)
        }
    }

    override fun onNavigateBack() {
        navigateBack(popTo = Screen.SmartSelectSendingTypeScreen.route, isRestart = false)
    }
}
