package com.multimoney.multimoney.presentation.ui.smart.transfer.otherbanks.amount

import androidx.lifecycle.viewModelScope
import com.multimoney.domain.model.accountsmart.Transfer365Account
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.ACCOUNT_365
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class Transfer365AmountViewModel @Inject constructor(): BaseSmartEditAmountViewModel() {

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
        }
    }

    override fun onContinueClick() {
        TODO("Not yet implemented")
    }

    override fun onProcessTransfer() {
        TODO("Not yet implemented")
    }

    override fun onNavigateBack() {
        TODO("Not yet implemented")
    }
}