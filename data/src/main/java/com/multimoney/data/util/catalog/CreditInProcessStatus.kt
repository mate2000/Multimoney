package com.multimoney.data.util.catalog

enum class CreditInProcessStatus(val status: String) {
    OFFER("Oferta PreAprobada"),
    IN_PROCESS("En Proceso")
}

fun getCreditProcessStatus(status: String?) = when (status) {
    CreditInProcessStatus.OFFER.status -> CreditInProcessStatus.OFFER
    CreditInProcessStatus.IN_PROCESS.status -> CreditInProcessStatus.IN_PROCESS
    else -> null
}