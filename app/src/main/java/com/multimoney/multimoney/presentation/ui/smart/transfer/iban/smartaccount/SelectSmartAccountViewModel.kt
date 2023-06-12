package com.multimoney.multimoney.presentation.ui.smart.transfer.iban.smartaccount

import androidx.lifecycle.SavedStateHandle
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.smart.common.selectsmartaccount.BaseSelectSmartAccountViewModel
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectSmartAccountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseSelectSmartAccountViewModel(savedStateHandle) {

    init {
        uiState = uiState.copy(
            screenTitle = R.string.smart_iban_transfer_select_smart_account_title
        )
    }

    override fun onSelectSmartAccount(currencyType: CurrencyType) {
        super.onSelectSmartAccount(currencyType)
        navigateTo(
            "${Screen.SmartSelectSendingTypeScreen.baseRoute}/$user/$idBrand/$identification/${encodeData(selectedSmartAccount)}/${encodeData(secondSmartAccount)}/$idClient/${Screen.SmartSelectAccountScreen.baseRoute}"
        )
    }
}