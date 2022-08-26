package com.multimoney.data.util.catalog

enum class CreditOnFidoOrFirmStatus(val status: String) {
    PENDING("PENDIENTE"),
    APPROVED("APROBADO"),
    REJECTED("RECHAZADO"),
    OVER_COUNTER("EXCEDIO_CONTADOR")
}