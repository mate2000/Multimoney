package com.multimoney.multimoney.presentation.ui.smart.payment.accounts

import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.accountsmart.SinpeAccount
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.SMART_PAYMENT_ACCOUNTS
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountViewModel.UIEvent.OnAccountClick
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountViewModel.UIEvent.OnAddAccountClick
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountViewModel.UIEvent.OnNavigateBack
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SmartPaymentAccountViewModel @Inject constructor(savedStateHandle: SavedStateHandle) :
    BaseViewModel(true) {

    var sinpeAccountList: List<SinpeAccount>? = null

    init {
        sinpeAccountList =
            savedStateHandle.get<Array<SinpeAccount>>(SMART_PAYMENT_ACCOUNTS)?.toList()
    }

    private fun onAddAccountClick() {
        // TODO Implement add sinpeAccount navigation
    }

    private fun onAccountClick(selectedSinpeAccount: SinpeAccount) {
        // TODO Implement selected sinpeAccount navigation
    }

    private fun onNavigateBack() {
        popAndNavigateTo(
            route = Screen.SmartPaymentOptionsScreen.route,
            popTo = Screen.SmartPaymentAccountScreen.route
        )
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            OnAddAccountClick -> onAddAccountClick()
            is OnNavigateBack -> onNavigateBack()
            is OnAccountClick -> onAccountClick(uiEvent.account)
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnAddAccountClick : UIEvent()
        data class OnAccountClick(val account: SinpeAccount) : UIEvent()
    }
}