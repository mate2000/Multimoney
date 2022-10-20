package com.multimoney.multimoney.presentation.util.catalog

import com.multimoney.multimoney.R

sealed class Currency(
    val id: Int,
    val value: String,
    val accountIcon: Int,
    val accountTitle: Int,
    val feeIcon: Int,
    val feeInfoButtonTitle: Int
) {
    object Colon : Currency(
        1,
        "COLONES",
        R.drawable.ic_account_colon,
        R.string.payment_account_title_colon,
        R.drawable.ic_payment_colon,
        R.string.payment_fee_one_option
    )

    object Dollar : Currency(
        2,
        "DOLARES",
        R.drawable.ic_account_dollar,
        R.string.payment_account_title_dollar,
        R.drawable.ic_payment_dollar,
        R.string.payment_fee_one_option
    )

    object Quetzal : Currency(
        3,
        "QUETZALES",
        R.drawable.ic_account_dollar,
        R.string.payment_account_title_dollar,
        R.drawable.ic_payment_fee_icon,
        R.string.empty
    )

    object All : Currency(
        100000,
        "",
        R.drawable.ic_account_dollar,
        R.string.payment_account_title_all,
        R.drawable.ic_payment_fee_icon,
        R.string.payment_fee_both_options
    )

    object Search {
        fun getCurrencyByIdCurrency(idCurrency: Int?): Currency {
            return when (idCurrency) {
                Colon.id -> Colon
                Dollar.id -> Dollar
                Quetzal.id -> Quetzal
                else -> All
            }
        }

        fun getCurrencyByCurrency(currency: String): Currency {
            return when (currency) {
                Colon.value -> Colon
                Dollar.value -> Dollar
                Quetzal.value -> Quetzal
                else -> All
            }
        }
    }
}
