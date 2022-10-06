package com.multimoney.multimoney.presentation.util.catalog

import com.multimoney.multimoney.R
import java.util.Locale

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
        R.drawable.ic_account_colon,
        R.string.payment_account_title_colon
    )

    object Dollar : Currency(
        2,
        "DOLARES",
        R.drawable.ic_account_dollar,
        R.string.payment_account_title_dollar,
        R.drawable.ic_account_colon,
        R.string.payment_account_title_colon
    )

    object Quetzales : Currency(
        3,
        "QUETZALES",
        R.drawable.ic_account_dollar,
        R.string.payment_account_title_dollar,
        R.drawable.ic_account_colon,
        R.string.payment_account_title_colon
    )

    object All : Currency(
        100000,
        "",
        R.drawable.ic_account_dollar,
        R.string.payment_account_title_all,
        R.drawable.ic_account_colon,
        R.string.payment_account_title_colon
    )

    object Search {
        fun getAccountIconByCurrency(currency: String?): Currency {
            return when (currency?.lowercase(Locale.ROOT)) {
                Colon.value.lowercase() -> Colon
                Dollar.value.lowercase() -> Dollar
                else -> All
            }
        }
    }
}
