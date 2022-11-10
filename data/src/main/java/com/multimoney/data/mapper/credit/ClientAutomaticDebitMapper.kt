package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.GetClientAutomaticDebitQuery
import com.multimoney.domain.model.credit.ClientBankAccount

private fun GetClientAutomaticDebitQuery.GetClientAutomaticDebit.mapToDomainModel() = ClientBankAccount(
    id = id.toString().toInt(),
    idBank = id_Banco,
    origin = origen,
    debitDate = fechaDebito,
    bankDescription = descripcion_banco,
    accountNumber = numeroCuenta,
    idCurrency = id_Moneda,
    idCurrencyDestination = id_Moneda_Destino
)

fun GetClientAutomaticDebitQuery.Data.mapToDomainModel() = getClientAutomaticDebit.map { it?.mapToDomainModel() }
