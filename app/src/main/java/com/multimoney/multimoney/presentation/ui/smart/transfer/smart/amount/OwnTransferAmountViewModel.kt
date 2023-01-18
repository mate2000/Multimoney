package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.amount

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
class OwnTransferAmountViewModel @Inject constructor() : BaseSmartEditAmountViewModel() {

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
            totalBalanceLabel =
                amountUIState.currency + smartAccount?.totalBalance.toString()
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
            originAccountNumber = smartAccount?.ibanAccountNumber ?: "",
            originCustomerName = userName,
            originCurrency = originCurrency?.id.toString(),
            destinationCustomerName = userName,
            destinationAccountNumber = smartDestiny?.ibanAccountNumber ?: "",
            destinationCurrency = destinyCurrency?.id.toString(),
            transferType = SmartSinpeTransferType.SEND,
            destinationIdentification = identification
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
        val isValidAmount =
            (amountUIState.currentAmountValueString?.toDoubleOrNull() ?: 0.0) <= (smartAccount?.totalBalance ?: 0.0)
        amountUIState = if (isValidAmount) {
            amountUIState.copy(
                isAmountValid = true,
                bottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded)
            )
        } else {
            amountUIState.copy(isAmountValid = false)
        }
    }

    override fun onNavigateBack() {
        navigateBack(popTo = Screen.SmartSelectSendingTypeScreen.route, isRestart = false)
    }
}
