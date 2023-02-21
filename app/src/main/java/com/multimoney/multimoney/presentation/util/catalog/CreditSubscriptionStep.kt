package com.multimoney.multimoney.presentation.util.catalog

enum class CreditSubscriptionStep(val step: String) {
    LinkGenerated("LINK GENERADO"),
    DocumentsFirmed("DOCUMENTOS FIRMADOS"),
    DocumentsRejected("DOCUMENTOS RECHAZADOS"),
    DocumentsFailed("DOCUMENTOS FALLIDOS"),
    AccountActivated("CUENTA ACTIVADA"),
    ErrorActivatingAccount("ERROR AL ACTIVAR CUENTA"),
}