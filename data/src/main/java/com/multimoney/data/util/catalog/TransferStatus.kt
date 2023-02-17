package com.multimoney.data.util.catalog

enum class TransferStatus(val status: Int) {
    IDLE(0),
    LOADING(1),
    SUCCESS(2),
    FAILED(3),
}