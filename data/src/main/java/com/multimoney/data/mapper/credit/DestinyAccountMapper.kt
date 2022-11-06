package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.type.PaymentAccountsInput
import com.multimoney.domain.model.credit.DestinyAccount

fun DestinyAccount.mapToApolloModel() = PaymentAccountsInput(
    numeroCuentaDestino = destinyAccountNumber,
    idMonedaDestino = destinyCurrencyId,
    montoDestino = destinyAmount ?: 0.0
)
