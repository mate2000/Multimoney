package com.multimoney.multimoney.presentation.util.catalog

import com.multimoney.multimoney.R

/**
 * @param alternativeValue: Used when the expected value is different from the received value, i.e 'DOLARES' and 'DÓLARES'
 * */
sealed class CurrencyType(
    val id: Int,
    val currency: String,
    val value: String,
    val alternativeValue: String,
    val stringName: String,
    val disbursementValue: String,
    val currencyName: Int,
    val accountIcon: Int,
    val accountTitle: Int,
    val feeIcon: Int,
    val feeInfoButtonTitle: Int,
    val symbol: String,
    val myAccountSmartSymbol: Int,
    val myAccountSmartName: Int
) {
    object Colon : CurrencyType(
        1,
        "01",
        "COLONES",
        "",
        "Colones",
        "CRC",
        R.string.colons,
        R.drawable.ic_bank_account_colon,
        R.string.payment_account_title_colon,
        R.drawable.ic_payment_colon_symbol,
        R.string.payment_fee_one_option,
        "₡",
        R.string.home_my_products_label_smart_colones,
        R.string.payment_select_sending_type_smart_account_colones
    )

    object Dollar : CurrencyType(
        2,
        "02",
        "DÓLARES",
        "DOLARES",
        "Dólares",
        "USD",
        R.string.dollars,
        R.drawable.ic_bank_account_dollar,
        R.string.payment_account_title_dollar,
        R.drawable.ic_payment_dollar_symbol,
        R.string.payment_fee_one_option,
        "$",
        R.string.home_my_products_label_smart,
        R.string.payment_select_sending_type_smart_account_dollars
    )

    object Quetzal : CurrencyType(
        3,
        "03",
        "QUETZALES",
        "",
        "Quetzales",
        "GTQ",
        R.string.quetzales,
        R.drawable.ic_bank_account_dollar,
        R.string.payment_account_title_dollar,
        R.drawable.ic_payment_fee_icon,
        R.string.empty,
        "Q",
        R.string.empty,
        R.string.empty
    )

    object All : CurrencyType(
        100000,
        "",
        "",
        "",
        "",
        "",
        0,
        R.drawable.ic_bank_account_dollar,
        R.string.payment_account_title_all,
        R.drawable.ic_payment_fee_icon,
        R.string.payment_fee_both_options,
        "$",
        R.string.empty,
        R.string.empty
    )
}
