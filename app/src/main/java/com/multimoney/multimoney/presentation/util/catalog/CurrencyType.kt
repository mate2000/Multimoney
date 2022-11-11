package com.multimoney.multimoney.presentation.util.catalog

import com.multimoney.multimoney.R

sealed class CurrencyType(
    val id: Int,
    val currency: String,
    val value: String,
    val accountIcon: Int,
    val accountTitle: Int,
    val feeIcon: Int,
    val feeInfoButtonTitle: Int,
    val symbol: String
) {
    object Colon : CurrencyType(
        1,
        "01",
        "COLONES",
        R.drawable.ic_bank_account_colon,
        R.string.payment_account_title_colon,
        R.drawable.ic_payment_colon,
        R.string.payment_fee_one_option,
        "₡"
    )

    object Dollar : CurrencyType(
        2,
        "02",
        "DOLARES",
        R.drawable.ic_bank_account_dollar,
        R.string.payment_account_title_dollar,
        R.drawable.ic_payment_dollar,
        R.string.payment_fee_one_option,
        "$"
    )

    object Quetzal : CurrencyType(
        3,
        "03",
        "QUETZALES",
        R.drawable.ic_bank_account_dollar,
        R.string.payment_account_title_dollar,
        R.drawable.ic_payment_fee_icon,
        R.string.empty,
        "$"
    )

    object All : CurrencyType(
        100000,
        "",
        "",
        R.drawable.ic_bank_account_dollar,
        R.string.payment_account_title_all,
        R.drawable.ic_payment_fee_icon,
        R.string.payment_fee_both_options,
        "$"
    )
}
