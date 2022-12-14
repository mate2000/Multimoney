package com.multimoney.data.util.catalog

enum class SmartOnFidoOrFirmStatus(val status: String) {
    PENDING("PENDIENTE"),
    APPROVED("APROBADO"),
    FIRMED("FIRMADO"),
    REJECTED("RECHAZADO"),
    OVER_COUNTER("EXCEDIO_CONTADOR"),
    FAILED("FALLIDO")
}
