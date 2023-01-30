package com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.amount

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.viewModelScope
import com.multimoney.domain.model.accountsmart.Transfer365Account
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.ACCOUNT_365
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.DisplayAccount
import com.multimoney.multimoney.presentation.util.catalog.SmartTransferTypes
import com.multimoney.multimoney.presentation.util.formatPhoneNumber
import com.multimoney.multimoney.presentation.util.getMaskedSmartAccount
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
            transfer365Account = savedStateHandle[ACCOUNT_365] ?: Transfer365Account()
            totalBalanceLabel =
                amountUIState.currency + smartAccount?.totalBalance.toString()
            val destinationInfo = if (transferType == SmartTransferTypes.SmartToMobile.id) {
                transfer365Account.phone
            } else if (transferType == SmartTransferTypes.SmartToOtherBank.id) {
                "${transfer365Account.bankName} | ${
                    getMaskedSmartAccount(
                        prefix = "",
                        accountNumber = transfer365Account.accountNumber.orEmpty()
                    )
                }"
            } else {
                ""
            }
            amountUIState = amountUIState.copy(
                destinyAccountDisplay = DisplayAccount(
                    sheetLabel = R.string.smart_payment_amount_bottom_sheet_to,
                    sheetTitle = "${transfer365Account.name} ${transfer365Account.lastname}",
                    sheetSubtitle = destinationInfo,
                    icon = R.drawable.ic_bank_account_dollar
                )
            )
        }
    }

    override fun onContinueClick() {
        amountUIState = amountUIState.copy(
            bottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded),
            openDialog = DialogParameters(
                titleResource = R.string.info,
                description = "TBD: Mostrar preconfirmacion REV- 1465"
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
            Screen.SmartOtherBanksAccountScreen.baseRoute -> Screen.SmartOtherBanksAccountScreen.route
            else -> Screen.HomeScreen.route
        }
        navigateBack(popTo = screen, isRestart = false)
    }
}