package com.multimoney.multimoney.presentation.util.catalog

sealed class CardType(val status: String, val blockType: String, val observation: String, val source: String) {
    object Blocked : CardType(
        status = "BLOQUEADA",
        blockType = "PB",
        observation = "Bloqueo Preventivo",
        source = "APP"

    )

    object Unblocked : CardType(
        status = "ACTIVO",
        blockType = "",
        observation = "Desbloqueo Preventivo",
        source = "APP"

    )
}
