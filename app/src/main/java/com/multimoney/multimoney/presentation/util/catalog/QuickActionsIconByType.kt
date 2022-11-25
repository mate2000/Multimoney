package com.multimoney.multimoney.presentation.util.catalog

import com.multimoney.multimoney.R

enum class QuickActionIconByType(val iconId: String, val iconResource: Int){
    DISBURSE_AVAILABLE ("1", R.drawable.ic_quick_action_calendar),
    PAY_FEE ("2",R.drawable.ic_quick_action_money),
    ACTIVATE_MM_VISA ("3",R.drawable.ic_quick_action_visa_logo),
    SEE_MM_VISA ("4",R.drawable.ic_quick_action_view),
    PAY_AT_BUSINESS ("5",R.drawable.ic_quick_action_pay),
    SAVE_SMART ("6",R.drawable.ic_saving_smart),
    SEND_MONEY ("7",R.drawable.ic_send_money),
    BUY_CRYPTO ("8",R.drawable.ic_quick_action_buy_crypto),
    SELL_CRYPTO ("9",R.drawable.ic_quick_actionsell_crypto),
    RECEIVE_CRYPTO ("10",R.drawable.ic_quick_action_receive_crypto),
    SEND_CRYPTO ("11",R.drawable.ic_quick_action_send_crypto)
}