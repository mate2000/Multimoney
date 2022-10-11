package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.credit.apollomodel.GetClientBankAccountQuery
import com.multimoney.domain.model.credit.ClientBankAccount

private fun GetClientBankAccountQuery.GetClientBankAccount.mapToDomainModel() = ClientBankAccount(
    id = id.toString().toInt(),
    idBank = id_Banco,
    bankDescription = descripcion_banco,
    accountNumber = numeroCuenta,
    idCurrency = id_Moneda,
    idCurrencyDestination = id_Moneda_Destino
)

fun GetClientBankAccountQuery.Data.mapToDomainModel() = getClientBankAccount?.map { it?.mapToDomainModel() }
