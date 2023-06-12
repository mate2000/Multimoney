package com.multimoney.data.util.catalog

sealed class SmartStatus(val id: Int, val name: String) {
    object One : SmartStatus(0, "SOLICITUD PENDIENTE")
    object Two : SmartStatus(1, "SOLICITUD CREADA")

    object Search {
        fun getIdByName(name: String?) = when (name) {
            One.name -> One.id
            Two.name -> Two.id
            else -> One.id
        }
    }
}
