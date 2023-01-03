package com.multimoney.multimoney.presentation.ui.smart.transfer.iban.smartaccount

import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SMART_IDS_LIST
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectSmartAccountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseSelectSmartAccountViewModel(savedStateHandle) {

    override fun onSelectSmartAccount(currencyType: CurrencyType) {
        super.onSelectSmartAccount(currencyType)
        navigateTo(
            "${Screen.SmartSelectSendingTypeScreen.baseRoute}/$user/$idBrand/${identification}/${encodeData(selectedSmartAccount)}"
        )
    }

    override fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnSmartAccountSelected -> onSelectSmartAccount(uiEvent.currencyType)
        }
    }
}