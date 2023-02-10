package com.multimoney.data.util.catalog

enum class BuyCryptoStep(val step: Int) {
    LIST_CRYPTO_CURRENCIES(0),
    SELECT_SMART_ACCOUNT(1),
    BUY_CURRENCY(2),
    LOADING_SCREEN(3),
    PURCHASE_VOUCHER(4),
    PURCHASE_FAILED(5),
}