package com.multimoney.data.util.catalog

enum class SendCryptoStep(val step: Int) {
    LIST_CRYPTO_CURRENCIES(0),
    CRYPTO_ADDRESS(1),
    SEND_CRYPTO(2),
    LOADING(3),
    SEND_VOUCHER(4),
    SEND_FAILED(5),
    SEND_ERROR(6)
}