package com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.accountsmart.MutationProcessSinpeTransferUseCase
import com.multimoney.domain.interaction.accountsmart.QuerySmartExchangeRateUseCase
import com.multimoney.domain.model.util.catalog.SmartSinpeTransferType
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel
import com.multimoney.multimoney.presentation.util.ShareHelper
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
            baseUIState = baseUIState.copy(
                currency = destinationCurrency?.symbol ?: CurrencyType.Dollar.symbol,
                placeholder = if (destinationCurrency == CurrencyType.Dollar) {
                    R.string.smart_dollar_placeholder
                } else {
                    R.string.smart_colon_placeholder
                }
            )
            fromSmartLabel = if (originCurrency == CurrencyType.Colon) {
                R.string.smart_iban_transfer_smart_account_colon
            } else {
                R.string.smart_iban_transfer_smart_account_dolar
            }
            totalBalanceLabel =
                baseUIState.currency + originSmartAccount?.totalBalance.toString()
            getExchangeOnCompleted(
                isStart = true,
                abbreviation = originCurrency?.disbursementValue ?: "",
                idOriginCurrency = destinationCurrency?.id.toString(),
                idDestinationCurrency = originCurrency?.id.toString()
            )
        }
    }

    override fun onAmountCompleted() {
        getExchangeOnCompleted(
            abbreviation = originCurrency?.disbursementValue ?: "",
            idOriginCurrency = destinationCurrency?.id.toString(),
            idDestinationCurrency = originCurrency?.id.toString()
        )
    }

    override fun onContinueClick() {
        val currentAmount = baseUIState.currentAmountValueString?.toDoubleOrNull() ?: 0.0
        val isValidAmount = (currentAmount) <= (originSmartAccount?.totalBalance ?: 0.0)
        baseUIState = if (isValidAmount) {
            baseUIState.copy(
                isAmountValid = true,
                bottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded)
            )
        } else {
            baseUIState.copy(isAmountValid = false)
        }
    }

    override fun onProcessTransfer() {
        onCallProcessSinpeTransfer(
            originIdentification = identification,
            originAccountNumber = originSmartAccount?.ibanAccountNumber ?: "",
            originCustomerName = userName,
            originCurrency = originCurrency?.id.toString(),
            destinationCustomerName = destinationIbanAccount?.nameAccount ?: "",
            destinationAccountNumber = destinationIbanAccount?.sinpeAccount ?: "",
            destinationCurrency = destinationCurrency?.id.toString(),
            transferType = SmartSinpeTransferType.SEND,
            destinationIdentification = destinationIbanAccount?.clientIdentification ?: ""
        )
    }

    override fun onNavigateBack() {
        navigateBack(popTo = Screen.SmartTransferIbanAccountScreen.route, isRestart = false)
    }
}
