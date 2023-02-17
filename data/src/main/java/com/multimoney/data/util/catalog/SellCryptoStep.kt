package com.multimoney.data.util.catalog

enum class SellCryptoStep(val step: Int) {
    LIST_CRYPTO_CURRENCIES(0),
    SELECT_SMART_ACCOUNT(1),
    SELL_CRYPTO(2),
    LOADING_SCREEN(3),
    SELL_VOUCHER(4),
    PURCHASE_FAILED(5),
}