package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.credit.apollomodel.type.CuentaDestino
import com.multimoney.domain.model.credit.DestinyAccount

fun DestinyAccount.mapToApolloModel() = CuentaDestino(
    numeroCuentaDestino = destinyAccountNumber,
    idMonedaDestino = destinyCurrencyId,
    montoDestino = destinyAmount ?: 0.0
)
