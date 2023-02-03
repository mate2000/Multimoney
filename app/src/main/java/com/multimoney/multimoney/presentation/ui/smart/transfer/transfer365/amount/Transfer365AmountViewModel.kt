package com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.amount

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.multimoney.domain.model.accountsmart.Transfer365Account
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.DESTINY_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.ORIGIN_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.DisplayAccount
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.validateDecimalIncome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class Transfer365AmountViewModel @Inject constructor() : BaseSmartEditAmountViewModel() {

    // stateless
    var fromSmartLabel: Int = R.string.transfer_365_amount_from_label
    var totalBalanceLabel: String = ""
    var transfer365Account = Transfer365Account()

    override fun onStart() {
        viewModelScope.launch {
            initializeValues()
            transfer365Account = savedStateHandle[DESTINY_ACCOUNT] ?: Transfer365Account()
            smartAccount = savedStateHandle[ORIGIN_ACCOUNT]
            originCurrency = smartAccount?.currencyID?.getCurrencyFromId() ?: CurrencyType.Dollar
            if (originCurrency == CurrencyType.All) originCurrency = CurrencyType.Dollar
            shouldDisplayExchange = false

            amountUIState = amountUIState.copy(
                originAccountDisplay = DisplayAccount(
                    sheetLabel = R.string.transfer_365_pre_confirmation_from_label,
                    sheetTitleResource = originCurrency?.myAccountSmartName,
                    sheetSubtitleResource = R.string.empty,
                    icon = R.drawable.ic_multimoney_smart
                ),
                currency = originCurrency?.symbol ?: CurrencyType.Dollar.symbol,
                placeholder = if (originCurrency == CurrencyType.Dollar) {
                    R.string.smart_dollar_placeholder
                } else {
                    R.string.empty
                }
            )
            totalBalanceLabel =
                amountUIState.currency + smartAccount?.totalBalance.toString()
        }
    }

    override fun onContinueClick() {
        amountUIState = amountUIState.copy(
            bottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded),
            openDialog = DialogParameters(
                titleResource = R.string.info,
                description = "TBD: Mostrar preconfirmacion REV- 1465",
                isActive = mutableStateOf(true)
            )
        )
    }

    override fun onAmountChanged(newAmount: String) {
        if (validateDecimalIncome(newAmount)) {
            amountUIState = amountUIState.copy(
                currentAmountValueString = newAmount
            )
        }
    }

    override fun onAmountCompleted() {
        val amount = amountUIState.currentAmountValueString?.toDoubleOrNull() ?: 0.0
        amountUIState =
            amountUIState.copy(isAmountValid = amount <= (smartAccount?.totalBalance ?: 0.0))
        amountUIState = amountUIState.copy(enableButton = validateForm())
    }

    override fun validateForm(
        newAmount: String?,
        newMotive: String
    ): Boolean {
        return when {
            newAmount?.isEmpty() == true -> false
            (newAmount?.toDoubleOrNull() ?: 0.0) <= 0.0 -> false
            newMotive.isEmpty() -> false
            amountUIState.isAmountValid.not() -> false
            else -> true
        }
    }

    override fun onProcessTransfer() {
        TODO("Not yet implemented")
    }

    override fun onNavigateBack() {
        // Todo add validation to go back to list transfer 365 accounts screen
        val screen = when (previousScreen) {
            Screen.SmartAdd365AccountScreen.baseRoute -> Screen.SmartAdd365AccountScreen.route
            else -> Screen.HomeScreen.route
        }
        navigateBack(popTo = screen, isRestart = false)
    }
}