package com.multimoney.data.util.catalog

sealed class UserStatus(val name: String) {
    object Active : UserStatus("Activo")
    object Inactive : UserStatus("Inactivo")
    object Blocked : UserStatus("Bloqueado")
    object PendingInformation : UserStatus("Pendiente Informacion")
    object Defeated : UserStatus("Vencido")
    object Incomplete : UserStatus("Proceso Incompleto")
}
