package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.amount

import androidx.compose.material.ExperimentalMaterialApi
import androidx.lifecycle.viewModelScope
import com.multimoney.domain.model.util.catalog.SmartSinpeTransferType
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.presentation.navigation.DESTINY_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.ORIGIN_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType.Dollar
import com.multimoney.multimoney.presentation.util.catalog.DisplayAccount
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class OwnTransferAmountViewModel @Inject constructor() : BaseSmartEditAmountViewModel() {

    // stateless
    var fromSmartLabel: Int = R.string.smart_iban_transfer_smart_account_colon

    override fun onStart() {
        viewModelScope.launch {
            initializeValues()
            smartAccount = savedStateHandle[ORIGIN_ACCOUNT]
            smartDestiny = savedStateHandle[DESTINY_ACCOUNT]
            originCurrency = smartAccount?.currencyID?.getCurrencyFromId()
            destinyCurrency = smartDestiny?.currencyID?.getCurrencyFromId()
            shouldDisplayExchange = true
            limits = preferences.getSmartTransferLimit().firstOrNull()

            amountUIState = amountUIState.copy(
                originAccountDisplay = DisplayAccount(
                    sheetTitleResource = originCurrency?.myAccountSmartSymbol,
                    sheetSubtitleResource = originCurrency?.currencyName,
                    icon = drawable.ic_multimoney_smart
                ),
                destinyAccountDisplay = DisplayAccount(
                    sheetTitleResource = destinyCurrency?.myAccountSmartSymbol,
                    sheetSubtitleResource = destinyCurrency?.currencyName,
                    icon = drawable.ic_multimoney_smart
                ),
                currency = destinyCurrency?.symbol ?: Dollar.symbol,
                placeholder = if (destinyCurrency == Dollar) {
                    R.string.smart_dollar_placeholder
                } else {
                    R.string.smart_colon_placeholder
                },
                totalBalance = smartAccount?.totalBalance,
                maxAmount = limits?.find { limit -> limit?.code == destinyCurrency?.id.toString() }?.amount
            )

            fromSmartLabel = if (originCurrency == CurrencyType.Colon) {
                R.string.smart_iban_transfer_smart_account_colon
            } else {
                R.string.smart_iban_transfer_smart_account_dolar
            }
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
        if (shouldDisplayExchange) {
            getExchangeOnCompleted(
                abbreviation = originCurrency?.disbursementValue ?: "",
                idOriginCurrency = destinyCurrency?.id.toString(),
                idDestinationCurrency = originCurrency?.id.toString()
            )
        } else {
            validateAmount()
        }
    }

    override fun onNavigateBack() {
        navigateBack(popTo = Screen.SmartSelectSendingTypeScreen.route, isRestart = false)
    }
}
