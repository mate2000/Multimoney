package com.multimoney.data.util.catalog

enum class CreditOnFidoOrFirmStatus(val status: String) {
    NOT_STARTED("SIN_INICIAR"),
    PENDING("PENDIENTE"),
    APPROVED("APROBADO"),
    FIRMED("FIRMADO"),
    REJECTED("RECHAZADO"),
    OVER_COUNTER("EXCEDIO_CONTADOR"),
    FAILED("FALLIDO")
}
