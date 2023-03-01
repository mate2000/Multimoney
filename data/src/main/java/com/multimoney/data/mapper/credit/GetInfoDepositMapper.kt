package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.GetInfoDepositQuery
import com.multimoney.domain.model.credit.GetInfoDeposit

fun GetInfoDepositQuery.GetInfoDeposit.mapToDomainModel() = GetInfoDeposit(
    accountNumber = numeroCuenta,
    amount = monto.toString(),
    amountLabel = monto_Label,
    referenceNumber = numeroReferencia,
    creditNumber = pagare,
    date = fecha.toString(),
    bankName = bankName,
    message = message,
    status = status,
    detail = detail
)

fun GetInfoDepositQuery.Data.mapToDomainModel() = getInfoDeposit.mapToDomainModel()
