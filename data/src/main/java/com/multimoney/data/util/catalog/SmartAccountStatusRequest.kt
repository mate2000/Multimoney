package com.multimoney.data.util.catalog

enum class SmartAccountStatusRequest(val status: String) {
    PENDING("SOLICITUD PENDIENTE"),
    SENT("SOLICITUD ENVIADA"),
    CANCELED("SOLICITUD CANCELADA"),
    CREATED("SOLICITUD CREADA")
}