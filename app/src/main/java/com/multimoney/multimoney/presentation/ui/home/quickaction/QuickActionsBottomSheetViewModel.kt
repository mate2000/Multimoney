package com.multimoney.multimoney.presentation.ui.home.quickaction

import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.catalog.QuickActionIconByType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QuickActionsBottomSheetViewModel @Inject constructor() : BaseViewModel(true) {

    fun getSmartQuickAction(label: String, iconId: String): QuickActionDummy {
        return QuickActionDummy(getQuickActionIconByType(iconId), label)
    }

    private fun getQuickActionIconByType(iconId: String): Int? {
        return when (iconId) {
            QuickActionIconByType.DISBURSE_AVAILABLE.iconId -> QuickActionIconByType.DISBURSE_AVAILABLE.iconResource
            QuickActionIconByType.PAY_FEE.iconId -> QuickActionIconByType.PAY_FEE.iconResource
            QuickActionIconByType.ACTIVATE_MM_VISA.iconId -> QuickActionIconByType.ACTIVATE_MM_VISA.iconResource
            QuickActionIconByType.SEE_MM_VISA.iconId -> QuickActionIconByType.SEE_MM_VISA.iconResource
            QuickActionIconByType.PAY_AT_BUSINESS.iconId -> QuickActionIconByType.PAY_AT_BUSINESS.iconResource
            QuickActionIconByType.SAVE_SMART.iconId -> QuickActionIconByType.SAVE_SMART.iconResource
            QuickActionIconByType.SEND_MONEY.iconId -> QuickActionIconByType.SEND_MONEY.iconResource
            QuickActionIconByType.BUY_CRYPTO.iconId -> QuickActionIconByType.BUY_CRYPTO.iconResource
            QuickActionIconByType.SELL_CRYPTO.iconId -> QuickActionIconByType.SELL_CRYPTO.iconResource
            QuickActionIconByType.RECEIVE_CRYPTO.iconId -> QuickActionIconByType.RECEIVE_CRYPTO.iconResource
            QuickActionIconByType.SEND_CRYPTO.iconId -> QuickActionIconByType.SEND_CRYPTO.iconResource
            else -> null
        }
    }
}

data class QuickActionDummy(val icon: Int?, val label: String)
