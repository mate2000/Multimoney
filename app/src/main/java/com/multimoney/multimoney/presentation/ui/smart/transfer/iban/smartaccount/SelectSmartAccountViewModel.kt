package com.multimoney.multimoney.presentation.ui.smart.transfer.iban.smartaccount

import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.SECOND_SMART_ACCOUNT
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
        val secondAccountSelected: String? = if (secondSmartAccount != null) {
            encodeData(secondSmartAccount)
        } else {
            null
        }
        navigateTo(
            "${Screen.SmartSelectSendingTypeScreen.baseRoute}" +
                    "/$user" +
                    "/$idBrand" +
                    "/$identification" +
                    "/${encodeData(selectedSmartAccount)}" +
                    "/$idClient" +
                    "/${Screen.SmartSelectAccountScreen.baseRoute}" +
                    "?$SECOND_SMART_ACCOUNT=$secondAccountSelected"
        )
    }
}
