package com.multimoney.data.util.catalog

sealed class CreditProcessStatusOnFido(val status: String) {
    object Approved : CreditProcessStatusOnFido("APROBADO")
}